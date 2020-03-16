package graphe;

public class Movie {

	private String name;
	private String year;
	
	
	public Movie(String name, String year) {
		super();
		this.name = name;
		this.year = year;
	}


	public String getName() {
		return name;
	}

	public String getYear() {
		return year;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setYear(String year) {
		this.year = year;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Movie other = (Movie) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}
