package automata;

import java.util.Objects;
import java.util.regex.Pattern;

public class LabeledEdge {
	
	private State src;
	private String label;
	private State dest;
	
	public LabeledEdge(State s1, String s, State s2) {
		src = s1;
		label = s;
		dest = s2;
	}
	
	public LabeledEdge(String s1, String s, String s2) {
		this(new State(s1), s, new State(s2));
	}
	
	public LabeledEdge(String arrow) {
		if (Pattern.matches("\\w+\\s\\w+\\s\\w+", arrow)) {
			String[] elements = arrow.split("\\s");
			src = new State(elements[0]);
			label = elements[1];
			dest = new State(elements[2]);
		} else {
			System.out.println("Error");
		}
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

}
