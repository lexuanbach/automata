package automata;

import java.util.*;

public class NFA {

	private HashSet<State> states;
	private NTransition transition;
	private Alphabet alphabet;
	private HashSet<State> iniStates;
	private HashSet<State> accStates;
	
	public NFA() {
		states = new HashSet<State>();
		transition = new NTransition();
		alphabet = new Alphabet();
		iniStates = new HashSet<State>();
		accStates = new HashSet<State>();
	}
	
	@SuppressWarnings("unchecked")
	public NFA(Alphabet al, HashSet<State> sts, NTransition tran, HashSet<State> iniS, HashSet<State> accS) {
		
		alphabet = al.clone();
		states = (HashSet<State>) sts.clone();
		transition = tran.clone();
		iniStates = (HashSet<State>) iniS.clone();
		accStates = (HashSet<State>) accS.clone();
	}
	
	public NFA(String[] al, String[] sts, String[] tran, String[] iniS, String[] accS) {
		
		alphabet = new Alphabet(al);
		states = new HashSet<State>();
		for(String item: sts) {
			states.add(new State(item));
		}
		transition = new NTransition(tran);
		iniStates = new HashSet<State>();
		for(String s: iniS) {
			iniStates.add(new State(s));
		}
		accStates = new HashSet<State>();
		for(String s: accS) {
			accStates.add(new State(s));
		}
	}
	
	public NFA clone() {
		
		return new NFA(alphabet,states,transition,iniStates,accStates);
	}
	
	public NFA rename(HashMap<State,State> mapping) {
		
		NFA nfa = new NFA();
		nfa.alphabet = alphabet.clone();
		for (State s: states) {
			nfa.states.add(mapping.get(s).clone());
		}
		for (LabeledEdge edge: transition.getAllEdges()) {
			LabeledEdge newEdge = new LabeledEdge(mapping.get(edge.getSrc()),edge.getLabel(),mapping.get(edge.getDest()));
			nfa.transition.addEdge(newEdge);
		}
		for (State s: iniStates) {
			nfa.iniStates.add(mapping.get(s).clone());
		}
		for (State s: accStates) {
			nfa.accStates.add(mapping.get(s).clone());
		}
		return nfa;
	}
	
	public NFA rename(String prefix) {
		HashMap<State,State> mapping = new HashMap<State,State>();
		int i=0;
		for(State s: states) {
			mapping.put(s.clone(), new State(prefix+(i++)));
		}
		return rename(mapping);
	}
	
	public NFA getUnion(NFA nfa) {
		
		NFA unionA = nfa.rename("s");
		NFA temp = rename("q");
		
		for (String letter: temp.alphabet.getAlphabet()) {
			unionA.alphabet.addLetter(letter);
		}
		for (State state: temp.states) {
			unionA.states.add(state.clone());
		}
		for (LabeledEdge edge: temp.transition.getAllEdges()) {
			unionA.transition.addEdge(edge.clone());
		}
		for (State state: temp.iniStates) {
			unionA.iniStates.add(state.clone());
		}
		for (State state: temp.accStates) {
			unionA.accStates.add(state.clone());
		}
		return unionA;
	}
	
	public String toString() {
		
		String result;
		result = "Alphabet: " + alphabet + "\n";
		result = result + "States: ";
		for(State s: states) {
			result = result + s + " ";
		}
		result = result + "\n";
		result = result + "Transitions: " + transition + "\n";
		result = result + "Initial states: ";
		for(State s: iniStates) {
			result = result + s + " ";
		}
		result = result + "\n";
		result = result + "Accepting states: ";
		for(State s: accStates) {
			result = result + s + " ";
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		NFA nfa = new NFA(new String[] {"0","1"},new String[] {"q0","q1"}, new String[] {"q0 0 q0","q0 1 q0","q0 0 q1","q1 1 q1"}, new String[] {"q0"},new String[] {"q1"});
		System.out.println(nfa);
		System.out.println(nfa.rename("s"));
	}
	
}
