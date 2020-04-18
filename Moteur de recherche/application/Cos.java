package application;

import java.util.Comparator;

public class Cos {

	private Doc d;
	private Double cos;
	
	public Cos(Doc d, Double cos) {
		
		this.d = d;
		this.cos = cos;
		
	}
	
	public double getCos() {
		return this.cos;
	}
	
	public Doc  getDocV() {
		
		return this.d;
	}


}
