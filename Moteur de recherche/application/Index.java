package application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Index {

	private List<Doc> documents;

	public Index(List<Doc> list) {
		this.documents = list;
	}

	public List<Doc> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Doc> documents) {
		this.documents = documents;
	}

}
