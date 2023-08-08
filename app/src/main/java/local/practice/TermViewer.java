package local.practice;

import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TermViewer {
  private final Directory indexDirectory;

  public TermViewer(String indexDirectoryPath) throws IOException {
    indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
  }

  public void view() throws IOException {
    try (IndexReader reader = DirectoryReader.open(indexDirectory)) {
      for (LeafReaderContext context : reader.leaves()) {
        LeafReader leafReader = context.reader();
        FieldInfos fieldInfos = leafReader.getFieldInfos();
        for (FieldInfo fieldInfo : fieldInfos) {
          printTermsForField(leafReader, fieldInfo.name);
        }
      }
    }
  }

  private void printTermsForField(LeafReader leafReader, String field) throws IOException {
    Terms terms = leafReader.terms(field);
    if (terms == null) return;

    TermsEnum termsEnum = terms.iterator();
    BytesRef term;
    while ((term = termsEnum.next()) != null) {
      printTerm(leafReader, field, term);
    }
  }

  private void printTerm(LeafReader leafReader, String field, BytesRef term) throws IOException {
    String termText = term.utf8ToString();

    // Get the postings for the term to find out which documents this term appears in
    PostingsEnum postings = leafReader.postings(new Term(field, term));
    List<String> filenames = new ArrayList<>();
    int docID;
    while ((docID = postings.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
        Document doc = leafReader.document(docID);
        filenames.add(doc.get("filename"));
    }

    System.out.println("Field: " + field + " - Term: " + termText + " - Filenames: " + String.join(", ", filenames));
  }
}
