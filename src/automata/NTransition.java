package automata;
import java.io.IOException;
import java.util.*;
import java.util.logging.*;

public class NTransition {
	
	private final static Logger LOGGER = Logger.getLogger(NTransition.class.getName());
	
	private HashMap<Pair<State,String>,HashSet<State>> tranMap;
	
	public NTransition() {
		
		tranMap = new HashMap<Pair<State,String>,HashSet<State>>();
	}
	
	//Create new copy of the map, good for later edit
	@SuppressWarnings("unchecked")
	public NTransition(HashMap<Pair<State,String>,HashSet<State>> map) {
		
		tranMap = (HashMap<Pair<State, String>, HashSet<State>>) map.clone();
	}
	
	public NTransition(String[] args){
		
		 tranMap = new HashMap<Pair<State,String>,HashSet<State>>();
		
		for (String tran: args) {
			String[] components = tran.trim().split("[\\s]+");
			if (components.length == 2){
				addEdge(new LabeledEdge(components[0],"",components[1]));
			} else if (components.length == 3){
				addEdge(new LabeledEdge(components[0],components[1],components[2]));
			} else {
				try {
					FileHandler fh = new FileHandler("logger.log", false);
					fh.setFormatter(new SimpleFormatter());
					LOGGER.addHandler(fh);
					LOGGER.log(Level.WARNING,"\"" + tran + "\" is an invalid transition and will be ignored.");
				} catch (SecurityException | IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public HashSet<LabeledEdge> getAllEdges(){
		HashSet<LabeledEdge> edges = new HashSet<LabeledEdge>();
		for (Map.Entry<Pair<State,String>, HashSet<State>> entry: tranMap.entrySet()) {
			for(State dest: entry.getValue()) {
				edges.add(new LabeledEdge(entry.getKey().getFst(),entry.getKey().getSnd(),dest));
			}
		}
		
		return edges;
	}
	
	public String[] getAllEntries() {
		
		HashSet<LabeledEdge> edges = getAllEdges();
		String[] entries = new String[edges.size()];
		int i=0;
		for (LabeledEdge edge: edges) {
			entries[i++] = edge.toString();
		}
		
		return entries;
	}	
	
	public NTransition clone() {
		
		return new NTransition(tranMap);
	}
	
	public void addEdge(LabeledEdge edge) {
		Pair<State,String> pair = new Pair<State,String>(edge.getSrc(),edge.getLabel());
		if (tranMap.containsKey(pair)) {
			tranMap.get(pair).add(edge.getDest());	
		} else {
			HashSet<State> dests = new HashSet<State>();
			dests.add(new State(edge.getDest()));
			tranMap.put(pair, dests);
		}
	}
	
	public void addEdges(HashSet<LabeledEdge> edges) {
		for (LabeledEdge e : edges) {
			addEdge(e);
		}
	}
	
	public String toString() {
		
		return Arrays.toString(getAllEntries());
	}
	
	public static void main(String[] args) {
		
		NTransition n = new NTransition(new String[] {"q1 q1", "q1    b   q2  ","q1 a q2","q1 a q2","q1 a q1"});
		System.out.println(n);
	}
}
