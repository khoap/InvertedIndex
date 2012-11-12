package indexHW1;

import org.apache.lucene.*;
import org.apache.lucene.analysis.LimitTokenCountAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.io.IOException;

public class IndexHW1 {

	public static void main(String[] args) throws IOException, ParseException {

		// ///////////////////////
		// // SETTING THINGS UP //
		// ///////////////////////

		IndexHW1 itweets = new IndexHW1();

		// First argument is the file containing tweets.
		//Vector<String> tweets = itweets.readFile(args[0]);
		
		BufferedReader bufftweets = new BufferedReader(new FileReader(args[0]));
		// Second argument is path to the directory where you
		// want your index to reside.
		File indexDir = new File(args[1]);

		// This seems to make it work properly on our cluster. Feel free to
		// leave this out and see what happens.
		Directory index = FSDirectory.open(indexDir, NoLockFactory.getNoLockFactory());

		// StandardAnalyzer converts to lowercase, strips out stopwords
		// and tokenizes according to the Unicode text segmentation
		// specification.
		StandardAnalyzer stdAn = new StandardAnalyzer(Version.LUCENE_36);

		// You can print out the stop words in case you're curious.
		//Set stops = stdAn.getStopwordSet();
		//System.out.println(stops.toString());
		//System.out.println(stops.getClass());

		// //////////////////////
		// BUILDING AN INDEX //
		// //////////////////////
		IndexWriterConfig iwConf = new IndexWriterConfig(Version.LUCENE_36,stdAn);
		iwConf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter indexWriter = new IndexWriter(index, iwConf);

		// For each document in your collection, create a new Document and index it.
		// Parse the line into the two fields:
		// First field is the document ID.
		// Second field is the text of the document.
		String line;
        while ( ((line = bufftweets.readLine()) != null) ) {
        	Document d = new Document();
        	String docID = itweets.getDocsID(line);
        	String tweetText = itweets.getTweet(line);
        	d.add(new Field("docID",docID, Field.Store.YES, Field.Index.NOT_ANALYZED));
			d.add(new Field("tweetText",tweetText, Field.Store.YES, Field.Index.ANALYZED));
			
			indexWriter.addDocument(d);
        }
        /*
		for(int i = 0; i < tweets.size(); i++) {
			Document d = new Document();
			String docID = itweets.getDocsID(tweets.elementAt(i));
			String tweetText = itweets.getTweet(tweets.elementAt(i));
			
			d.add(new Field("docID",docID, Field.Store.YES, Field.Index.NOT_ANALYZED));
			d.add(new Field("tweetText",tweetText, Field.Store.YES, Field.Index.ANALYZED));
			
			indexWriter.addDocument(d);
		}
		*/
		indexWriter.forceMerge(1);
		indexWriter.commit();
		indexWriter.close();
		
		// Using IndexReader to open index for the rest of program.
		IndexReader ir = IndexReader.open(index);
		
		// For each document, print out the input text and the tokens.
		// Once you get a TokenStream from StandardAnalyzer, you can
		// send it to the helper method included in this class,
		// printTokenStream(), which you'll find below.
		int numDocs = ir.numDocs();
		for(int i = 0; i < numDocs; i++) {
			Document doc = ir.document(i);
			List<Fieldable> fields = doc.getFields();
			String text = fields.get(1).stringValue();
			Reader textReader = new StringReader(text);
			TokenStream tokenStream = stdAn.tokenStream("tweetText", textReader);
			System.out.println("Input text: " + text);
			System.out.print("All tokens: "); 
			itweets.printTokenStream(tokenStream);
			System.out.println("--------------------------------------");
		}

		// ////////////////////////
		// INSPECTING THE INDEX //
		// ////////////////////////
		
		// Print out all of the terms and their document frequencies (DF).
		// Using TermEnum Class to parse all the terms indexed.
		// Loop through the TermEnum and output the term and its docFreq using docFreq method.
		TermEnum termEnum = ir.terms();
		System.out.println("\n \n");
		System.out.println("-----------------------------------------------------------");
		System.out.println("------- All Tokens and their documents frequencies -------");
		System.out.println("-----------------------------------------------------------");
		while(termEnum.next()) {
			Term tweetTerm = termEnum.term();
			if(tweetTerm.field().equals("tweetText")) {
				int docTerm = ir.docFreq(tweetTerm);
				System.out.println("Terms: " + tweetTerm.text() + " ----- docFreq: " + docTerm);
			}	
		}
		
		// For each document, print out the terms and their frequencies (TF).
		// Read each document by using Class Document. 
		// Getting the tweetText only by using List<Fieldable> at position 1 in the list.
		// Terms of each document are collected after tokenized. 
		// For each term, output the corresponding termFreq.
		System.out.println("\n \n");
		System.out.println("-----------------------------------------------------------");
		System.out.println("---Terms of each document and their documents frequencies---");
		System.out.println("-----------------------------------------------------------");
	    for(int i = 0; i < ir.numDocs(); i++) {
			Document doc = ir.document(i);
			List<Fieldable> fields = doc.getFields();
			String aTweet = fields.get(1).stringValue();
			TokenStream tokenStream = stdAn.tokenStream("tweet", new StringReader(aTweet));
			CharTermAttribute terms = tokenStream.addAttribute(CharTermAttribute.class);
			System.out.println("Terms in docID: " + fields.get(0).stringValue());
			while (tokenStream.incrementToken()) {
				String term = terms.toString();
				int docTerm = ir.docFreq(new Term(fields.get(1).name(), term));
				System.out.println(term + " ----- docFreq: " + docTerm);
			}
			System.out.println();
		}
		
		// ///////////////
		// QUERY STUFF //
		// ///////////////

		// If there are three arguments, the last is the query term.
		// Default to query term "cheese", just so you don't have
		// to enter a query when trying stuff out.
		String querystr = "cheese";
		if (args.length > 2) {
			querystr = args[2];
		}

		// A few things to note here:
		// 1. You should parse your query with the same Analyzer used to
		// build the index (here, the StandardAnalyzer stdAn).
		// 2. The "text" arg specifies here the default field to use
		// when no field is explicitly specified in the query.
		String dField = "tweetText";
		Query q = new QueryParser(Version.LUCENE_36, dField, stdAn)
				.parse(querystr);

		// /////////////////////////
		// SEARCHING and SCORING //
		// /////////////////////////
		
		int hitsPerPage = 10; //Define hitsPage

		// Using IndexSearch for searching in documents of index.
	    IndexSearcher searcher = new IndexSearcher(ir);
	    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
	    searcher.search(q, collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;

	    // Display all of the matching documents and their scores.
	    System.out.println("\n");
		System.out.println("-----------------------------------------------------------");
		System.out.println("------------------SEARCHING and SCORINGs-------------------");
		System.out.println("-----------------------------------------------------------");
	    System.out.println("Found " + hits.length + " hits of terms: " + querystr);
	    for(int i=0; i<hits.length; ++i) {
	    	int docId = hits[i].doc;
	        float score = hits[i].score;
	        Document d = searcher.doc(docId);
	        System.out.println((i + 1) + ". docID: " + d.get("docID") + "Score: " + score);
	    }
		searcher.close();
		ir.close();
	}

	// //////////////////
	// HELPER METHODS //
	// //////////////////

	// This prints out the tokenization of the string.
	// Kind of convoluted but apparently how it is supposed to work.
	void printTokenStream(TokenStream ts) throws IOException {
		CharTermAttribute terms = ts.addAttribute(CharTermAttribute.class);
		OffsetAttribute offsetAttribute = ts.addAttribute(OffsetAttribute.class);
		while (ts.incrementToken()) {
			String term = terms.toString();
			System.out.print(term + " ");
		}
		System.out.println();
	}
	/*
	 * function: readFile.
	 * param: filename
	 * return: Vector<String>.
	 */
	public Vector<String> readFile(String filename) throws IOException {
		File inputFile = new File(filename);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				inputFile));
		Vector<String> tweets = new Vector<String>();
		String inputLine;
		while ((inputLine = bufferedReader.readLine()) != null) {
			tweets.add(inputLine);
		}
		return tweets;
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
}