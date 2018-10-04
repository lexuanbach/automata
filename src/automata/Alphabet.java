package automata;

import java.util.*;

public class Alphabet {
	
	private HashSet<String> alist;
	
	public Alphabet(String[] list) {
		
		alist = new HashSet<String>();
		for (String item: list) {
			alist.add(item);
		}
	}
	
	public Alphabet(HashSet<String> list) {
		
		this.alist = list;
	}
	
	public boolean contains(String letter) {
		
		return alist.contains(letter);
	}
	
	public HashSet<String> getAlphabet(){
		
		return alist;
	}
	
	public void setAlphabet(HashSet<String> list) {
		
		alist = list;
	}
	
	public void addLetter(String letter) {
		
		alist.add(letter);
	}
	
	public void addLetters(HashSet<String> letters) {
		
		for (String item: letters) {
			alist.add(item);
		}
	}
	
	public void addLetters(String[] letters) {
		
		for (String item: letters) {
			alist.add(item);
		}
	}
	
	public String toString() {
		String result = "";
		for(String item: alist) {
			result = result + item + " ";
		}
		return result;
	}

}
