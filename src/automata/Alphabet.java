package automata;

import java.util.ArrayList;

public class Alphabet {
	
	private ArrayList<String> alist;
	
	public Alphabet(ArrayList<String> list) {
		
		this.alist = list;
	}
	
	public boolean isAlphabet(String letter) {
		
		for (int i=0;i<this.alist.size();i++) {
			if (this.alist.get(i).equals(letter)) {
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<String> getAlphabet(){
		
		return this.alist;
	}
	
	public void setAlphabet(ArrayList<String> list) {
		
		this.alist = list;
	}
	
	public void addLetter(String letter) {
		
		this.alist.add(letter);
	}
	
	public void addLetters(ArrayList<String> letters) {
		
		for (int i=0;i<letters.size();i++) {
			this.alist.add(letters.get(i));
		}
	}

}
