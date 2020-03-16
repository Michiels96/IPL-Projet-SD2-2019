package main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import graphe.Actor;
import graphe.Graph;
import graphe.Movie;

public class SAXHandler extends DefaultHandler{
	
	private Map<String, Actor> idActors;

	private Set<Actor> actorsOfMovie; // Les acteurs d'un film (change à chaque appel de startElement)
	private Set<String> actorsById;
	
	private Movie movie;
	private Actor actor;
	
	private Graph graph;
	private boolean inActorElement;
	private boolean inMovieElement;
	private String movieYear;
	
	
	public SAXHandler(){
		this.graph = new Graph();
		this.idActors = new HashMap<String, Actor>();
		this.actorsById = new HashSet<String>();
		this.actorsOfMovie = new HashSet<Actor>();
	}
	
	public Graph getGraph(){
		return this.graph;	
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if(qName.equalsIgnoreCase("actor")) {
			inActorElement = true;
			actor = new Actor(attributes.getValue("id"), attributes.getValue("name"));
			idActors.put(attributes.getValue("id"), actor);
			graph.ajouterMoviesParActeur(actor);
			
			//allActors.add(actor); // Shayan 7-03
			//myMovies.put(actor, null);
		}
		else if(qName.equalsIgnoreCase("movie")){
			inMovieElement = true;
			actorsById.clear();
			
			// tous les acteurs d'un film
			actorsById = (HashSet<String>) Arrays.stream(attributes.getValue("actors").split(" ")).collect(Collectors.toSet());
			
			movieYear = attributes.getValue("year");
			
			// SOL1 Pas efficace O(N^2)
			//this.actorsOfMovie.clear(); // Réinitialiser l'ensemble des acteurs pour le prochain traitement
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		/*System.out.println("");
		String ret = "";
		for(Map.Entry<Movie, HashSet<Actor>> entry : this.myActors.entrySet()) {
			ret += entry.getKey().getName() + "[";
			for(Actor act : entry.getValue()) {
				ret += "("+act.getIdActor()+ ","+ act.getName() +")";
			}
			ret += "]\n";
		}
		System.out.println(ret);*/
	}
	
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		// le nom est lu en dernier (apres les acteurs et l'attribut year
		if(inMovieElement){
			String movieName = new String(ch, start, length);
			actorsOfMovie = new HashSet<Actor>();
			movie = new Movie(movieName, movieYear);

			for(String idActeur : actorsById) { // Parcourir les acteurs du film courant
				actor = idActors.get(idActeur);
				actorsOfMovie.add(actor);
				graph.ajouterMovie(actor, movie);
			}
			graph.ajouterActeursParMovie(movie, (HashSet<Actor>) actorsOfMovie);
		}
			
	}
	
}
