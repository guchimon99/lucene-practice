package local.practice;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryparser.classic.ParseException;

public class App {
    String indexDir = "./index/";
    String dataDir = "./data/";
    Indexer indexer;
    Searcher searcher;

    public static void main(String[] args)  {
        App app;
        try {
            app = new App();
            app.createIndex();
            app.search(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void createIndex () throws IOException {
        indexer  =  new Indexer(indexDir);

        int numIndexed = indexer.createIndex(dataDir);
        indexer.close();

        System.out.println(numIndexed + " files indexed");
    }

    private void search (String searchQuery) throws IOException, ParseException {
        searcher = new Searcher(indexDir);
        TopDocs hits = searcher.search(searchQuery);
        System.out.println(hits.totalHits + " documents found");

        for (ScoreDoc scoreDoc: hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            System.out.println("File: " + doc.get("filepath"));
        }
    }
}
