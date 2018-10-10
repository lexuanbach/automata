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
	
	public NFA(String al, String sts, String tran, String iniS, String accS) {
		
		this(al.trim().split("[\\s]+"),sts.trim().split("[\\s]+"),tran.trim().split("[\\s]*,[\\s]*"),iniS.trim().split("[\\s]+"),accS.trim().split("[\\s]+"));	
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
		int i=1;
		for(State s: states) {
			mapping.put(s.clone(), new State(prefix+(i++)));
		}
		return rename(mapping);
	}
	
	public NFA getUnion(NFA nfa) {
		
		NFA unionA = nfa.rename("u");
		NFA temp = rename("t");
		
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
		return unionA.rename("q");
	}
	
	public HashMap<State,HashSet<State>> computeEClosure(){
		
		HashMap<State,HashSet<State>> closure = new HashMap<State,HashSet<State>>();
		for (State s: states) {
			closure.put(s, new HashSet<State>());
			HashSet<State> visited = new HashSet<State>();
			Queue<State> visiting = new LinkedList<State>();
			visiting.add(s);
			while(!visiting.isEmpty()) {
				State head = visiting.remove();
				visited.add(head);
				for (LabeledEdge e: transition.getAllEdges()) {
					if (e.getSrc().equals(s) && e.isEpsilonEdge() && !visited.contains(e.getDest())) {
						visiting.add(e.getDest());
						closure.get(s).add(e.getDest());
					}
				}
			}			
		}
		
		return closure;
	}
	
	public NFA removeEpsilon() {
		
		HashMap<State,HashSet<State>> closure = computeEClosure();
		NTransition nTran = new NTransition();
		for(State s: states) {
			for(LabeledEdge e: transition.getAllEdges()) {
				if (closure.get(s).contains(e.getSrc()) && !e.isEpsilonEdge()) {
					nTran.addEdge(new LabeledEdge(s,e.getLabel(),e.getDest()));
				}
			}
		}
		
		NFA nfa = new NFA();
		
		return nfa;
	}
	
	public DFA getDFA() {
		
		HashSet<HashSet<State>> stateSets = new HashSet<HashSet<State>>();
		HashSet<State> iniSState = new HashSet<State>();
		DTransition dTran = new DTransition();
		HashSet<HashSet<State>> accSStates = new HashSet<HashSet<State>>();
		
		for(State s: iniStates) {
			iniSState.add(s.clone());
		}

		Queue<HashSet<State>> visiting = new LinkedList<HashSet<State>>();
		visiting.add(iniSState);
		
		while(!visiting.isEmpty()) {
			
			HashSet<State> head = visiting.remove();

			if(!stateSets.contains(head)) {
				stateSets.add(head);
				HashSet<LabeledEdge> edges = transition.getAllEdges();
				for(String a: alphabet.getAlphabet()) {
					HashSet<State> newStateSet = new HashSet<State>();
					boolean isAcceptingState = false;
					for (LabeledEdge edge: edges) {
						if(head.contains(edge.getSrc()) && edge.getLabel().equals(a)) {
							newStateSet.add(edge.getDest());
							if(accStates.contains(edge.getDest())) {
								isAcceptingState = true;
							}
						}
					}
					
					dTran.addEdge(new LabeledEdge(head.toString(),a,newStateSet.toString()));

					if (!stateSets.contains(newStateSet)) {
						visiting.add(newStateSet);
						if (isAcceptingState) {
							accSStates.add(newStateSet);
						}
					}
				}
			}
		}
		
		HashSet<State> castingSSets = new HashSet<State>();
		for (HashSet<State> ss: stateSets) {
			castingSSets.add(new State(ss.toString()));
		}
		HashSet<State> castingAccSStates = new HashSet<State>();
		for(HashSet<State> ss: accSStates) {
			castingAccSStates.add(new State(ss.toString()));
		}
		
		DFA dfa = new DFA(alphabet,castingSSets,dTran,new State(iniSState.toString()),castingAccSStates);
		return dfa;
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
		NFA nfa = new NFA("0 1","q1 q2 q3 q4","q1 0 q1,q1 1 q1,q1 1 q2,q2 0 q2,q2 0 q1,q2 1 q3,q3 0 q3,q3 1 q3,q4 0 q4,q4 1 q3","q1","q3 q4");
		System.out.println(nfa);
		System.out.println(nfa.getDFA().rename("q"));
	}
	
}
