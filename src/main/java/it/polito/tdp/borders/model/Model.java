package it.polito.tdp.borders.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.borders.db.BordersDAO;

public class Model {
	
	private Graph<Country, DefaultEdge> graph ;
	private Map<Integer,Country> countriesMap ;
	private Simulatore simulatore;
	
	public Model() {
		this.countriesMap = new HashMap<>() ;
		this.simulatore= new Simulatore();
	}
	
	public void creaGrafo(int anno) {
		
		this.graph = new SimpleGraph<>(DefaultEdge.class) ;

		BordersDAO dao = new BordersDAO() ;
		
		//vertici
		dao.getCountriesFromYear(anno,this.countriesMap) ;
		Graphs.addAllVertices(graph, this.countriesMap.values()) ;
		
		// archi
		List<Adiacenza> archi = dao.getCoppieAdiacenti(anno) ;
		for( Adiacenza c: archi) {
			graph.addEdge(this.countriesMap.get(c.getState1no()), 
					this.countriesMap.get(c.getState2no())) ;
			
		}
	}
	
	public List<CountryAndNumber> getCountryAndNumbers(){
		List<CountryAndNumber> result= new LinkedList<>();
		
		for(Country c: this.graph.vertexSet()) {
			result.add(new CountryAndNumber(c, this.graph.degreeOf(c))); //degreeOf restituisce quanti archi toccano il vertice quindi quanti vicini ho se il grafo non è orientato, mentre se il grafo è orientato ritorna la somma del grado in entrata e quello di uscita quindi il numero di archi che toccano il vertice inpendentemente che sia in entrata o uscita.
		}
		Collections.sort(result);
		return result;
	}
	
	public void Simula(Country partenza) {
		if(graph!=null) {
			this.simulatore.init(partenza, graph);
			this.simulatore.run();
		}
	}

	public Integer getT() {
		return this.simulatore.getT();
	}
	
	public List<CountryAndNumber> getStanziali() {
		Map<Country, Integer> stanziali=this.simulatore.getStanziali();
		List<CountryAndNumber> result= new ArrayList<>();
		for(Country c: stanziali.keySet()) {
			if(stanziali.get(c)>0) {
				CountryAndNumber cn= new CountryAndNumber(c, stanziali.get(c));
				result.add(cn);
			}
		}
		Collections.sort(result);
		return result;
	}

	public Set<Country> getCountries() {
		if(this.graph!=null)
			return this.graph.vertexSet();
		return null;
	}
}
