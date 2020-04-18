package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import opennlp.tools.tokenize.SimpleTokenizer;

public class Crawler {

	private String directory;
	private Filter f;
	private static Integer nbDoc;

	public Crawler(String d, Filter f) throws IOException {
		this.f = f;
		this.directory = d;
		this.nbDoc = 0;
	}

	public static void setNbDoc() {
		nbDoc = nbDoc + 1;
	}

	public Index retIndex() throws IOException {
		String[] a = this.change(this.crawl(this.directory));

		return new Index(this.litDirectory(a));
	}

	public Set<Path> crawl(String doc) throws IOException {
		Stream<Path> paths = Files.walk(Paths.get(doc + "/"));

		return paths.filter(Files::isRegularFile).collect(Collectors.toSet());
	}

	public String[] change(Set<Path> liste) {
		int n = liste.size();
		String arr[] = new String[n];
		int i = 0;

		for (Path x : liste)
			arr[i++] = x.toString();

		return arr;
	}

	public static List<Doc> litDirectory(String[] set) {
		List<Doc> listeIndex = new ArrayList<Doc>();
		double IDF = 0.0;

		for (int i = 0; i < set.length; i++) {
			setNbDoc();
			Doc d = new Doc();
			attributes(set[i], d);
			listeIndex.add(d);
		}
		
		for (Doc doc : listeIndex) {
			for (String word : doc.getFrequencies().keySet()) {
				IDF = doc.getFrequencies().get(word).IDF(listeIndex, word);
				doc.getFrequencies().get(word).setTFIDF(doc.getFrequencies().get(word).getFrequency(), IDF);
			}
		}

		return listeIndex;
	}

	public String readFile(String filename) {
		Set<String> records = new HashSet<String>();
		String texte = "";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				records.add(line);
				texte += line.toLowerCase() + " ";
			}
			reader.close();
			return texte;
		} catch (Exception e) {
			System.err.format("Exception occurred trying to read '%s'.", filename);
			e.printStackTrace();
			return null;
		}
	}

	public static void attributes(String pathfile, Doc d) {
		SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(pathfile);

		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			if (rootNode.getChild("DOCNO") != null) {
				d.setName(rootNode.getChildTextTrim("DOCNO"));
			}
			else {
				d.setName("Nom de document inexistant");
			}
			/*
			 * DATE
			 */
			if (rootNode.getChild("DATE_TIME") != null) {
				d.setDate(rootNode.getChildTextTrim("DATE_TIME"));
			} else {
				d.setDate(rootNode.getChildTextTrim("DATE_TIME"));
			}

			/*
			 * IF BODY
			 */
			if(rootNode.getChild("BODY") != null) {
				Element nodeB = rootNode.getChild("BODY");
				/*
				 * TITRE
				 */
				if (nodeB.getChild("HEADLINE") != null) {
					d.setTitle(nodeB.getChildTextTrim("HEADLINE"));
				} else {
					d.setTitle(nodeB.getChildTextTrim("HEADER"));
				}

				try {
					String tokenT[] = simpleTokenizer.tokenize(d.getTitle().trim());

					// on split le titre et récupère chaque mot ainsi que son énumération
					for (int i = 0; i < tokenT.length; i++) {
						if (d.getFrequencies().containsKey(tokenT[i].toLowerCase())) {
							d.getFrequencies().get(tokenT[i].toLowerCase()).updateOccurence();
						} else {
							d.getFrequencies().put(tokenT[i].toLowerCase(), new Frequency());
						}
					}
				} catch (Exception e) {
				}

				/*
				 * TEXT
				 */
				try {
					String addText = "";
					Element nodeT = nodeB.getChild("TEXT");
					List<Element> pList = nodeT.getChildren();
					for (int i = 0; i < pList.size(); i++) {
						// ajoute le texte Ã  une variable qui implÃ©mentera le texte de chaque doc
						// respectif
						addText += pList.get(i).getText().toLowerCase();

						String p = pList.get(i).getTextTrim();
						String tokenP[] = simpleTokenizer.tokenize(p);

						for (int j = 0; j < tokenP.length; j++) {
							if (d.getFrequencies().containsKey(tokenP[j].toLowerCase())) {
								d.getFrequencies().get(tokenP[j].toLowerCase()).updateOccurence();
							} else {
								d.getFrequencies().put(tokenP[j].toLowerCase(), new Frequency());
							}
						}
					}
					// ajoute le texte Ã  l'attribut du doc
					d.setText(addText);
				} catch (Exception e) {}
				
			}
			
			/*
			 * SI LE BODY N'EXISTE PAS
			 */
			if(rootNode.getChild("BODY") == null) {
				/*
				 * TITRE
				 */
				if (rootNode.getChild("HEADLINE") != null) {
					d.setTitle(rootNode.getChildTextTrim("HEADLINE"));
				} else {
					d.setTitle(rootNode.getChildTextTrim("HEADER"));
				}

				try {
					String tokenT[] = simpleTokenizer.tokenize(d.getTitle().trim());

					// on split le titre et récupère chaque mot ainsi que son énumération
					for (int i = 0; i < tokenT.length; i++) {
						if (d.getFrequencies().containsKey(tokenT[i].toLowerCase())) {
							d.getFrequencies().get(tokenT[i].toLowerCase()).updateOccurence();
						} else {
							d.getFrequencies().put(tokenT[i].toLowerCase(), new Frequency());
						}
					}
				} catch (Exception e) {	}
				/*
				 * TEXT
				 */
				try {
					String addText = "";
					Element nodeT = rootNode.getChild("TEXT");
					List<Element> pList = nodeT.getChildren();
					for (int i = 0; i < pList.size(); i++) {
						// ajoute le texte Ã  une variable qui implÃ©mentera le texte de chaque doc
						// respectif
						addText += pList.get(i).getText().toLowerCase();

						String p = pList.get(i).getTextTrim();
						String tokenP[] = simpleTokenizer.tokenize(p);

						for (int j = 0; j < tokenP.length; j++) {
							if (d.getFrequencies().containsKey(tokenP[j].toLowerCase())) {
								d.getFrequencies().get(tokenP[j].toLowerCase()).updateOccurence();
							} else {
								d.getFrequencies().put(tokenP[j].toLowerCase(), new Frequency());
							}
						}
					}
					// ajoute le texte Ã  l'attribut du doc
					d.setText(addText);
				} catch(Exception e) {}
			}
				
			//fréquences
			int nbrWords = d.getFrequencies().values().size();
			for (Frequency f : d.getFrequencies().values() ) {
				f.setFrequency(f.getOccurence(), nbrWords);
			}
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (JDOMException e) {
			System.out.println(e.getMessage());
		}
	}

	public ReverseIndex retReverseIndex(List<Doc> listeDoc) throws IOException {
		Map<String, Set<Doc>> ri = new HashMap<String, Set<Doc>>();

		for (Doc d : listeDoc) {
			Set<String> listeMot = d.getFrequencies().keySet();
			for (String mot : listeMot) {
				Set<Doc> list = new HashSet<Doc>();
				if (ri.containsKey(mot.toLowerCase())) {
					ri.get(mot.toLowerCase()).add(d);
				} else {
					ri.put(mot.toLowerCase(), list);
					ri.get(mot.toLowerCase()).add(d);
				}
			}
		}

		return new ReverseIndex(ri);
	}

	public static Set<String> cutting(String phrase) {
		SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;
		String tokenT[] = simpleTokenizer.tokenize(phrase.trim());
		Set<String> splitPhrase = new HashSet<String>();

		for (int j = 0; j < tokenT.length; j++) {
			splitPhrase.add(tokenT[j].toLowerCase());
		}
		return splitPhrase;
	}

	public static List<String> cuttingList(String phrase) {
		SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;
		String tokenT[] = simpleTokenizer.tokenize(phrase.trim());
		List<String> splitPhrase = new ArrayList<String>();

		for (int j = 0; j < tokenT.length; j++) {
			splitPhrase.add(tokenT[j].toLowerCase());
		}
		return splitPhrase;
	}


	public static Map<Doc, int[]> createVector(List<Doc> allDoc, Set<String> allWords) {
		Map<Doc, int[]> vector = new HashMap<Doc, int[]>();

		for (Doc d : allDoc) {
			int i = 0;
			int test = 0;
			List<String> texteParDoc = cuttingList(d.getText());

			for (String s : allWords) {
				test = 0;
				int tab[] = new int[allWords.size()];
				test += texteParDoc.stream().filter(x -> x.equals(s)).count();
				tab[i] = test;

				if (!vector.containsKey(d)) {
					vector.put(d, tab);
				} else {
					vector.get(d)[i] = test;
				}
				i++;
			}
		}
		return vector;
	}
	
	public static int[] toVector(Set<String> toutWords, Set<String> phrase) {
		int i =0;
		int[] tab = new int[toutWords.size()];
		for(String motT : toutWords ) {
			for(String motP : phrase ) {
				if(motP.equals(motT)) {
					tab[i]=1;
				}else {
					tab[i]=0;
				}
			}
			i++;

		}
		return tab;
	}
	
	public static Set<String> toutWords(ReverseIndex ri){
		Set<String> toutWords = new HashSet<String>();
		
		for (String mot : ri.getReverseIndex().keySet()) {
			toutWords.add(mot);
		}
		return toutWords;
	}

	public static SortedSet<Cos> rechercheVectorielle(Index i, ReverseIndex ri, Set<String> phrase) {
		Map<Doc, int[]> vector = createVector(i.getDocuments(), ri.getReverseIndex().keySet());
		int[] vec = toVector(toutWords(ri), phrase);
		
		Map<String , Double> cos =  new HashMap<String , Double>();
		
		  Comparator<Cos> comparator = new Comparator<Cos>() {

		        @Override
		        public int compare(Cos o1, Cos o2) {
		        	if(o1.getCos()>o2.getCos()) {
		        		return -1;
		        	}else {
		        		return 1;
		        	}
		        }
		    };
		    
		SortedSet<Cos> rangeCos = new TreeSet<Cos>(comparator);
		Cos cosV;
		for (Doc d : vector.keySet()) {
			cosV = new Cos(d, ri.calculCosinus(vector.get(d), vec));
			rangeCos.add(cosV);	
		}
		
		return rangeCos;
	}
	
	public static void main(String args[]) throws IOException {
		MotorEvaluation m = new MotorEvaluation();
		Map<Integer, Double> trueMap = new HashMap<>();
		Map<Integer, Double> falseMap = new HashMap<>();
		Map<Integer, Double> mapMotor = new HashMap<>();

		//ON LANCE LE MOTEUR ET LE MINUTEUR
		m.timeStart();
		
		Filter f = new Filter();
		Crawler c = new Crawler("Doc", f);
		Index i = c.retIndex();
		ReverseIndex ri = c.retReverseIndex(i.getDocuments());

		Scanner sc = new Scanner(System.in);
		System.out.println("Rechercher : ");
		String str = sc.nextLine().toLowerCase();

		Set<String> phrase = cutting(str);
		Set<String> listeMotT = f.filterPhrase(phrase, "./application/stopwords");
		Set<Doc> listeDocAnd = ri.researchAnd(listeMotT);
		Set<Doc> listeDocOr = ri.researchOr(listeMotT);
		String tabAnd []= new String [phrase.size()] ;
		
		if(phrase.size()>=2) {
			int k=0;
			for(String mot : phrase) {
				tabAnd[k]=mot;
				System.out.println(mot);
				k++;
			}
		}
	
		Map<Doc, int[]> vector = createVector(i.getDocuments(), ri.getReverseIndex().keySet());
		int[] vec = toVector(toutWords(ri), phrase);
		
		//Map<String , Double> cos =  new HashMap<String , Double>();
		
		  Comparator<Cos> comparator = new Comparator<Cos>() {
		        @Override
		        public int compare(Cos o1, Cos o2) {
		        	if(o1.getCos()>o2.getCos()) {
		        		return -1;
		        	}else {
		        		return 1;
		        	}
		        }
		    };
		    
		SortedSet<Cos> rangeCos = new TreeSet<Cos>(comparator);
		Cos cosV;
		for (Doc d : vector.keySet()) {
			cosV = new Cos(d, ri.calculCosinus(vector.get(d), vec));
			rangeCos.add(cosV);	
		}
		
		m.timeStop();

		for (Cos cos2 : rangeCos) {
			System.out.println("Titre : "+ cos2.getDocV().getTitle()+" | Nom du document :"+ cos2.getDocV().getName()+" | Cos : "+cos2.getCos());
		}
		
		m.saisirDocsPertinents("Doc/Directory1");
		
		/*m.setFalseDoc((HashMap<Integer, Double>) falseMap);
		m.setTrueDoc((HashMap<Integer, Double>) trueMap);*/
		Map<String,Double> mapFinal = m.mesureEval((HashMap<Integer, Double>) mapMotor);
		System.out.println(mapFinal);
		
		System.out.println( "tmpExecution détail " + m.convertMillis(m.getTimeStart()) );
	}

	
}