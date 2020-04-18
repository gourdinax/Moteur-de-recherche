package application;


import java.util.Map;

public class Frequency {

    private Integer occurence;
    private Double frequency; // occurence/nbr total occurences
    private Double TFIDF;

    public Frequency() {
        this.occurence = 1;
        this.frequency = 0.0;
    }

    public void updateOccurence() {
        this.occurence++;
    }

    public void setFrequency(Integer occ, Integer occTotal) {
        this.frequency = (double) occ / occTotal;
    }

    public void setTFIDF() {
    	
    }
    
    public String toString() {
    	return " [Occurence:"+ this.occurence + ", Frequence:" + this.frequency + "]\n";
    }
    
    public Integer getOccurence() {
    	
    	return this.occurence;
    	
    }

}