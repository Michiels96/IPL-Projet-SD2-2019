package graphe;

public class Actor implements Comparable<Actor>{

	private String idActor;
	private String name;
	private int cout;


	public Actor(String idActor, String name) {
		super();
		this.idActor = idActor;
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idActor == null) ? 0 : idActor.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Actor other = (Actor) obj;
		if (idActor == null) {
			if (other.idActor != null)
				return false;
		} else if (!idActor.equals(other.idActor))
			return false;
		return true;
	}

	public String getIdActor() {
		return idActor;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Actor o) {
		int diff = this.cout - o.cout;
		if(diff == 0) {
			return this.idActor.compareTo(o.idActor);
		}
		return diff;
	}

	public int getCout() {
		return cout;
	}

	public void setCout(int cout) {
		this.cout = cout;
	}
	
	
	
	
}
