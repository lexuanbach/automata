package automata;
import java.util.*;
import java.util.regex.Pattern;

public class DTransition {
	
	private Hashtable<String,State> tranMap;
	
	public DTransition() {
		tranMap = new Hashtable<String,State>();
	}
	
	public void addEntry(State src, String a, State dest) {
		
		Pair<State,String> p = new Pair<State,String>(src,a);
		tranMap.put(p.toString(), dest);
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
				
				String src = element.substring(0,element.indexOf(" "));
				element = element.substring(element.indexOf(" ")+1);
				String dest = element.substring(element.lastIndexOf(" ")+1);
				String a = element.substring(0,element.lastIndexOf(" "));
				
				addEntry(src,a,dest);
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
		
		Pair<State,String> p = new Pair<State,String>(s,a);
		return tranMap.get(p.toString());
	}
	//Shortcut
	public State getNextState(String s, String a) {
		
		return getNextState(new State(s), a);
	}
	
	public void removeEntry(State s, String a) {
		
		Pair<State,String> p = new Pair<State,String>(s,a);
		tranMap.remove(p.toString());
	}
	//Shortcut
	public void removeEntry(String s, String a) {
		removeEntry(new State(s), a);
	}
	
	public String[] getAllEntries() {
		
		String[] entries = new String[tranMap.size()];
		int i = 0;
		for ( Map.Entry<String, State> entry : tranMap.entrySet()) {
		    String key = entry.getKey();
		    State dest = entry.getValue();
		    Pair<String,String> pair = Utility.parsePair(key);
		    entries[i++] =  pair.getFst() + " " + pair.getSnd() + " " + dest;

		}
		
		return entries;
		
	}
	
	public String toString() {
		
		return Arrays.toString(getAllEntries());
	}
	
	@SuppressWarnings("unchecked")
	public void removeState(State s) {
		//Need to clone it to avoid concurrent modification bug

		for ( Map.Entry<String, State> entry : 
			((Hashtable<String, State>) tranMap.clone()).entrySet()) {
			String key = entry.getKey();
			Pair<String,String> pair = Utility.parsePair(key);
			if (pair.getFst().equals(s.getName())) {
				tranMap.remove(key);
			}
		}
	}
	//Shortcut
	public void removeState(String s) {
		removeState(new State(s));
	}

	public static void main(String[] args) {
		DTransition tran = new DTransition();
		tran.addEntries(new String[]{"q1 a q2","q2 b q2","q1 b q1"});
		System.out.println(tran);
		tran.removeState("q1");
		System.out.println(tran);
	}

	
}
