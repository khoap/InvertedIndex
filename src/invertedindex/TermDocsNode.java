package invertedindex;

/*
 * TermDocsNode Class.
 * - To represent TermDocsNode object.
 * - Properties: docsID, term.
 * - Initialize function: TermDocsNode with/without argument.
 */

public class TermDocsNode {
	String docsID;
	String term;

	public TermDocsNode() {}
	public TermDocsNode(String docsID, String term) {
		this.docsID = docsID;
		this.term = term;
	}
}

