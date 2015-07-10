package Utilities;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

import Utilities.Utils;
import Utilities.WordCount;



public class Classification {
	
	//File containing doc ids for category
	public static String fileName = "DocInfo\\";
	//File containing text for all documents
	public static String textFile = "Document Text AFP & CNA.txt";
	
	public static HashMap<String, String> docText = new HashMap<String, String>();
	public static HashMap<String, String> docLable = new HashMap<String, String>();	
	
	public static ArrayList<String> categories = new ArrayList<String>();
	
	public static void main(String[] args){
		Scanner s = new Scanner(System.in);
		System.out.print("Enter number of words for each category : ");
		int numberOfWords = s.nextInt();
		
		HashSet<String> features = getFeatures(numberOfWords);
		try {
			Utils.createCSV(features);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//Returns the Set of Features for all Categories add to the list
	public static HashSet<String> getFeatures(int countForEachCat){
		int count = 0;
		HashSet<String> features = new HashSet<String>();
		try {
			categories.add("finance");
			categories.add("health");
			categories.add("industry");
			categories.add("people");
			categories.add("politics");
			categories.add("science");
			categories.add("sports");
			categories.add("world");
			
			//Iterate over categories
			for(String cat : categories){				
				Set<String> docIds = new HashSet<String>();
				HashMap<String, Integer> counts = new HashMap<String, Integer>();
				PriorityQueue<WordCount> p = new PriorityQueue<WordCount>();
				
				FileInputStream fin = new FileInputStream(fileName+cat);
				BufferedReader br = new BufferedReader(new InputStreamReader(fin));
				
				String line = br.readLine();
				String currDocId = null;
				
				while(line != null){
					if(line.contains("(") && line.contains(")")){
						currDocId = line.substring(line.indexOf('(')+1, line.indexOf(')'));
						//System.out.println(currDocId);
						docIds.add(currDocId);
					}
					line = br.readLine();
				}
				
				fin.close();
				br.close();
				
				fin = new FileInputStream(textFile);
				br = new BufferedReader(new InputStreamReader(fin));
				
				line = br.readLine();
				int startIndex = 0;
				StringBuilder headLineText = new StringBuilder();
				StringBuilder text = new StringBuilder();
				
				while(line != null){
					if(line.startsWith("<DOC id=")){
						startIndex = line.indexOf("\"");
						currDocId = line.substring(startIndex+1, line.indexOf('\"', startIndex+1));
						
						if(docIds.contains(currDocId)){
							docLable.put(currDocId, cat);
							
							StringBuilder htext = new StringBuilder();
							StringBuilder ttext = new StringBuilder();
							line = br.readLine();
							while(!line.contains("<HEADLINE>")){
								if(line.contains("<TEXT>") || line.contains("<DOC id=")){
									break;
								}
								line = br.readLine();
							}
							//count++;
							if(line.contains("<HEADLINE>")){
								while(!line.endsWith("</HEADLINE>")){
									line = br.readLine();
									if(!line.contains("</HEADLINE>"))
										headLineText.append(line+" ");
								}
							}
							
							while(!line.contains("<TEXT>")){
								if(line.contains("<DOC id=")){
									break;
								}
								line = br.readLine();
							}
							
							if(line.contains("<TEXT>")){
								while(!line.endsWith("</TEXT>")){
									line = br.readLine();
									if(!line.contains("</TEXT>") && !line.contains("<P>") && !line.contains("</P>"))
										text.append(line+" ");
								}
							}
							
							//Collect the text for each document
							htext.append(" "+headLineText);
							ttext.append(" "+text);
							docText.put(currDocId, htext.append(" "+ttext.toString()).toString());
						}
					}
					line = br.readLine();
				}
					
				headLineText.append(text.toString());
				
				//Use utility functions to get the word counts 
				Utils.countWords(Utils.removeStopWords((headLineText.toString()).toLowerCase()), counts);
				//Sort the words according to their counts
				p = Utils.sortWords(counts);
				
				String currFeature = null;
				//Remove the duplicate features
				for(int i = 0; i < countForEachCat; i++){
					currFeature = p.poll().word;
					features.add(currFeature);
				}
				
				fin.close();
				br.close();

			}
		
			System.out.println(features.size());
			/*for(String s : features){
				System.out.println(s);
			}*/
			return features;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return features;
		}
	}
	
	
}
