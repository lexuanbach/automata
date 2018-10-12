package automata;
import java.io.IOException;
import java.util.*;

public class DFA {
	
	private HashSet<State> states;
	private DTransition transition;
	private Alphabet alphabet;
	private State iniState;
	private HashSet<State> accStates;
	
	public DFA() {
		
		states = new HashSet<State>();
		transition = new DTransition();
		alphabet = new Alphabet();
		iniState = new State();
		accStates = new HashSet<State>();
	}
	
	//The standard constructor
	@SuppressWarnings("unchecked")
	public DFA(Alphabet al, HashSet<State> sts, DTransition tran, State ini, HashSet<State> accs) {
		
		states = (HashSet<State>) sts.clone();
		alphabet = al.clone();
		transition = tran.clone();
		iniState  = ini.clone();
		accStates = (HashSet<State>) accs.clone();
	}
	
	//The "raw" constructor
	public DFA(String[] al, String[] sts, String[] trans, String ini, String[] accs) {
		
		alphabet = new Alphabet(al);
		states = new HashSet<State>();
		for(String item: sts) {
			states.add(new State(item));
		}
		transition = new DTransition(trans);
		iniState = new State(ini);
		accStates = new HashSet<State>();
		for(String item: accs) {
			accStates.add(new State(item));
		}
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
		result = result + "Initial state: " + iniState + "\n";
		result = result + "Accepting states: ";
		for(State s: accStates) {
			result = result + s + " ";
		}
		
		return result;
	}
	
	public DFA rename(HashMap<State,State> mapping) {
		
		DFA dfa = new DFA();
		dfa.alphabet = alphabet.clone();
		for (State s: states) {
			dfa.states.add(mapping.get(s).clone());
		}
		for (LabeledEdge edge: transition.getAllEdges()) {
			LabeledEdge newEdge = new LabeledEdge(mapping.get(edge.getSrc()),edge.getLabel(),mapping.get(edge.getDest()));
			dfa.transition.addEdge(newEdge);
		}
		dfa.iniState = mapping.get(iniState).clone();
		for (State s: accStates) {
			dfa.accStates.add(mapping.get(s).clone());
		}
		return dfa;
	}
	
	public DFA rename(String prefix) {
		
		HashMap<State,State> mapping = new HashMap<State,State>();
		int i=1;
		for(State s: states) {
			mapping.put(s.clone(), new State(prefix+(i++)));
		}
		return rename(mapping);
	}
	
	@SuppressWarnings("unchecked")
	public HashSet<State> getReachableStates(State source){
		
		HashMap<State,Boolean> visited = new HashMap<State,Boolean>();
		for(State s: states) {
			visited.put(s, false);
		}
		
		HashSet<State> reachable = new HashSet<State>();
		Queue<State> visiting = new LinkedList<State>();
		visiting.add(source);
		
		while(!visiting.isEmpty()) {
			State head = visiting.remove();
			visited.replace(head, true);
			reachable.add(head);
			for (Pair<State,String> neighbour: transition.getNextStates(head)) {
				if(!visited.get(neighbour.getFst())) {
					visiting.add(neighbour.getFst());
				}
			}
		}

		
		return (HashSet<State>)reachable.clone();
	}
	
	public NFA getNFA() {
		
		NTransition nTran = new NTransition();
		for (LabeledEdge edge: transition.getAllEdges()) {
			nTran.addEdge(edge);
		}
		HashSet<State> iniStates = new HashSet<State>();
		iniStates.add(iniState);
		NFA nfa = new NFA(alphabet,states,nTran,iniStates,accStates);
		return nfa;
	}
	
	@SuppressWarnings("unchecked")
	public HashSet<State> getUnreachableStates(State source){
		
		HashSet<State> unreachable = new HashSet<State>();
		HashSet<State> reachable = getReachableStates(source);
		for(State s: states) {
			if (!reachable.contains(s)) {
				unreachable.add(s);
			}
		}
		return (HashSet<State>)unreachable.clone();
	}
	
	//partition the states into distinguishable state sets
	public HashMap<Pair<State,State>,Boolean> dPartition(){
		
		HashMap<Pair<State,State>,Boolean> table = new HashMap<Pair<State,State>,Boolean>();
		for (State s1: states) {
			for (State s2: states) {
				if (!s1.equals(s2)) {
					if ((accStates.contains(s1)&&!accStates.contains(s2)) || (!accStates.contains(s1)&&accStates.contains(s2))) {//break the symmetry
						table.put(new Pair<State,State>(s1,s2),true);
					} else {
						table.put(new Pair<State,State>(s1,s2),false);
					}
				}
			}
		}
		
		boolean flag = true;
		//Not the best one, but it is good for now
		//Using Myphill-Nerode Theorem
		while(flag) {
			flag = false;
			for (Pair<State,State> currentPair: table.keySet()) {
				if (!table.get(currentPair)) {//current pair is not marked yet
					for (String a: alphabet.getAlphabet()) {//look for the next states to see whether they were already marked
						Pair<State,State> nextPair =  new Pair<State,State>(transition.getNextState(currentPair.getFst(), a),transition.getNextState(currentPair.getSnd(), a));
						if (table.containsKey(nextPair) && table.get(nextPair)) {//if so then mark the current pair
							table.replace(currentPair, true);
							flag = true;
						}
					}
				}
			}
		}
		
		return table;
	}
	
	public DFA getMinimalDFA() {
		
		DFA temp = getCompleteDFA();
		for (State s: getUnreachableStates(iniState)) {
			temp.transition.removeState(s);
			temp.accStates.remove(s);
			temp.states.remove(s);
		}

		HashMap<Pair<State,State>,Boolean> table = temp.dPartition();
		
		for (Pair<State,State> pair: table.keySet()) {
			if (!table.get(pair)) {//redundant
				if (temp.states.contains(pair.getFst())) {
					if (temp.states.contains(pair.getSnd())) {//remove the first state
						
						for (LabeledEdge edge: temp.transition.getAllEdges()) {
							temp.transition.removeEdge(edge);
							//We need to rename accordingly
							State newSrc = edge.getSrc();
							State newDest = edge.getDest();
							if (newSrc.equals(pair.getFst())) {
								newSrc = pair.getSnd();
							}
							if (newDest.equals(pair.getFst())) {
								newDest = pair.getSnd();
							}
							temp.transition.addEdge(new LabeledEdge(newSrc,edge.getLabel(),newDest));
						}
						temp.accStates.remove(pair.getFst());
						temp.states.remove(pair.getFst());
						if(pair.getFst().equals(temp.iniState)) {
							//if the removed state is the initial state
							//then we need to replace it with the second state
							temp.iniState = pair.getSnd();
						}
					}
				}
			}
		}
		
		return temp;
	}
	
	@SuppressWarnings("unchecked")
	public DFA clone() {
		
		return new DFA(alphabet.clone(),(HashSet<State>)(states.clone()),transition.clone(),iniState.clone(),(HashSet<State>)accStates.clone());
	}
	
	public boolean runWord(String[] word, int start, State current, ArrayList<State> visited) {
		
		if(start < 0) return false;

		if (start >= word.length) {
			visited.add(current);
			if (accStates.contains(current)){
				return true;
			}
		} else {
			visited.add(current);
			State next = transition.getNextState(current, word[start]);
			if (next != null) {
				return runWord(word, start+1, next, visited);
			} else {
				return false;
			}
		}
		
		return false;
		
	}
		
	public boolean runWord(String[] word, ArrayList<State> visited) {
		
		return runWord(word,0,iniState, visited);
	}
	
	public boolean runWord(String[] word) {
		
		return runWord(word, new ArrayList<State>());
	}
	
	//Add extra transitions to make the automaton complete
	// where deadState is the name for the dead-end state
	public DFA getCompleteDFA(State deadState) {
		
		DFA temp = clone();
		
		boolean flag = false;
		for (State s: temp.states) {
			for (String a: temp.alphabet.getAlphabet()) {
				if (temp.transition.getNextState(s,a) == null) {
					temp.transition.addEntry(s, a, deadState);
					flag = true;
				}
			}
		}
		if(flag) {
			temp.states.add(deadState);
			for(String a: temp.alphabet.getAlphabet()) {
				temp.transition.addEntry(deadState, a, deadState);	
			}
		}
		
		return temp;
	}
	
	public DFA getCompleteDFA() {
		
		return getCompleteDFA(new State("DEAD"));
	}
	
	public DFA getComplement() {
		
		DFA temp = getCompleteDFA();
		HashSet<State> newaccStates = new HashSet<State>();
		for (State s: temp.states) {
			if (!temp.accStates.contains(s)) {
				newaccStates.add(s);
			}
		}
		temp.accStates = newaccStates;
		return temp;
	}

	
	public Set<State> getAllStates(){
		return states;
	}
	
	//Extract only states that appear in the transition function
	public Set<State> getAllStatesFromTrans(){
		
		Set<State> stateList = new HashSet<State>();
		
		for (LabeledEdge e: transition.getAllEdges()) {
			stateList.add(e.getSrc());
			stateList.add(e.getDest());
		}
		//Add initial state and accepting states
		stateList.add(iniState);
		for (State s: accStates) {
			stateList.add(s);
		}
		
		return stateList;
	}
	
	//Search whether the automaton accepts any word at all
	//where " " represents the empty string
	public String getAcceptedWord() {
		
		if (accStates.contains(iniState)) {
			return " ";
		}
		
		HashMap<State,String> visited = new HashMap<State,String>();
		for (State current: getAllStates()) {
			visited.put(current, "");
		}
		
		Queue<State> visiting = new LinkedList<State>();
		visiting.add(iniState);

		while(!visiting.isEmpty()) {
			State head = visiting.remove();

				for (Pair<State,String> neighbour: transition.getNextStates(head)) {

					if (accStates.contains(neighbour.getFst())) {
						return visited.get(head) + neighbour.getSnd();
					} else {
						if (visited.get(neighbour.getFst()).equals("")) {
							visiting.add(neighbour.getFst());	
							visited.replace(neighbour.getFst(), visited.get(head) + neighbour.getSnd() + " ");
						}
					}
				}
		}
		
		return "";
	}
	
	public boolean isEmpty() {
		return getAcceptedWord().equals("");
	}
	
	public void draw(String fileName, String type) throws IOException {
		getNFA().draw(fileName, type);
	}
	
	public void draw(String fileName) throws IOException {
		getNFA().draw(fileName);
	}
	
	public static void main(String[] args) throws CloneNotSupportedException {
		DFA dfa = new DFA(new String[] {"0","1"},new String[] {"q0","q1","q2","q3","q4","q5"}, new String[] {"q0 0 q3","q0 1 q1","q1 0 q2","q1 1 q5","q2 0 q2","q2 1 q5","q3 0 q0","q3 1 q4","q4 0 q2","q4 1 q5","q5 0 q5","q5 1 q5"}, new String("q0"), new String[] {"q1","q2","q4"});
		System.out.println(dfa + "\n");
		dfa.dPartition();
		System.out.println("MIN: ");
		System.out.println(dfa.getMinimalDFA());
	}
}
