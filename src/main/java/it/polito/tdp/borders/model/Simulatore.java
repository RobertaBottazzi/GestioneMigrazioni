package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {
	
	//Stato del sistema ad ogni passo
	private Graph<Country, DefaultEdge> grafo;
	//Tipi di evento-> influisce sulla queue
	private PriorityQueue<Evento> queue; //queue è fondamentale nel momento in cui aggiungo eventi mentre sto simulando, qui infatti il numero di eventi non è fisso a priori perchè genero altri eventi man mano che arrivano persone in uno stato (il fiume invece sapevamo già il numero di eventi perchè c'era entrata e uscita per ogni giorno dove c'era una misurazione)
	//Parametri in entrata 
	private int N_MIGRANTI=1000;
	private Country partenza;
	//valori in output
	private int T=-1;
	private Map<Country, Integer> stanziali; //durante la simulazione devo modificare man mano le persone in uno stato perchè queste si muovono e possono tornare in uno stato dove sono già state e possono diventare stanziali in esso, quindi avere una mappa è più semplice per modificare il valore perchè se avessi una lista ogni volta dovrei scorrerla mentre con la mappa mi basta fare un get della chiave
	
	//inizializzo la simulazione
	public void init(Country country, Graph<Country, DefaultEdge> grafo) {
		this.partenza=country;
		this.grafo=grafo;
		
		//imposto lo stato iniziale
		this.T=1; //così almeno una simulazione la faccio
		this.stanziali= new HashMap<>();
		for(Country c: this.grafo.vertexSet()) {
			stanziali.put(c, 0);
		}
		
		//creo la coda
		this.queue= new PriorityQueue<>();
		//inserisco il primo evento dove arrivano 1000 persone (N_MIGRANTI)
		this.queue.add(new Evento(this.T,partenza,this.N_MIGRANTI));
		
	}
	//a partire dal primo evento lancio tutta la simulazione
	public void run() {
		//finchè la coda non si svuota prendo un evento e lo eseguo
		Evento e;
		while((e=this.queue.poll())!=null) {
			//simulo evento
			this.T=e.getT(); //sovrascrivo T con il tempo dell'evento
			int nPersone=e.getN();
			Country stato=e.getCountry();
			
			//dobbiamo prendere i vicini dello stato per sapere gli stati dove si possono spostare le persone
			List<Country> vicini=Graphs.neighborListOf(grafo, stato);
			//ottenuti gli stati vicini calcolo quante persone finiscono in ogni stato
			int migrantiPerStato=(nPersone/2)/vicini.size(); //essendo un int se ottengo un numero con la virgola il resto finisce direttamente in stanziali
			//caso particolare: numero persone che si spostano è minore del numero di vicini, in quel caso nessuno si sposta
			//nPersone=10
			//persone che si spostano=5
			//se i vicini.size()=7
			//(nPersone/2)/vicini.size()=0,..
			if(migrantiPerStato>0) {
				//creo evento per ogni vicino
				for(Country confinante: vicini) {
					queue.add(new Evento(e.getT()+1,confinante,migrantiPerStato));
				}
			}
			//else
				//nessuno si sposta, sono tutti stanziali
			
			//gli stanziali sono il 50% oppure 50%+persone che non si sono spostate per via del caso particolare, dipende appunto dal caso in cui mi trovo
			int stanziali= nPersone-migrantiPerStato*vicini.size(); //se rientro nel caso particolare migrantiPerStato=0 quindi rimane solo nPersone
			this.stanziali.put(stato, this.stanziali.get(stato)+stanziali); //sovrascrivo l'informazione precendente delle persone all'interno dello stato
		}
	}
	
	public Map<Country,Integer> getStanziali(){
		return this.stanziali;
	}
	public Integer getT() {
		return this.T;
	}
}
