package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import it.polito.tdp.crimes.db.EventsDao;


public class Model {
	
	private SimpleWeightedGraph<String,DefaultWeightedEdge> grafo; //String 
	private EventsDao dao;
	//ATTENZIONE: NON CREO IDMAP PERCHE I VERTICI SONO DELLE STRINGHE E NON DEGLI OGGETTI DI UNA TABELLA
	private List<String> percorsoMigliore;
	
	
	public Model() {
		dao= new EventsDao();
		
	}
	
	/** 
	 * RICHIAMO IL METODO CHE CREO NEL DAO PER RIEMPIRE LA TENDINA DELLE CATEGORIE (sono String) :
	 * LO DICHIARO ALL'INIZIO 
	 * @return
	 */
	public List<String> getCategorie(){
		return dao.getCategorie();
	}
	
	
	public void creaGrafo(String categoria, int mese) {
		this.grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//VERTICI --> filtrati, creo il metodo getVertici --> se non ho idMAP passo solo i parametri
		
		Graphs.addAllVertices(grafo, dao.getVertici(categoria,mese));

		//ARCHI --> il peso è dato dal numero di quartieri distinti in cui si verificano due reati diversi
		//entrambi nello stesso quartiere
		//faccio il controllo sugli archi , se non esiste lo aggiungo
		for(Adiacenza a: dao.getAdiacenze(categoria, mese)) {
			if(this.grafo.getEdge(a.getV1(), a.getV2())==null) {
				Graphs.addEdgeWithVertices(grafo, a.getV1(), a.getV2(), a.getPeso());
			}
		}
		
		//stampo sulla console direttamente 
		System.out.println("# vertici: " +this.grafo.vertexSet().size());
	    System.out.println("# archi: " +this.grafo.edgeSet().size());
	
	}
	
	
	/**
	 * CREO METODO CHE MI SERVE PER AVERE UN FILTRO SUGLI ARCHI DA STAMPARE, DOPO AVER CREATO TUTTI GLI ARCHI
	 * RICHIEDE DI STAMPARE SOLO GLI ARCHI CHE HANNO PESO MAGGIORE AL PESO MEDIO DEL GRAFO
	 * @return
	 */
	public List<Adiacenza> getArchiPesoMaggiore(){
		
		//calcolo il peso medio degli archi presenti nel grafo
	
		double pesoMedio=0.0;
		 //per tutti gli archi del grafo
		for(DefaultWeightedEdge e:this.grafo.edgeSet()) {
			 //incremento il pesoMedio con il peso dell'arco che sto scorrendo
			pesoMedio+= this.grafo.getEdgeWeight(e);
		}
		 
		//divido il pesoMedio per la size() degli archi, ovvero quanti archi ho in totale
		pesoMedio= pesoMedio/ this.grafo.edgeSet().size();
	
		//"filtro" gli archi, tenendo solo quelli che hanno peso maggiore del peso medio 
		List<Adiacenza> result = new LinkedList<>();
		
		//scorro di nuovo tutti gli archi, e aggiungo alla lista di Adiacenze solo quelli con peso > pesoMedio
		for(DefaultWeightedEdge e:this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)> pesoMedio) {
			result.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), this.grafo.getEdgeWeight(e)));
			}
	       }
		return result;
	}
	
	//per restituire il num vertici in FXML
	public int getNumeroVertici() {
		if(this.grafo!=null) {
			return this.grafo.vertexSet().size();
		}
		return 0;
	}
	
	//per restituire il num archi in FXML
	public int getNumeroArchi() {
		if(this.grafo!=null) {
			return this.grafo.edgeSet().size();
		}
		return 0;
	}
	
	//trova percorso --> trovo un percorso tra il nodo sorgente e destinazione che non sia diretto 
	//ma voglio trovare la soluzione migliore intesa come percorso piu lungo!
	
	//creo una list di string percorsoMigliore
	
	/**
	 * METODO PER TROVARE IL PERCORSO PIU LUNGO 
	 * @param sorgente
	 * @param destinazione
	 * @return
	 */
	public List<String> trovaPercorso(String sorgente, String destinazione){
		this.percorsoMigliore= new ArrayList<>();
		//definiamo parziale qui e la passiamo come parametro nel cerca
		List<String> parziale= new ArrayList<>();	
		parziale.add(sorgente); //ATTENZIONE: IL PRIMO NODO E' SICURO LA SORGENTE, QUINDI LA AGGIUNGO SUBITO A PARZIALE
		//dopo aver aggiunto la sorgente facciamo partire la ricorsione 
		//a cui passiamo la destinazione, parziale e 0
		cerca(destinazione, parziale);
		return this.percorsoMigliore; 
		//ritorniamo il percorso migliore, che cambia man mano ogni volta fino a raggiungere l'ottimo
	
	}

	/**
	 * METODO RICORSIVO
	 * @param destinazione
	 * @param parziale
	 * NON HO IL LIVELLO PERCHE NON L'HO USATO , NON MI SERVE
	 */
	private void cerca(String destinazione, List<String> parziale) {
	
		//CASO TERMINALE --> se la destinazione è raggiunta,
		  //cioè l'ultimo elemento in parziale (con la get) coincide con la destinazione
		
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			 //devo controllare che il percorso è il migliore visto fin ora, ovvero il più lungo, quindi ha il maggior numero di vertici visitati
			// se parziale è quindi piu lungo del percorso visto fino ad ora, sovrascrivo 
			
			if(parziale.size() > this.percorsoMigliore.size()) {
				this.percorsoMigliore= new LinkedList<>(parziale);
		     }
				return;
			}
		
		//altrimenti : aggiungo un nuovo vertice a partire da dove siamo per poter proseguire il percorso
		//in parziale ho come ultimo elemento un vertice che non è la destinazione 
		//prendo tutti i vicini di questo vertice e provo ad aggiungerli tornando indietro con il backtracking
	
		//Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) restituisce la lista di vertici 
		//adiacenti al vertice che vogliamo per il mio grafo --> noi vogliamo l'ultimo vertice 
		
		//recupero l'ultimo vertice con  parziale.get(parziale.size()-1)
		for(String vicino: Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			
			//se parziale non ha gia quel vicino
			if(!parziale.contains(vicino)) {  //PERCHE' NON VOGLIAMO CREARE UN CICLO (voglio cammino aciclico)
				parziale.add(vicino);
				cerca(destinazione, parziale);
				parziale.remove(parziale.size()-1);
			}
			
		}
	}
}
