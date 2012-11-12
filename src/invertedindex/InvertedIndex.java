package invertedindex;

import io.IO;
import java.io.*;
import java.util.*;

import javax.print.DocFlavor.URL;

import sort.SelectionSort;

/*
 *  InvertedIndex Class.
 *  Author: Khoa Pham - khoa.pham@me.com - all rights reserved.
 * - To perform main task of the project. Including main function.
 * - Implementation based on: http://nlp.stanford.edu/IR-book/html/htmledition/a-first-take-at-building-an-inverted-index-1.html
 */
public class InvertedIndex {
	public InvertedIndex() {}
	String regex_declaration = "[a-zA-Z_0-9[_-][%]]*";
	IO io = new IO();
	SelectionSort sectsort = new SelectionSort();
	/*
	 * function: getTermInDoc. 
	 * The function will use IO class instance io to read from file, and then perform intermediate pre-language processing
	 * to delete unwanted characters such as *, !, . , ...
	 * param: aTweet.
	 * return: Vector<TermDocsNode> which holds the Vectors of all terms in each document.
	 */
	public Vector<TermDocsNode> getTermInDoc(String aTweet) {
		String docID = io.getDocsID(aTweet);
		Vector<String> terms = io.StringToWord(io.getTweet(aTweet));
		Vector<TermDocsNode> termsInDoc = new Vector<TermDocsNode>();
		for(int i = 0; i < terms.size(); i ++) {
			if(terms.elementAt(i).matches(regex_declaration)) {
				TermDocsNode node = new TermDocsNode(docID, terms.elementAt(i));
				termsInDoc.add(node);
			}
		}
		return termsInDoc;		
	}
	
	/*
	 * function: mergeTermsToDict. 
	 * The function is used to merge all terms in each document into a whole dictionary of term. 
	 * Every node (term) is defined as TermDocsNode.
	 * param: Vector<String> allDocs, String filename.
	 * return: Vector<TermDocsNode> dict.
	 */
	public Vector<TermDocsNode> mergeTermsToDict(Vector<String> allDocs, String filename) throws IOException {
		Vector<String> docs = io.readFile(filename);
		Vector<TermDocsNode> dict = new Vector<TermDocsNode>();
		for(int i = 0; i < docs.size(); i++) {
			Vector<TermDocsNode> terms = this.getTermInDoc(docs.elementAt(i));
			dict.addAll(terms);
		}
		return dict;
	}
	
	/*
	 * function: sortTermsDict. 
	 * The function is used to sort the dictionary  alphabetically
	 * param: List<TermDocsNode> list.
	 * return: none (this method will affect the dictionary immediately after sorting).
	 */
	public void sortTermsDict(List<TermDocsNode> list) {
		Collections.sort(list, new CustomComparator());
	}
	
	/*
	 * function: printList. 
	 * The function is used to print the list TermDocsNode.
	 * param: List<TermDocsNode> list
	 * return: none.
	 */
	public void printList(List<TermDocsNode> list) {
		for(int i = 0; i< list.size(); i++) {
			System.out.println(list.get(i).term);
		}
	}
	
	/*
	 * function: generateDictPostingList. 
	 * The function is used to generatePostingList from the dictionary.
	 * param: List<TermDocsNode> termDict
	 * return: Vector<TermDocsFreq>
	 */
	public Vector<TermDocsFreq> generatePostingListDict(List<TermDocsNode> termDict) {
		Vector<TermDocsFreq> termDocsFreqList = new Vector<TermDocsFreq>();
		this.sortTermsDict(termDict);  //Sort the termDict.
		long termDictLength = termDict.size();
		int freq = 1;
		int current_index = 0; int next_index = 1;
		while(current_index < termDictLength-1) {
			List<String> postingList = new Vector<String>();
			postingList.add(termDict.get(current_index).docsID);
			while(next_index < termDictLength) {
				if(termDict.get(current_index).term.equalsIgnoreCase(termDict.get(next_index).term)) {
					freq++;
					postingList.add(termDict.get(next_index).docsID);
					next_index++;
					current_index++;
				}
				else {
					next_index++;
					current_index++;
					break;
				}
			}
			termDocsFreqList.add(new TermDocsFreq(termDict.get(current_index-1).term, freq, postingList));
			freq = 1;
		}
		return termDocsFreqList;
	}
	
	/*
	 * function: printPostingListSingleTerm. 
	 * The function is used to print the posting list if string input doesn't include suffix key *.
	 * param: String st, Vector<TermDocsFreq> termDocsFreqList
	 * return: none
	 */
	public void printPostingListSingleTerm(String st, Vector<TermDocsFreq> termDocsFreqList) {
		for(int i = 0; i < termDocsFreqList.size(); i++) {
			if(termDocsFreqList.get(i).term.equalsIgnoreCase(st)) {
				System.out.print("Posting List of string input: ");
				System.out.println(termDocsFreqList.get(i).postingList);
			}
		}
	}
	
	/*
	 * function: printPostingListPrefix. 
	 * The function is used to print the posting list if string input includes suffix key *.
	 * param: String st, Vector<TermDocsFreq> termDocsFreqList
	 * return: none
	 */
	public void printPostingListPrefix(String prefix, Vector<TermDocsFreq> termDocsFreqList) {
		String prefixString = io.getPrefix(prefix);
		if(prefixString != null) {
			for(int i = 0; i < termDocsFreqList.size(); i++) {
				if(termDocsFreqList.get(i).term.startsWith(prefixString)) {
					System.out.print("Posting List of "+ termDocsFreqList.get(i).term +" : ");
					System.out.println(termDocsFreqList.get(i).postingList);
				}
			}
		}
	}
	
	/*
	 * function: getPostingListSingleTerm. 
	 * The function get the posting list of a term from the term docs frequency list.
	 * param: String st, Vector<TermDocsFreq> termDocsFreqList
	 * return: Set<String>.
	 */
	public Set<String> getPostingListSingleTerm(String st, Vector<TermDocsFreq> termDocsFreqList) {
		Set<String> setOfPostingList = new HashSet<String>();
		for(int i = 0; i < termDocsFreqList.size(); i++) {
			if(termDocsFreqList.get(i).term.equalsIgnoreCase(st)) 
				setOfPostingList.addAll(termDocsFreqList.get(i).postingList);
		}
		return setOfPostingList;
	}
	
	/*
	 * function: getPostingListOfTerms. 
	 * The function is used to print the posting list if string input includes suffix key *.
	 * param: String st, Vector<TermDocsFreq> termDocsFreqList
	 * return: none
	 */
	public Vector<Set<String>> getPostingListOfTerms(String terms, Vector<TermDocsFreq> termDocsFreqList) {
		Vector<String> termsList = io.splitQuery(terms);
		Vector<Set<String>> setOfPostingList = new Vector<Set<String>>();
		for(int i = 0; i < termsList.size(); i++) {
			Set<String> postingList = this.getPostingListSingleTerm(termsList.get(i), termDocsFreqList);
			this.addPostingList(setOfPostingList, postingList);
		}
		return setOfPostingList;
	}
	

	/*
	 * function: addPostingList. 
	 * The function is used to add the posting list based on its size to the set of posting list.
	 * param: Vector<Set<String>> setOfPostingList, Set<String> postingList
	 * return: none
	 */
	public void addPostingList(Vector<Set<String>> setOfPostingList, Set<String> postingList) {
		int setOfPostingListSize = setOfPostingList.size();
		if(setOfPostingListSize == 0) {
			setOfPostingList.add(postingList);
		}
		else {
			for(int i = 0; i < setOfPostingListSize; i++) {
				if(postingList.size() <= setOfPostingList.get(i).size()) {
					setOfPostingList.insertElementAt(postingList, i);
				}
			}
		}
	}
	

	/*
	 * function: intersectPostingList. 
	 * The function is used to get the intersection posting list of two terms.
	 * param: String st, Vector<TermDocsFreq> termDocsFreqList
	 * return: Set<String>
	 */
	public Set<String> intersectPostingList(String term1, String term2, Vector<TermDocsFreq> termDocsFreqList) {
		Set<String> postingListTerm1 = this.getPostingListSingleTerm(term1, termDocsFreqList);
		Set<String> postingListTerm2 = this.getPostingListSingleTerm(term2, termDocsFreqList);
		if(postingListTerm1.size() > postingListTerm2.size()) {
			postingListTerm1.retainAll(postingListTerm2);
			return postingListTerm1;
		}
		else {
			postingListTerm2.retainAll(postingListTerm1);
			return postingListTerm2;
		}
	}
	

	/*
	 * function: insertectPostingListTerms. 
	 * The function is used to get the intersection posting list of list of terms.
	 * param: String booleanQuery,  Vector<TermDocsFreq> termDocsFreqList
	 * return: Set<String>
	 */
	public Set<String> intersectPostingListTerms(String booleanQuery,  Vector<TermDocsFreq> termDocsFreqList) {
		Vector<Set<String>> getPostingListOfTerms = this.getPostingListOfTerms(booleanQuery, termDocsFreqList);
		Set<String> result = getPostingListOfTerms.firstElement();
		for(int i = 1; i < getPostingListOfTerms.size(); i++) {
			result.retainAll(getPostingListOfTerms.elementAt(i));
		}
		return result;
	}
	

	/*
	 * function: printIntersectionPostingList. 
	 * The function is used to print the intersection of the posting lists of two terms.
	 * param: String term1, String term2, Vector<TermDocsFreq> termDocsFreqList
	 * return: none
	 */
	public void printIntersectionPostingList(String term1, String term2, Vector<TermDocsFreq> termDocsFreqList) {
		Set<String> intersectionPostingList = this.intersectPostingList(term1, term2, termDocsFreqList);
		System.out.println(intersectionPostingList);
	}
	
	public void printIntersectionPostingListTerms(String booleanQuery,  Vector<TermDocsFreq> termDocsFreqList) {
		
		System.out.print("Posting list of "+booleanQuery + " is: ");
		System.out.println(this.intersectPostingListTerms(booleanQuery, termDocsFreqList));
	}
	/*
	 * function: main - finally. 
	 * param: String args[]
	 * return: none
	 */
	public static void main(String args[]) throws IOException {
		InvertedIndex invertedIndex = new InvertedIndex();
		IO io = new IO();
		final String filename = System.getProperty("user.dir") + ("/smalltweets.txt");
		Vector<String> storedData = io.readFile(filename);
		List<TermDocsNode> termsDict = invertedIndex.mergeTermsToDict(storedData, filename);
		Vector<TermDocsFreq> PostingListDict = invertedIndex.generatePostingListDict(termsDict);
		
		// Print posting list to file
		//String outputFileName = "Part3";
		//final String outputFile = System.getProperty("user.dir") + ("/output_") + (outputFileName) + (".txt");

		//Part2.
		System.out.println("---------  Part 2 ---------");
		invertedIndex.printPostingListPrefix("a*", PostingListDict);  // Change a* to any string you want. a* means any string begins with a
		
		//Part3.
		System.out.println("---------  Part 3 ---------");
		System.out.println("egg AND cheese");
		invertedIndex.printIntersectionPostingListTerms("egg AND cheese", PostingListDict);
		System.out.println("chocolate AND strawberry");
		invertedIndex.printIntersectionPostingListTerms("chocolate AND strawberry", PostingListDict);
		System.out.println("eggs AND cheese AND bacon");
		invertedIndex.printIntersectionPostingListTerms("eggs AND cheese AND bacon", PostingListDict);

	}
}
