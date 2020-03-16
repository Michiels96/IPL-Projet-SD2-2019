package graphe;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Graph {

	private Map<Actor, HashSet<Movie>> myMovies;
	private Map<Movie, HashSet<Actor>> myActors;

	public Graph() {
		this.myMovies = new HashMap<Actor, HashSet<Movie>>();
		this.myActors = new HashMap<Movie, HashSet<Actor>>();
	}

	public void calculerCheminLePlusCourt(String actor1, String actor2, String path) {
		Deque<Actor> file = new ArrayDeque<Actor>();
		Set<Actor> actorsVisites = new HashSet<Actor>();

		Actor firstAct = myMovies.keySet().stream().filter(s -> s.getName().equals(actor1)).findFirst().get();
		Actor actorTemp;
		Actor lastAct = myMovies.keySet().stream().filter(s -> s.getName().equals(actor2)).findFirst().get();

		// map nouvel acteur, acteur source
		Map<Actor, Actor> actorLink = new HashMap<Actor, Actor>();
		// map nouvel acteur, film dans lequel il a joue
		Map<Actor, Movie> movieOfActor = new HashMap<Actor, Movie>();

		// Les variables nécessaire uniquement pour le fichier XML
		int totalCost = 0;
		List<Actor> actorsToSave = new ArrayList<Actor>();
		List<Movie> moviesToSave = new ArrayList<Movie>();

		actorsVisites.add(firstAct);
		file.add(firstAct);
		while (!file.isEmpty()) {
			actorTemp = file.poll();
			Set<Movie> moviesOfActor = this.myMovies.get(actorTemp);

			for (Movie mov : moviesOfActor) {
				Set<Actor> actorsOfMovie = this.myActors.get(mov);
				for (Actor actorMovie : actorsOfMovie) {
					// permet d'éviter de repasser au meme acteur
					if (!actorsVisites.contains(actorMovie)) {
						file.add(actorMovie);
						actorsVisites.add(actorMovie);
						actorLink.put(actorMovie, actorTemp);
						movieOfActor.put(actorMovie, mov);

						if (actorMovie.equals(lastAct)) {
							Actor act = lastAct;
							totalCost += myActors.get(movieOfActor.get(lastAct)).size();
							int nbActeurs = 1;
							do{
								nbActeurs++;
								totalCost += myActors.get(movieOfActor.get(act)).size();
								act = actorLink.get(act);
							}while(act != firstAct);
							
							Actor[] actors = new Actor[nbActeurs];
							Movie[] movies = new Movie[nbActeurs-1];
							act = lastAct;
							for(int i = nbActeurs-1; i > 0; i--) {
								actors[i] = act;
								if(i > 0) {
									movies[i-1] = movieOfActor.get(act);
								}
								
								act = actorLink.get(act);
							}
							actors[0] = firstAct;
							actorsToSave = Arrays.asList(actors);
							moviesToSave = Arrays.asList(movies);
							break;
						}
					}
				}
			}
		}

		try {
			if(!actorsToSave.contains(lastAct)){
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("impossible d'aller à l'acteur de destination");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Sauver en XML
		sauverDansFichierXml(totalCost, actorsToSave, moviesToSave, path);
		
		
	}

	private void sauverDansFichierXml(int totalCost, List<Actor> actorsToSave, List<Movie> moviesToSave, String fileName) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = dBuilder.newDocument();
		Element rootElement = doc.createElement("path");
		doc.appendChild(rootElement);

		// Ajout de l'attribut cost
		Attr movieCost = doc.createAttribute("cost");
		movieCost.setValue(String.valueOf(totalCost));
		rootElement.setAttributeNode(movieCost);

		// Ajout de l'attribut nbMovies
		Attr nbMovies = doc.createAttribute("nbMovies");
		nbMovies.setValue(String.valueOf(moviesToSave.size()));
		rootElement.setAttributeNode(nbMovies);

		List<Actor> acts = actorsToSave.stream().collect(Collectors.toList());
		List<Movie> movs = moviesToSave.stream().collect(Collectors.toList());

		// Ajout élément actor
		for (int i = 0; i < actorsToSave.size(); i++) {
			Element actor = doc.createElement("actor");
			Element movie = doc.createElement("movie");
			actor.appendChild(doc.createTextNode(acts.get(i).getName()));
			rootElement.appendChild(actor);
			if (i < actorsToSave.size() - 1) {
				Attr name = doc.createAttribute("name");
				name.setValue(movs.get(i).getName());
				movie.setAttributeNode(name);
				Attr year = doc.createAttribute("year");
				year.setValue(movs.get(i).getYear());
				movie.setAttributeNode(year);
				rootElement.appendChild(movie);
			}
		}

		// enregistrer dans un fichier
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileName));
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param actor1
	 * @param actor2
	 * @param path
	 */
	public void calculerCheminCoutMinimum(String actor1, String actor2, String path) {
		SortedSet<Actor> actorsBetweenActorsCost = new TreeSet<Actor>();
		// Integer Max Value et diminuer au fur et a mesure le cout
		Map<Actor, Integer> etiquetteDefinitive = new HashMap<Actor, Integer>();
		int maxValue = Integer.MAX_VALUE;

		Actor firstAct = myMovies.keySet().stream().filter(s -> s.getName().equals(actor1)).findFirst().get();
		Actor actorTemp;
		Actor lastAct = myMovies.keySet().stream().filter(s -> s.getName().equals(actor2)).findFirst().get();
		
		Map<Actor, Actor> actorLink = new HashMap<Actor, Actor>();
		// map nouvel acteur, film dans lequel il a joue
		Map<Actor, Movie> movieOfActor = new HashMap<Actor, Movie>();

		for (Actor act : myMovies.keySet()) {
			act.setCout(maxValue);
		}

		firstAct.setCout(0);
		actorsBetweenActorsCost.add(firstAct);

		SortedSet<Actor> actorsNeighbour = new TreeSet<Actor>();
		Set<Movie> moviesOfActor = this.myMovies.get(firstAct);
	

		// tant que l'etiquette provisoire n'est pas vide
		while (!actorsBetweenActorsCost.isEmpty()){
			if(etiquetteDefinitive.containsKey(lastAct)){
				break;
			}
			// Recuperer le cout min
			Actor act = actorsBetweenActorsCost.first();
			moviesOfActor = this.myMovies.get(act);

			// Mettre croix dans l'étiquette provisoire
			actorsBetweenActorsCost.remove(act);
			etiquetteDefinitive.put(act, act.getCout());

			for (Movie mov : moviesOfActor) {
				Set<Actor> actorsOfMovie = this.myActors.get(mov);

				int movieCost = myActors.get(mov).size();
				// parcours les acteurs du film
				for (Actor actNeighbour : actorsOfMovie) {
					if (!etiquetteDefinitive.containsKey(actNeighbour)) {

						if (!actorsBetweenActorsCost.contains(actNeighbour)) {
							// mise a jour du cout dans treeSet en l'enlevant
							actNeighbour.setCout(act.getCout() + movieCost);
							actorLink.put(actNeighbour, act);
							movieOfActor.put(actNeighbour, mov);
							actorsBetweenActorsCost.add(actNeighbour);
							
						} else if (movieCost + act.getCout() < actNeighbour.getCout()) {
							actorsBetweenActorsCost.remove(actNeighbour);
							actNeighbour.setCout(act.getCout() + movieCost);
							actorLink.put(actNeighbour, act);
							movieOfActor.put(actNeighbour, mov);
							actorsBetweenActorsCost.add(actNeighbour);
						}
					}
				}
			}
		}
		try {
			if(actorLink.get(lastAct) == null) {
				throw new Exception();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("impossible d'aller à l'acteur de destination");
			e.printStackTrace();
		}
		
		
		Actor act = lastAct;
		
		int nbActeurs = 1;
		do{
			nbActeurs++;
			act = actorLink.get(act);
		}while(act != firstAct);
		Actor[] actors = new Actor[nbActeurs];
		Movie[] movies = new Movie[nbActeurs-1];
		act = lastAct;
		for(int i = nbActeurs-1; i > 0; i--) {
			actors[i] = act;
			if(i > 0) {
				movies[i-1] = movieOfActor.get(act);
			}
			
			act = actorLink.get(act);
		}
		actors[0] = firstAct;
		List<Actor> actorsToSave = Arrays.asList(actors);
		List<Movie> moviesToSave = Arrays.asList(movies);
		sauverDansFichierXml(lastAct.getCout(), actorsToSave, moviesToSave, path);
	}

	public void ajouterMoviesParActeur(Actor actor) {
		myMovies.put(actor, new HashSet<Movie>());
	}

	public void ajouterMovie(Actor actor, Movie movie) {
		myMovies.get(actor).add(movie);
	}

	public void ajouterActeursParMovie(Movie movie, HashSet<Actor> actors) {
		myActors.put(movie, actors);
	}
}
