package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Controler implements Initializable {
	// FXML
	// tab
	@FXML private TabPane tabpane_research;
	@FXML private Tab research_tab;
	@FXML private Tab doc_tab;
	// research
	@FXML private Button model_btn;
	@FXML private TextField input_research_bool;
	@FXML private ChoiceBox<String> condition_btn;
	@FXML private Button submit_research_bool;
	@FXML private Button reset_bool;
	@FXML private ListView<String> list_research;
    @FXML private Button empty_list_btn;
	@FXML private TextField input_research_vec;
	@FXML private Button submit_research_vec;
	@FXML private Button reset_vec;
	@FXML private Text logs;
	@FXML private ListView<Text> list_log;
    @FXML private Button empty_log_btn;
    @FXML private Label notice;
    
	// doc
	@FXML private Button return_btn;
	@FXML private TextField input_doc_title;
	@FXML private TextField input_doc_date;
	@FXML private TextArea input_doc_text;

	// Model
	private Filter filter;
	private Crawler crawler;
	private Index index;
	private ReverseIndex revIdx;
	private Collection<String> tmp;
	
	private ListProperty<String> listProperty = new SimpleListProperty<>();
	private ObservableList<String> docOBList = FXCollections.<String>observableArrayList();
	private Map<String, Doc> titleDocMap;
	//private Map<Cos, Doc> titleDocMapVec;

	// functions
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// page prep
		enable_research_bool();
		this.notice.setVisible(false);
		input_research_bool.setPromptText("Deux mots maximum");

		// research prep
		condition_btn.getItems().addAll("OU", "ET", "ET NON");
		condition_btn.setValue("OU");
				
		//model
		try {
			this.filter = new Filter();
			this.crawler = new Crawler("Doc", filter);
			this.index = crawler.retIndex();
			this.revIdx = crawler.retReverseIndex(index.getDocuments());
			
			this.titleDocMap = new HashMap<String, Doc>();
			//this.titleDocMapVec = new HashMap<Cos, Doc>();
			this.list_research.setItems(this.docOBList);
		} catch(Exception exception) {
			System.exit(0);
		}
	}

	@FXML
	void switch_model(ActionEvent event) {
		if (model_btn.getText() == "BOOLÉEN") {
			enable_research_vec();
		} else {
			enable_research_bool();
		}
	}

	private void enable_research_bool() {
		model_btn.setText("BOOLÉEN");
		input_research_vec.setDisable(true);
		submit_research_vec.setDisable(true);
		reset_vec.setDisable(true);
		input_research_bool.setDisable(false);
		input_research_bool.setPromptText("Deux mots maximum");
		condition_btn.setDisable(false);
		submit_research_bool.setDisable(false);
		reset_bool.setDisable(false);
	};

	private void enable_research_vec() {
		model_btn.setText("VECTORIEL");
		input_research_bool.setDisable(true);
		condition_btn.setDisable(true);
		submit_research_bool.setDisable(true);
		reset_bool.setDisable(true);
		input_research_vec.setDisable(false);
		submit_research_vec.setDisable(false);
		reset_vec.setDisable(false);
	};

	//récupère le doc pour l'afficher
    @FXML
    void selectDoc(MouseEvent event) {
    	try {
    		String title = this.list_research.getSelectionModel().getSelectedItem();	
    		Doc doc = this.titleDocMap.get(title);
    		
    		this.input_doc_title.setText(doc.getTitle());
    		this.input_doc_date.setText(doc.getDate());
    		this.input_doc_text.setText(doc.getText());
    		
    		this.tabpane_research.getSelectionModel().select(doc_tab);
       	} catch(Exception exception) {
       		this.tabpane_research.getSelectionModel().select(research_tab);
       		logInsert("Pas de document à récupérer", 1);
       	}
    }
	
    //récupère les logs pour pouvoir refaire des recherches
    @FXML
    void selectLog(MouseEvent event) {
    	try {
    		String research = "";
    		Text txt = this.list_log.getSelectionModel().getSelectedItem();
    		if (txt.getFill() == Color.BLACK )
    			research = txt.getText();
   
    		
    		if (model_btn.getText() == "BOOLÉEN") {
    			this.input_research_bool.setText(research);
    		} else {
    			this.input_research_vec.setText(research);
    		}
    	} catch(Exception exception) {
       		logInsert("Erreur dans la récupération de log", 1);
    	}
    }
    
    //soumets la recherche booléenne
	@FXML
	void submit_bool(ActionEvent event) {
		try {
			Set<Doc> docList = new HashSet<Doc>();
			String input = input_research_bool.getText().toLowerCase();
			Set<String> phrase = Crawler.cutting(input);
			Set<String> filtered = this.filter.filterPhrase(phrase, "./application/stopwords");
			
			//précise dans les logs l'exception
			if(filtered.size() > 2) {
				logInsert("Votre recherche contient plus de deux mots", 1);
				throw new Exception("");
			} else if(filtered.isEmpty()) {
				logInsert("Votre recherche est vide", 1);
				throw new Exception("");
			}
			
			if( condition_btn.getValue() == "OU" ) {
				docList = this.revIdx.researchOr(filtered);
			} else if( condition_btn.getValue() == "ET" ) {
				docList = this.revIdx.researchAnd(filtered);
			} else {
				List<String> list = filtered.stream().collect(Collectors.toList());
				docList = this.revIdx.researchAndNot(list.get(0), list.get(1));
			}
			
			if(docList.isEmpty()) {
				//précise dans les logs le résultat null
				logInsert("Aucun résultat", 2);
			} else {
				//transforme le set<string> en un seul string
				logInsert(filtered.toString().replaceAll("\\[|\\]","").replaceAll(","," "), 0);
				this.titleDocMap.clear();
			}
			
			for (Doc doc : docList) {
				if(doc.getTitle()!= null && doc.getTitle()!="" && doc.getTitle()!="null") {
					this.titleDocMap.put(doc.getTitle(), doc);
				} else {
					this.titleDocMap.put("Titre inexistant", doc);
				}
			}
			this.docOBList.setAll(this.titleDocMap.keySet());
		} catch (Exception exception) {}
	}

	@FXML
	void submit_vec(ActionEvent event) {
		try {
			List<Doc> docList;
			String input = input_research_vec.getText().toLowerCase();
			Set<String> phrase = Crawler.cutting(input);
			Set<String> filtered = this.filter.filterPhrase(phrase, "./application/stopwords");
			
			//précise dans les logs l'exception
			if(filtered.isEmpty()) {
				logInsert("Votre recherche est vide", 1);
				throw new Exception("");
			}
			
			docList = Crawler.rechercheVectorielle(this.index, this.revIdx, filtered).stream().map(x -> x.getDocV()).collect(Collectors.toList());
					
			if(docList.isEmpty()) {
				//précise dans les logs le résultat null
				logInsert("Aucun résultat", 2);
			} else {
				//transforme le set<string> en un seul string
				logInsert(filtered.toString().replaceAll("\\[|\\]","").replaceAll(","," "), 0);
				this.titleDocMap.clear();
			}
			
			for (Doc doc : docList) {
				System.out.println(doc.getTitle() + " " + doc.getName());
				if(doc.getTitle()!= null && doc.getTitle()!="" && doc.getTitle()!="null") {
					this.titleDocMap.put(doc.getTitle(), doc);
				} else {
					this.titleDocMap.put("Titre inexistant", doc);
				}
			}
			this.docOBList.setAll(this.titleDocMap.keySet());
		} catch (Exception exception) {}
	}

	@FXML
	void resetBool(ActionEvent event) {
		input_research_bool.setText("");
		condition_btn.setValue("OU");
		this.list_research.getItems().clear();
		docOBList.clear();
		emptyDocTab();
	}

	@FXML
	void resetVec(ActionEvent event) {
		input_research_vec.setText("");
		this.list_research.getItems().clear();
		docOBList.clear();
		emptyDocTab();
	}

    @FXML
    void switch_tab(ActionEvent event) {
		if(this.tabpane_research.getSelectionModel().getSelectedItem().equals(this.research_tab) ) {
			this.tabpane_research.getSelectionModel().select(doc_tab);
		} else {
			this.tabpane_research.getSelectionModel().select(research_tab);
		}
    }
	
	void logInsert(String log, int color) {
		Text txt = new Text(log);
		switch (color) {
			case 1: txt.setFill(Color.RED); break;
			case 2: txt.setFill(Color.DARKORANGE); break;
			default: txt.setFill(Color.BLACK); break;
		}
		this.list_log.getItems().add(txt);
	}
	
	void emptyDocTab() {
		this.input_doc_title.setText("");
		this.input_doc_date.setText("");
		this.input_doc_text.setText("");
	}
	
    @FXML void emptyList(ActionEvent event) { this.list_research.getItems().clear(); }
    @FXML void emptyLog(ActionEvent event) { this.list_log.getItems().clear(); }
    
    @FXML void showNotice(MouseEvent event) { this.notice.setVisible(true); }
    @FXML void hideNotice(MouseEvent event) { this.notice.setVisible(false); }
}
