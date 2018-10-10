package automata;
import java.util.*;
import java.util.regex.Pattern;

public class DTransition {
	
	private Hashtable<Pair<State,String>,State> tranMap;
	
	public DTransition() {
		tranMap = new Hashtable<Pair<State,String>,State>();
	}
	
	public DTransition clone() {
		
		DTransition newTran = new DTransition();
		for (LabeledEdge e: getAllEdges()) {
			newTran.addEdge(e);
		}
		return newTran;
	}
	
	public DTransition(String[] trans) {
		tranMap = new Hashtable<Pair<State,String>,State>();
		addEntries(trans);
	}
	
	public void addEntry(State src, String a, State dest) {
		
		tranMap.put(new Pair<State,String>(src,a), new State(dest));
	}
	//Shortcut
	public void addEntry(String src, String a, String dest) {
		
		addEntry(new State(src), a, new State(dest));
	}
	//Entries need to have the form s1 a s2
	public void addEntries(String[] entryList) {
		
		for (int i=0;i<entryList.length;i++) {
			String element = entryList[i];

			if (Pattern.matches("[\\w]+\\s[\\w(,)]+\\s[\\w]+", element)) {
				LabeledEdge edge = new LabeledEdge(element);
				addEntry(edge.getSrc(),edge.getLabel(),edge.getDest());
			}
		}
	}
	
	public Set<Pair<State,String>> getNextStates(State s) {
		
		Set<Pair<State,String>> stateList = new HashSet<Pair<State,String>>();
		String[] entries = getAllEntries();
		for (String entry:entries) {
			LabeledEdge edge = new LabeledEdge(entry);
			if (edge.getSrc().equals(s)) {
				stateList.add(new Pair<State, String>(edge.getDest(),edge.getLabel()));
			}
		}
		return stateList;
	}
	
	public State getNextState(State s, String a) {
		
		return tranMap.get(new Pair<State,String>(s,a));
	}
	//Shortcut
	public State getNextState(String s, String a) {
		
		return getNextState(new State(s), a);
	}
	
	public void removeEntry(State s, String a) {
		
		tranMap.remove(new Pair<State,String>(s,a));
	}
	//Shortcut
	public void removeEntry(String s, String a) {
		removeEntry(new State(s), a);
	}
	
	public void removeEdge(LabeledEdge edge) {
		removeEntry(edge.getSrc(),edge.getLabel());
	}
	
	public void addEdge(LabeledEdge edge) {
		addEntry(edge.getSrc(),edge.getLabel(),edge.getDest());
	}
	
	public HashSet<LabeledEdge> getAllEdges(){
		
		HashSet<LabeledEdge> edges = new HashSet<LabeledEdge>();
		
		for (Map.Entry<Pair<State,String>, State> entry: tranMap.entrySet()) {
			Pair<State,String> key = entry.getKey();
			State dest = entry.getValue();
			edges.add(new LabeledEdge(new State(key.getFst()),key.getSnd(),dest));
		}
		
		return edges;
	}
	
	public String[] getAllEntries() {
		
		HashSet<LabeledEdge> edges = getAllEdges();
		String[] entries = new String[edges.size()];
		int i = 0;
		
		for (LabeledEdge item: edges) {
			entries[i++] = item.toString();
		}
		
		return entries;
		
	}
	
	public String toString() {
		
		return Arrays.toString(getAllEntries());
	}
	
	public void removeState(State s) {
		
		HashSet<LabeledEdge> edges = getAllEdges();
		for(LabeledEdge edge: edges) {
			if (edge.getSrc().equals(s) || edge.getDest().equals(s)) {
				removeEntry(edge.getSrc(),edge.getLabel());
			}
		}
	}
	//Shortcut
	public void removeState(String s) {
		removeState(new State(s));
	}
}
