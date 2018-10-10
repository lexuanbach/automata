package automata;

import java.util.*;

public class LabeledEdge {
	
	private State src;
	private String label;
	private State dest;
	
	public LabeledEdge(State s1, String s, State s2) {
		
		src = s1.clone();
		label = s;
		dest = s2.clone();
	}
	
	public LabeledEdge(String s1, String s, String s2) {
		
		this(new State(s1), s, new State(s2));
	}
	
	public LabeledEdge(String input) {
		
		String[] elements = input.split("\\s");
		if(elements.length == 3) {
			src = new State(elements[0]);
			label = elements[1];
			dest = new State(elements[2]);
		} else {
			System.out.println("Error!");
		}
			
	}
	
	public boolean isEpsilonEdge() {
		
		return getLabel().equals("");
	}
	
	public State getSrc() {
		
		return src;
	}
	
	public State getDest() {
		
		return dest;
	}
	
	public String getLabel() {
		
		return label;
	}
	
	public String toString() {
		
		return src + " " + label + " " + dest;
	}
	
	public boolean equals(Object o) {
		
		if (!(o instanceof LabeledEdge)) {
			return false;
		}
		
		LabeledEdge edge = (LabeledEdge) o;
		return src.equals(edge.getSrc()) && label.equals(edge.getLabel()) && dest.equals(edge.getDest());
	}
	
	public int hashCode() {
		
		return Objects.hash(src,label,dest);
	}
	
	public LabeledEdge clone() {
		
		return new LabeledEdge(src,label,dest);
	}

}
