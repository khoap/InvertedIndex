package invertedindex;

import java.util.ArrayList;
import java.util.List;

/*
 *  TermDocsFreq Class.
 *  Author: Khoa Pham - khoa.pham@me.com - all rights reserved.
 * - To represent TermDocsFreq object.
 * - Properties: term, docFreq, postingList.
 * - Initialize function: TermDocsNode with/without argument.
 */
public class TermDocsFreq {
	String term;
	int docFreq;
	List<String> postingList = new ArrayList<String>();
	
	public TermDocsFreq() {}
	public TermDocsFreq(String term, int docFreq, List<String> postingList) {
		this.term = term;
		this.docFreq = docFreq;
		this.postingList = postingList;
	}
}
