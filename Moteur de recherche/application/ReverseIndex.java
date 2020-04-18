package application;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReverseIndex {

	private Map<String, Set<Doc>> reverseIndexList;

	public ReverseIndex(Map<String, Set<Doc>> ri) {
		this.reverseIndexList = ri;
	}

	public Map<String, Set<Doc>> getReverseIndex() {
		return this.reverseIndexList;
	}

	public String toString() {
		return "" + this.reverseIndexList;
	}

	public Set<Doc> getListeNumDoc(String mot) {
		return this.reverseIndexList.get(mot);
	}

	public Set<Doc> researchAnd(Set<String> phrase) {
		Set<Doc> l3 = new HashSet<Doc>();
		Boolean test = true;

		for (String mot : phrase) {
			if (!this.getReverseIndex().containsKey(mot)) {
				test = false;
			}
		}

		if (test == true) {
			for (Doc b : this.getListeNumDoc((String) phrase.toArray()[0])) {
				l3.add(b);
			}

			for (String mot : phrase) {
				if (this.getReverseIndex().containsKey(mot)) {
					Set<Doc> pivotListe = new HashSet<Doc>();

					for (Doc numL3 : l3) {
						if (this.getListeNumDoc(mot).contains(numL3)) {
							pivotListe.add(numL3);
						}
					}
					l3 = pivotListe;
				}
			}

			return l3;
		} else {
			return null;
		}

	}

	public Set<Doc> researchOr(Set<String> phrase) {
		Set<Doc> l3 = new HashSet<Doc>();
		Set<String> l4 = new HashSet<String>();

		for (String mot : phrase) {
			if (this.getReverseIndex().containsKey(mot)) {
				l4.add(mot);
			}
		}

		for (String mot : l4) {
			for (Doc numDoc : this.getListeNumDoc(mot)) {
				l3.add(numDoc);
			}
		}
		return l3;
	}

// A REVOIR PAS FINI
	
	public Set<Doc> researchAndNot(String mot1,String mot2) {
		Boolean test = true;
		Set<Doc> l3 = new HashSet<Doc>();
		Set<Doc> l4 = new HashSet<Doc>();

			
			if (!this.getReverseIndex().containsKey(mot1) && !this.getReverseIndex().containsKey(mot2)) {
				
				test = false;
				
			}
		

		if (test == true) {
			
			
				for (Doc numDoc : this.getListeNumDoc(mot1)) {
					l3.add(numDoc);
				}
				for (Doc numDoc : this.getListeNumDoc(mot2)) {
					l4.add(numDoc);
				}
				
				for(Doc d : l4) {
				
					if(l3.contains(d)) {
						l3.remove(d);
				}
				
			}

			return l3;
		} else {
			return null;
		}
	}


	public static Double norme(int[] vecteur) {
		int carre = 0;
		for (int i = 0; i < vecteur.length; i++) {
			carre += vecteur[i]^2;
		}
		return Math.sqrt(carre);
	}

	public static int produitScalaire(int[] tabNimp, int[] phrase) {
		int produitScalaire = 0;
		for (int i = 0; i < Math.min(phrase.length, tabNimp.length); i++) {
			produitScalaire += phrase[i] * tabNimp[i];
		}
		return produitScalaire;
	}

	public static Double calculCosinus(int[] tabNimp, int[] phrase) {
		return produitScalaire(tabNimp, phrase) / (norme(phrase) * norme(tabNimp));
	}

}
