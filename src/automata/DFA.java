package automata;
import java.util.*;

public class DFA {
	
	private HashSet<State> states;
	private DTransition transition;
	private Alphabet alphabet;
	private State iniState;
	private Set<State> accStates;
	
	public DFA() {}
	
	//The standard constructor
	public DFA(Alphabet al, HashSet<State> sts, DTransition tran, State ini, Set<State> accs) {
		
		states = sts;
		alphabet = al;
		transition = tran;
		iniState  = ini;
		accStates = accs;
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
	
	//Add extra transitions to make the automaton complete
	// where deadState is the name for the dead-end state
	public DFA getCompleteDFA(String deadState) {
		
		
		return null;
	}
	
	public boolean runWord(String[] word, ArrayList<State> visited) {
		
		return runWord(word,0,iniState, visited);
	}
	
	public boolean runWord(String[] word) {
		
		return runWord(word, new ArrayList<State>());
	}
	
	public Set<State> getAllStates(){
		return states;
	}
	
	//Extract only states that appear in the transition function
	public Set<State> getAllStatesFromTrans(){
		
		Set<State> stateList = new HashSet<State>();
		String[] entries = transition.getAllEntries();
		
		for (String item : entries) {
			String src = item.substring(0, item.indexOf(" "));
			String dest = item.substring(item.lastIndexOf(" ") + 1);
			stateList.add(new State(src));
			stateList.add(new State(dest));
		}
		//Add initial state and accepting states
		stateList.add(iniState);
		for (State temp: accStates) {
			stateList.add(temp);
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
	
	public static void main(String[] args) {
		DFA dfa = new DFA(new String[] {"a","b"},new String[] {"q1","q2","q3"}, new String[] {"q1 a q2","q3 b q3","q2 a q1", "q2 b q3", "q1 b q3"}, new String("q1"), new String[] {"q3"});
		System.out.println(dfa);
		System.out.println("aa\naaaa");
		String[] word = {"a","b","b","b"};
		ArrayList<State> visit = new ArrayList<State>();
		System.out.println(dfa.runWord(word,visit));
		for(int i=0;i<visit.size();i++){
		    System.out.println(visit.get(i));
		} 
		System.out.println("End here");
		Set<State> check = dfa.getAllStates();
		for (State temp: check) {
			System.out.println(temp);
		}
		
		HashSet<String> test = new HashSet<String>();
		test.add("a");
		test.add("b");
		test.add("c");
		@SuppressWarnings("unchecked")
		HashSet<String> test2 = (HashSet<String>)test.clone();
		for(String temp:test2) {
			test.remove(temp);
			System.out.println(temp);
		}
		
		System.out.println(test.size());
		System.out.println(dfa.getAcceptedWord());
		System.out.println(dfa.isEmpty());
	}
}
