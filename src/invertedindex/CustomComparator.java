package invertedindex;

import java.util.Comparator;

/*
 * CustomComparator Class implement Comparator<TermDocsNode>. 
 * Author: Khoa Pham - khoa.pham@me.com - all rights reserved.
 * - To compare to TermDocsNode for sorting.
 */
public class CustomComparator implements Comparator<TermDocsNode>{
	public int compare(TermDocsNode node1,TermDocsNode node2) {
		return node1.term.compareTo(node2.term);
	}
}