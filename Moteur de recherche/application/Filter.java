package application;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import opennlp.tools.tokenize.SimpleTokenizer;

public class Filter {

	public Filter() {

	}

	public static SortedSet<String> stopWords(String fileName) throws IOException {
		Stream<String> stream = Files.lines(Paths.get(fileName));
		Set<String> s = stream.collect(Collectors.toSet());
		SortedSet<String> ss = new TreeSet<String>();
		ss.addAll(s);
		return ss;
	}

	public void filter(Map<String, Frequency> listeWords, String fileName) throws IOException {
		SortedSet<String> stopWords = stopWords(fileName);

		for (String stop : stopWords) {
			if (listeWords.containsKey(stop)) {
				listeWords.remove(stop);
			}
		}
	}
		
		public Set<String> filterPhrase(Set<String> phrase, String fileName) throws IOException {
			SortedSet<String> stopWords = stopWords(fileName);
			
			for (String stop : stopWords) {
				if (phrase.contains(stop)) {
					phrase.remove(stop);
				}
			}
		return phrase;
	}

}
