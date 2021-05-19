package it.polito.tdp.borders.model;

public class Evento implements Comparable<Evento>{
	//non serve enum perchè ho un solo tipo di evento ossia l'arrivo di migranti in uno stato
	private int t; //tempo
	private Country country; 
	private int n; //numero persone arrivate nel tempo t allo stato country
	
	public Evento(int t, Country country, int n) {
		this.t = t;
		this.country = country;
		this.n = n;
	}

	public int getT() {
		return t;
	}

	public void setT(int t) {
		this.t = t;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	@Override
	public int compareTo(Evento o) {
		return this.t-o.t; //ritono valori >0 =0 <0
	}
	
	

}
