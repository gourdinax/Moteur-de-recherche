package application;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class MotorEvaluation {

	// Au début du main
	private double timeStart;
	private HashMap<Integer, Double> doc;
	private String requete;
	private int nbFalse;
	private int nbTrue;
	private int vp;
	private int fp;
	private int fn;
	private int vn;

	public MotorEvaluation() {
		this.doc = new HashMap<>();
	}

	public void timeStart() { this.timeStart = System.currentTimeMillis(); }
	
	public void timeStop() { this.timeStart = System.currentTimeMillis() - this.getTimeStart(); }
	
	public String convertMillis(double time) {
		return (int)((time/1000) / 60) + " minute(s), " + (int)((time/1000) % 60) + " seconde(s)";
	}

	public void saisirDocsPertinents(String rep) {
		File repertoire = new File(rep);
		String liste[] = repertoire.list();
		Scanner sc = new Scanner(System.in);
		String test = "", test2="";

		if (liste != null) {
			for (int i = 0; i < liste.length; i++) {
				do {
					System.out.println("Le document " + liste[i] + " est-il pertinent selon vous ? y/n");
					test = sc.nextLine();

				} while (!test.equals("y") && !test.equals("n"));
				if (test.equals("y")) {
					incrementeVp();
					nbTrue++;
				} else {
					//pour le faux positifs il faudrait demander confirmation à l'utilisateur
					//refaire un do while et un questionnaire
					do {
						System.out.println("Présente-t-il au moins un des mots de votre requête ? y/n");
						test2 = sc.nextLine();

					} while (!test2.equals("y") && !test2.equals("n"));
					if(test2.equals("y")) {
						incrementeFp();
					} else {
						incrementeVn();
						incrementeFn();
					}					
					nbFalse++;
				}
			}
		} else {
			System.err.println("Nom de repertoire invalide");
		}
	}

	public void incrementeFn() { this.fn++;	}

	public void incrementeFp() { this.fp++;	}

	public void incrementeVn() { this.vn++;	}

	public void incrementeVp() { this.vp++;	}

	public void requete(String r) {	this.requete = r; }

	// retourne les mesures
	public Map<String, Double> mesureEval(HashMap<Integer, Double> m) {
		System.out.println("Vrais positifs : " + this.vp);
		System.out.println("Faux positifs : " + this.fp);
		System.out.println("Vrais négatifs : " + this.fn);
		System.out.println("Faux négatifs : " + this.vn);

		Map<String, Double> map = new HashMap<>();
		map.put("tmpExecution", this.getTimeStart());
		map.put("precision", calculPrecision(this.vp, this.fp));
		map.put("rappel", calculRappel(this.vp, this.fn));

		return map;
	}

	// ou calcul sensibilité
	public static double calculPrecision(int vp, int fp) {
		return (double) vp / (vp + fp);
	}

	public static double calculRappel(int vp, int fn) {
		return (double) vp / (vp + fn);
	}

	public double getTimeStart() {
		return timeStart;
	}

}