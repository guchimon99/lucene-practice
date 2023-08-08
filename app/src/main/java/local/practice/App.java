package local.practice;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryparser.classic.ParseException;

public class App {
    String indexDir = "./index/";
    String dataDir = "./data/";
    String exportFile = "./terms.txt";
    Indexer indexer;
    Searcher searcher;
    TermExportor termExportor;
    TermViewer termViewer;

    public static void main(String[] args)  {
        if (args.length < 2) {
            System.err.println("Usage: java App <search_query> <analyzer_type>");
            return;
        }

        String searchQuery = args[0];
        String analyzerTypeString = args[1];

        App app;
        try {
            app = new App();
            Indexer.AnalyzerType analyzerType = Indexer.AnalyzerType.valueOf(analyzerTypeString.toUpperCase());
            app.createIndex(analyzerType);
            app.viewTerms();
            app.exportTerms();
            app.search(searchQuery);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void createIndex(Indexer.AnalyzerType analyzerType) throws IOException {
        indexer = new Indexer(indexDir, analyzerType);

        int numIndexed = indexer.createIndex(dataDir);
        indexer.close();

        System.out.println(numIndexed + " files indexed");
    }

    private void viewTerms () throws IOException {
        termViewer = new TermViewer(indexDir);
        termViewer.view();
    }

    private void exportTerms() throws IOException {
        termExportor = new TermExportor(indexDir);
        termExportor.export(exportFile);
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
