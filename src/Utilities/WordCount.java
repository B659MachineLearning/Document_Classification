package Utilities;

public class WordCount {
	public String word;
	public int count;
	
	public WordCount(String w, int c){
		this.word = w;
		this.count = c;
	}
	
	public String toString(){
		return this.word+":"+this.count;
	}
	
	
}
