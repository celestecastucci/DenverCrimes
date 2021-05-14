/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.crimes;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.crimes.model.Adiacenza;
import it.polito.tdp.crimes.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxCategoria"
    private ComboBox<String> boxCategoria; // Value injected by FXMLLoader

    @FXML // fx:id="boxMese"
    private ComboBox<Integer> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="btnAnalisi"
    private Button btnAnalisi; // Value injected by FXMLLoader

    @FXML // fx:id="boxArco"
    private ComboBox<Adiacenza> boxArco; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader
    
    
    
    
    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	txtResult.clear();
    	txtResult.setEditable(false);
    	
    	//selezionati categoria e mese, si crea il grafo alla pressione di ANALISI QUARTIERI
    	String categoria= this.boxCategoria.getValue();
    	Integer mese= this.boxMese.getValue();
    	
    	if(categoria== null || mese==null) {
    		txtResult.appendText("Selezionare entrambi i valori di input!");
    		return;
    	}
    	
    	this.model.creaGrafo(categoria, mese);
    
    	txtResult.appendText("GRAFO CREATO!"+"\n");
    	txtResult.appendText("#VERTICI: "+this.model.getNumeroVertici()+"\n");
    	txtResult.appendText("#ARCHI: "+this.model.getNumeroArchi()+"\n");
    	
    	//stampo l'elenco degli archi con il vincolo sul peso maggiore del peso medio del grafo creato
    	txtResult.appendText("ELENCO ARCHI CON PESO > PESO MEDIO:"+"\n");
    	
    	
    	for(Adiacenza a: this.model.getArchiPesoMaggiore()) {
    		txtResult.appendText(a.toString()+"\n");
    	}
    	
    	//riempio la tendina per il punto 2 !!!!! IMPORTANTE, NON VA RIEMPITA A PRIORI MA IN BASE AL GRAFO CREATO
    	this.boxArco.getItems().addAll(this.model.getArchiPesoMaggiore());
    	
    }


    @FXML
    void doCalcolaPercorso(ActionEvent event) {

     //la tendina va riempita nuovamente ogni volta che ricalcolo il grafo, 
    	//perchè dipende dall'elenco che corrisponde al metodo getArchiPesoMaggiore
    	  
    	txtResult.clear();
    	Adiacenza arco = this.boxArco.getValue();
    	if(arco == null) {
    		txtResult.appendText("Seleziona un arco!");
    		return ;
    	}
    	
    	//scelta una riga della tendina, devo collegare il metodo trova percorso
   
    	List<String> percorso = this.model.trovaPercorso(arco.getV1(), arco.getV2());
    	txtResult.appendText("PERCORSO TRA " + arco.getV1() + " e " + arco.getV2() + ":\n");
    	for(String v : percorso) {
    		txtResult.appendText(v + "\n");	
    	}
    	
    }
   
    @FXML // This method is called by the FXML.Loader when initialization is complete
    void initialize() {
        assert boxCategoria != null : "fx:id=\"boxCategoria\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnAnalisi != null : "fx:id=\"btnAnalisi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxArco != null : "fx:id=\"boxArco\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
  
    	//inserisco le categorie nella box --> ho fatto metodo con la query
    	this.boxCategoria.getItems().addAll(model.getCategorie());
    	
    	//inserisco i mesi nella box --> sono interi, faccio un ciclo for
    	LinkedList<Integer> mesi= new LinkedList<Integer>();
    	for(int i=1; i<=12; i++) {
    		mesi.add(i);
    	}
    	this.boxMese.getItems().addAll(mesi);
    	
    	 //LA BOX ARCO NON LA RIEMPIO A PRIORI MA SOLO DOPO AVER CREATO IL GRAFO
    }
}
