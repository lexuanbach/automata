package automata;

import java.io.File;
import java.io.IOException;
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
	
	public NTransition getTransition() {
		return transition;
	}
	
	public HashSet<State> getIniStates(){
		return iniStates;
	}
	
	public HashSet<State> getAccStates(){
		return accStates;
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
		
		NFA unionA = nfa.removeEpsilon().rename("u");
		NFA temp = removeEpsilon().rename("t");
		
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
	
	public boolean isEmpty() {
		
		return getDFA().isEmpty();
	}
	
	public boolean isSubLanguage(NFA nfa) {
		return getIntersection(nfa.getComplement()).isEmpty();
	}
	
	public boolean isEquiv(NFA nfa) {
		return isSubLanguage(nfa) && nfa.isSubLanguage(this);
	}
	
	public NFA getIntersection(NFA nfa) {
		NFA nfa1 = clone().removeEpsilon().rename("t");
		NFA nfa2 = nfa.clone().removeEpsilon().rename("r");
		
		Alphabet newAl = nfa1.alphabet.clone();
		for(String a: nfa2.alphabet.getAlphabet()) {
			newAl.addLetter(a);
		}
		
		HashSet<Pair<State,State>> pairStates = new HashSet<Pair<State,State>>();

		HashSet<Pair<State,State>> iniPStates = new HashSet<Pair<State,State>>();
		HashSet<Pair<State,State>> accPStates = new HashSet<Pair<State,State>>();
		for (State s1: nfa1.iniStates) {
			for (State s2: nfa2.iniStates) {
				Pair<State,State> p = new Pair<State,State>(s1,s2);
				iniPStates.add(p);
				if (nfa1.accStates.contains(s1) && nfa2.accStates.contains(s2)) {
					accPStates.add(p);
				}
			}
		}


		NTransition nTran = new NTransition();
		Queue<Pair<State,State>> visiting = new LinkedList<Pair<State,State>>();
		for (Pair<State,State> p : iniPStates) {
			visiting.add(p);
		}
		
		while (!visiting.isEmpty()) {
			Pair<State,State> head = visiting.remove();
			pairStates.add(head);
			for (LabeledEdge e1: nfa1.transition.getAllEdges()) {
				for (LabeledEdge e2: nfa2.transition.getAllEdges()) {
					Pair<State,State> pDest = new Pair<State,State>(e1.getDest(),e2.getDest());
					if (head.getFst().equals(e1.getSrc()) && head.getSnd().equals(e2.getSrc()) && e1.getLabel().equals(e2.getLabel())) {
						LabeledEdge pairEdge = new LabeledEdge(head.toString(),e1.getLabel(),pDest.toString());
						nTran.addEdge(pairEdge);
						
						if(!pairStates.contains(pDest) && !visiting.contains(pDest)) {
							visiting.add(pDest);
						}
						if (nfa1.accStates.contains(pDest.getFst()) && nfa2.accStates.contains(pDest.getSnd())) {
							accPStates.add(pDest);
						}
					}
					
				}
			}
		}
		
		HashSet<State> castingPStates = new HashSet<State>();
		for (Pair<State,State> p: pairStates) {
			castingPStates.add(new State(p.toString()));
		}
		HashSet<State> castingIniPStates = new HashSet<State>();
		for(Pair<State,State> p: iniPStates) {
			castingIniPStates.add(new State(p.toString()));
		}
		HashSet<State> castingAccPStates = new HashSet<State>();
		for(Pair<State,State> p: accPStates) {
			castingAccPStates.add(new State(p.toString()));
		}
		
		return new NFA(newAl,castingPStates,nTran,castingIniPStates,castingAccPStates).rename("q");
	}
	
	public NFA getComplement() {
		return getDFA().getComplement().getNFA();
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
				if (e.getSrc().equals(s) && !e.isEpsilonEdge()) {
					nTran.addEdge(e);
				}
				
				if (closure.get(s).contains(e.getSrc()) && !e.isEpsilonEdge()) {
					nTran.addEdge(new LabeledEdge(s,e.getLabel(),e.getDest()));
				}
			}
			
		}
		
		@SuppressWarnings("unchecked")
		HashSet<State> newAccStates = (HashSet<State>) accStates.clone();
		for (State s: states) {
			for (State dest: closure.get(s)) {
				if (accStates.contains(s)) {
					newAccStates.add(dest);
				}
				if (accStates.contains(dest)) {
					newAccStates.add(s);
				}
			}
		}
		
		NFA nfa = new NFA(alphabet,states,nTran,iniStates,newAccStates);
		
		return nfa;
	}
	
	public DFA getDFA() {
		
		HashSet<HashSet<State>> stateSets = new HashSet<HashSet<State>>();
		HashSet<State> iniSState = new HashSet<State>();
		DTransition dTran = new DTransition();
		HashSet<HashSet<State>> accSStates = new HashSet<HashSet<State>>();
		NFA withoutEpsilon = clone().removeEpsilon();
		
		for(State s: withoutEpsilon.iniStates) {
			iniSState.add(s.clone());
		}
		
		for (State s: iniSState) {
			if (withoutEpsilon.accStates.contains(s)) {
				accSStates.add(iniSState);
				break;
			}
		}

		Queue<HashSet<State>> visiting = new LinkedList<HashSet<State>>();
		visiting.add(iniSState);
		
		while(!visiting.isEmpty()) {
			
			HashSet<State> head = visiting.remove();

			if(!stateSets.contains(head)) {
				stateSets.add(head);
				HashSet<LabeledEdge> edges = withoutEpsilon.transition.getAllEdges();
				for(String a: withoutEpsilon.alphabet.getAlphabet()) {
					HashSet<State> newStateSet = new HashSet<State>();
					boolean isAcceptingState = false;
					for (LabeledEdge edge: edges) {
						if(head.contains(edge.getSrc()) && edge.getLabel().equals(a)) {
							newStateSet.add(edge.getDest());
							if(withoutEpsilon.accStates.contains(edge.getDest())) {
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
	
	//Record the trace of the run on word and return True iff it is accepted
	public boolean getTrace(String[] word,ArrayList<HashSet<LabeledEdge>> trace){
		HashSet<State> currentStates = iniStates;

		for (int i=0;i<word.length && !currentStates.isEmpty();i++) {
			HashSet<State> nextStates = new HashSet<State>();
			trace.add(new HashSet<LabeledEdge>());
			for (LabeledEdge e: transition.getAllEdges()) {
				if (currentStates.contains(e.getSrc()) && e.getLabel().equals(word[i])) {
					nextStates.add(e.getDest());
					trace.get(i).add(e);
				}
			}
			currentStates = nextStates;
		}

		for (State s: currentStates) {
			if (accStates.contains(s)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isAccepted(String[] word) {
		return getTrace(word, new ArrayList<HashSet<LabeledEdge>>());
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
	
	 
	 public void draw(String fileName, String type) throws IOException {
		 
		 GraphViz gv = new GraphViz();
	     gv.addln(gv.start_graph());
	     
		 gv.addln("rankdir=LR;");
		 
	     String finalStates = "node [shape = doublecircle]";
	     for (State s: getAccStates()) {
	    	 finalStates = finalStates + " \"" + s + "\"";
	     }
	     finalStates = finalStates + ";";

	     gv.addln(finalStates);
	     gv.addln("node [shape = none]; \"\"");
	     gv.addln("node [shape = circle];");
	     
	     for (LabeledEdge e: getTransition().getAllEdges()) {
	    	 String tran = "\"" + e.getSrc()+ "\" -> \"" + e.getDest() + "\"[label=\"" + e.getLabel() + "\"];";
	    	 gv.addln(tran);
	     }
	     for (State iniState: getIniStates()) {
	    	 gv.addln("\"\" -> " + iniState + ";");
	     }

	     gv.addln(gv.end_graph());
	     
	     gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), new File(fileName + "." + type) );
	     gv.writeDotSourceToFile(gv.getDotSource(), fileName + ".dot");
	 }
	 
	 public void draw(String fileName) throws IOException {
		 draw(fileName,"pdf");
	 }
	 
	
	
	public static void main(String[] args) throws IOException {
		NFA nfa = new NFA("0 1","q1 q2 q3 q4","q1 0 q1,q1 1 q1,q1 1 q2,q2 0 q2,q2 0 q1,q2 1 q3,q3 0 q3,q3 1 q3,q4 0 q4,q4 1 q3","q1","q3 q4");
		System.out.println(nfa);
		System.out.println(nfa.getDFA().rename("q"));
		NFA nfa1 = new NFA("0","r q1 q2 q3 s1 s2 s3 s4 s5","q1 0 q2,q2 0 q3,q3 0 q1,s1 0 s2,s2 0 s3,s3 0 s4,s4 0 s5,s5 0 s1,r s1,r q1","r","q1 s1");
		
		System.out.println(nfa1);
		System.out.println(nfa1.getDFA().getMinimalDFA());
		System.out.println("+++++++++++++++++++");
		NFA a1 = new NFA("1","q1 q2 q3","q1 1 q2,q2 1 q3,q3 1 q1","q1","q1 q2");
		NFA a2 = new NFA("1","s1 s2 s3 s4 s5","s1 1 s2,s2 1 s3,s3 1 s4,s4 1 s5,s5 1 s1","s1","s1 s4");
		NFA b1 = a1.getIntersection(a2);
		b1.getDFA().rename("q").getNFA().draw("out","pdf");
		NFA a3 = new NFA("1","q1 q2 q3 q4 q5 q6","q1 1 q2,q2 1 q3,q3 1 q4,q4 1 q5,q5 1 q6,q6 1 q1","q1","q1 q3 q5");
		a3.draw("a", "pdf");
		a3.getDFA().getMinimalDFA().rename("q").draw("a3");
	}
	
}
