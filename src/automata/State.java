package automata;

public class State {
	
	private String name;
	
	public State() {
		name = "";
	}
	
	public State(String name) {
		
		this.name = name;
	}
	
	public State(State s) {
		this.name = s.name;
	}
	
	public String getName() {return name;}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean equals(Object o) {
	     if (!(o instanceof State)){ 
	         return false;
	       }
	       State s = (State) o;
	       return s.name.equals(name);
		
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public int compareTo(State s) {
		return hashCode() - s.hashCode();
	}
	
	public String toString() {
		return name;
	} 
	
	public State clone() {
		return new State(name);
	}

}
