package local.practice;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import local.practice.Indexer.AnalyzerType;

import org.apache.lucene.queryparser.classic.ParseException;

public class App {
    String indexDir = "./index/";
    String dataDir = "./data/";
    String termsDir = "./terms/";

    Indexer indexer;
    Searcher searcher;
    TermExportor termExportor;
    TermViewer termViewer;
    AnalyzerType analyzerType;

    public static void main(String[] args)  {
        if (args.length < 2) {
            System.err.println("Usage: java App <search_query> <analyzer_type>");
            return;
        }

        String searchQuery = args[0];
        String analyzerTypeString = args[1];

        App app;
        try {
            AnalyzerType analyzerType = AnalyzerType.valueOf(analyzerTypeString.toUpperCase());
            app = new App(analyzerType);
            app.createIndex();
            app.viewTerms();
            app.exportTerms();
            app.search(searchQuery);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public App (AnalyzerType analyzerType) {
        this.analyzerType = analyzerType;
    }

    private void createIndex() throws IOException {
        indexer = new Indexer(indexDir, analyzerType);
        System.out.println("Using Analyzer: " + analyzerType);

        int numIndexed = indexer.createIndex(dataDir);
        indexer.close();

        System.out.println(numIndexed + " files indexed");
    }

    private void viewTerms () throws IOException {
        termViewer = new TermViewer(indexDir);
        termViewer.view();
    }

    private void exportTerms() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedNow = now.format(formatter);

        termExportor = new TermExportor(indexDir);
        termExportor.export(termsDir + formattedNow + "_" + analyzerType + ".txt");
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
