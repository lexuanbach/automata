package automata;

import java.util.Objects;

public class Pair<T,S> {
	
	private T fst;
	private S snd;
	
	public Pair(T first, S second) {
		
		fst = first;
		snd = second;
	}
	
	public T getFst() {return fst;}
	public S getSnd() {return snd;}
	
	public void setFst(T first) {fst = first;}
	public void setSnd(S second) {snd = second;}
	public void setPair(T first, S second) {
		setFst(first);
		setSnd(second);
	}
	
	public String toString() {
		return "<" + fst + "," + snd + ">";
	}
	
	@SuppressWarnings("rawtypes")
	public boolean equals(Object o) {
	     if (!(o instanceof Pair<?,?>)){ 
	         return false;
	       }
	    @SuppressWarnings("unchecked")
		Pair p = (Pair<T,S>) o;
		return fst.equals(p.fst) && snd.equals(p.snd);
	}
	
	public int hashCode() {
		
		return Objects.hash(fst,snd);
	}
	
	public int compareTo(Pair<T,S> p) {
		return hashCode()-p.hashCode();
	}
	
}
