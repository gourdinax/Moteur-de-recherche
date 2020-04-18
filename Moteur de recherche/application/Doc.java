package application;


import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Doc {

	private String title;
	private String date;
	private String text;
	private String name;
	private Map<String, Frequency> frequencies;

	public Doc() {
		this.name = "";
		this.title = "";
		this.date = "";
		this.text = "";
		this.frequencies = new HashMap<String, Frequency>();
	}

	public Map<String, Frequency> getFrequencies() {
		return this.frequencies;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setFrequencies(Map<String, Frequency> frequencies) {
		this.frequencies = frequencies;
	}
	

}