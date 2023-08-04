package local.practice;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
    private IndexWriter writer;

    public Indexer (String indexDirectoryPath) throws IOException  {
        Directory directory = FSDirectory.open(Paths.get(indexDirectoryPath));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        LogMergePolicy policy = new LogDocMergePolicy();
        policy.setMergeFactor(10);
        config.setMergePolicy(policy);

        config.setMaxBufferedDocs(100);

        clearIndexDirectory(indexDirectoryPath);

        writer = new IndexWriter(directory, config);
    }

    private void clearIndexDirectory (String indexDirectoryPath) {
        Path path = Paths.get(indexDirectoryPath);
        deleteDirectoryRecursively(path);
    }

    private void deleteDirectoryRecursively(Path path) {
        try (Stream<Path> stream = Files.walk(path)) {
            stream
                .sorted(java.util.Comparator.reverseOrder())
                .forEach(p -> deletePath(p));
        } catch (IOException e) {
            System.err.println("Failed to read directory: " + path);
        }
    }

    private void deletePath(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            System.err.println("Failed to delete " + path);
        }
    }

    public void close() throws CorruptIndexException, IOException {
        writer.close();
    }

    private Document getDocument (File file) throws IOException {
        Document document = new Document();
        TextField contentsField = new TextField("contents", new FileReader(file));
        TextField fileNameField = new TextField("filename", file.getName(), TextField.Store.YES);
        TextField filePathField = new TextField("filepath", file.getPath(), TextField.Store.YES);
        document.add(contentsField);
        document.add(fileNameField);
        document.add(filePathField);
        return document;
    }

    private void indexFile(File file) throws IOException {
        Document document = getDocument(file);
        writer.addDocument(document);
    }

    public int createIndex(String dataDirPath) throws IOException {
        File[] files = new File(dataDirPath).listFiles();
        for (File file : files) {
            if (!file.isDirectory() && file.exists() && file.canRead())  {
                indexFile(file);
            }
        }
        return writer.numRamDocs();
    }
}
