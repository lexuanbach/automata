package automata;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Utility {
	
	public static<T> ArrayList<T> copy(ArrayList<T> list){
		
		ArrayList<T> newList = new ArrayList<T>();
		for (int i=0;i<list.size();i++) {
			newList.add(list.get(i));
		}
		
		return newList;
	}
	
	public static Pair<String,String> parsePair(String p){
		Pair<String,String> pair = new Pair<String,String>("Err","Err");
		if (Pattern.matches("<[\\w]+,[\\w]+>", p)) {

			p  = p.substring(1, p.length()-1);
			pair.setFst(p.substring(0,p.indexOf(",")));
			pair.setSnd(p.substring(p.indexOf(",") + 1));
		}
		
		return pair;
	}

}
