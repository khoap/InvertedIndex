package io;

import java.io.*;
import java.util.*;

/*
 * IO Class. 
 * Author: Khoa Pham - khoa.pham@me.com - all rights reserved.
 * - To read text from file.
 * - Get Document ID.
 * - Get Tweet.
 * - Convert tweet string to array of words.
 * - Get Prefix of a String if String input has as a suffix.
 */
public class IO {
	public IO() {}
	
	/*
	 * function: readFile.
	 * param: filename
	 * return: Vector<String>.
	 */
	public Vector<String> readFile(String filename) throws IOException{
		File inputFile = new File(filename);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
		Vector<String> storedData = new Vector<String>();
		String inputLine;
		while((inputLine = bufferedReader.readLine()) != null) { storedData.add(inputLine);}
		return storedData;
	}
	
	/*
	 * function: splitQuery.
	 * Remove the boolean word such as AND in the query and put the rest in the Vector String.
	 * param: booleanQuery.
	 * return: Vector<String>.
	 */
	public Vector<String> splitQuery(String booleanQuery) {
		Vector<String> result = new Vector<String>();
		StringTokenizer st = new StringTokenizer((booleanQuery.replaceAll("AND", "")));
		while (st.hasMoreTokens()) {
			result.add(this.removePluralSuffix((st.nextToken().toLowerCase())));
		}
		return result;
	}
	/*
	 * function: getDocsID.
	 * param: a tweet which is read from Vector<String>.
	 * return: String.
	 */
	public String getDocsID(String tweet) {
		if(tweet.indexOf("\t") < 0) { 
			return tweet.substring(0, tweet.indexOf(' '));
		}
		else {
			return tweet.substring(0, tweet.indexOf("\t"));
		}
	}
	
	/*
	 * function: getTweet.
	 * param: a tweet.
	 * return: String.
	 */
	public String getTweet(String tweet) {
		if(tweet.indexOf("\t") < 0) {
			return tweet.substring(tweet.indexOf(' '));	
		}
		else {
			return tweet.substring(tweet.indexOf("\t"));
		}
	}
	
	/*
	 * function: StringToWord.
	 * param: a tweet.
	 * return: Vector<String> after tokenizing and remove plural suffix
	 */
	public Vector<String> StringToWord(String tweet) {
		Vector<String> result = new Vector<String>();
		Set<String> terms = new HashSet<String>();
		StringTokenizer st = new StringTokenizer(tweet);
		while (st.hasMoreTokens()) {
			terms.add(this.removePluralSuffix(st.nextToken().toLowerCase()));
		 }
		result.addAll(terms);
		return result;
	}
	
	/*
	 * function: removeSuffixPlural.
	 * This function is used to replace the plural words into the singular words.
	 * By just checking if a term ends with "s", then delete it.
	 * param: String aString.
	 * return: aString after removing the plural suffix.
	 */
	public String removePluralSuffix(String aString) {
		if(aString.endsWith("s"))
			return aString.substring(0, aString.length()-1);
		return aString;
	}
	/*
	 * function: getPrefix.
	 * param: a st.
	 * return: prefix of string if not null.
	 */
	public String getPrefix(String st) {
		if(st.contains("*"))
			return st.substring(0, st.indexOf("*"));
		return null;
	}
	
	/*
	 * function: printStream to file.
	 * param: String dir.
	 * return: none.
	 */
	public void printStream (PrintStream pst) throws FileNotFoundException {
		System.setOut(pst);
	}
	public void closeStream(PrintStream pst) {
		pst.close();
	}

}
