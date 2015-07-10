package Utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class Utils {

	//Method to remove stop words from a given string. Uses Lucene list of stop words and a custom list stored in the stopwords.txt file
	public static String removeStopWords(String textFile) throws Exception {
	    //CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
		
		StandardAnalyzer analyser = new StandardAnalyzer();
	    TokenStream tokenStream = new StandardTokenizer(new StringReader(textFile.trim()));
	    tokenStream = new StopFilter(tokenStream, analyser.STOP_WORDS_SET);
	    
	    BufferedReader br = new BufferedReader(new FileReader("stopwords.txt"));
	    String line = null;
	    List<String> stopw = new ArrayList<String>();
	    int i =0;
	    line = br.readLine();
	    while(line != null){
		   stopw.add(line);
		   line = br.readLine();
		}
	    
	    tokenStream = new StopFilter(tokenStream, StopFilter.makeStopSet(stopw));
	    
	    StringBuilder sb = new StringBuilder();
	    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
	    tokenStream.reset();
	    while (tokenStream.incrementToken()) {
	        String term = charTermAttribute.toString();
	        if(term.length() > 2)
	        	sb.append(term + " ");
	    }
	    return sb.toString();
	}
	
	//Method to get the unique words in the given String
	public static String getWords(String fileName){
		String result = null;
		try {
			FileInputStream fin = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));
			
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			while(line != null){
				sb.append(line);
				line = br.readLine();
			}
			br.close();
			result = Utils.removeStopWords(sb.toString());
			return result;
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		}
	}
	
	//Method to do the word count
	public static void countWords(String text, HashMap<String, Integer> counts){
		StringTokenizer st = new StringTokenizer(text);
		String currWord = null;
		while(st.hasMoreTokens()){
			currWord = st.nextToken();
			if(counts.containsKey(currWord)){
				counts.put(currWord, counts.get(currWord)+1);
			}
			else{
				counts.put(currWord, 1);
			}
		}
	}
	
	//Method to put the words and their counts into priority queue for sorting purpose
	public static PriorityQueue<WordCount> sortWords(HashMap<String, Integer> counts){
		PriorityQueue<WordCount> words = new PriorityQueue<WordCount>(99999, new wordCountComparator());
		WordCount wc = null;
		for(Map.Entry<String, Integer> entry : counts.entrySet()){
			wc = new WordCount(entry.getKey(), entry.getValue());
			words.add(wc);
		}
		return words;
	} 
	
/*	public static void main(String[] args){
		String textFile = new String("My name is name Mayur Name Tare. I NAME was Computer the great");
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		PriorityQueue<WordCount> p = new PriorityQueue<WordCount>();
		try {
			System.out.println(removeStopWords(textFile));
			
			countWords(textFile, counts);
			String s = "bongo's";
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	//Method to create Dataset.csv
	public static void createCSV(HashSet<String> features) throws IOException{
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("dataset.csv"), "utf-8"));
		ArrayList<String> featureList = new ArrayList<String>();
		featureList.addAll(features);
		
		for(String f : featureList){
			if(f.contains("\'")){
				f = f.replace('\'', '*');
			}
			writer.write(f+",");
		}
		writer.write("ClassLable");
		writer.write(System.lineSeparator());
		
		for(Entry<String, String> ent : Classification.docText.entrySet()){
			for(String f : featureList){
				if(ent.getValue().toLowerCase().contains(f)){
					writer.write("1,");
				}
				else{
					writer.write("0,");
				}
			}
			String category = Classification.docLable.get(ent.getKey());
			writer.write('0'+Classification.categories.indexOf(category));
			writer.write(System.lineSeparator());
		}
		writer.close();
	}
}

//Comparator class to implement a custom comparator for WordCount object.
class wordCountComparator implements Comparator{

	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		WordCount wc1 = (WordCount) arg0;
		WordCount wc2 = (WordCount) arg1;
		
		if(wc1.count > wc2.count){
			return -1;
		}
		else if(wc1.count < wc2.count){
			return 1;
		}
		return 0;
	}
	
}
