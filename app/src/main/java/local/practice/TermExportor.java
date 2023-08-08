package local.practice;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TermExportor {
  private final Directory indexDirectory;

  public TermExportor(String indexDirectoryPath) throws IOException {
    indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
  }

  public void export(String outputPath) throws IOException {
    StringBuilder result = new StringBuilder();

    try (IndexReader reader = DirectoryReader.open(indexDirectory)) {
      for (LeafReaderContext context : reader.leaves()) {
        LeafReader leafReader = context.reader();
        FieldInfos fieldInfos = leafReader.getFieldInfos();
        for (FieldInfo fieldInfo : fieldInfos) {
          List<String> termsForField = getTermsForField(leafReader, fieldInfo.name);
          result.append(fieldInfo.name).append(": \n");
          for (String term : termsForField) {
            result.append(term).append("\n");
          }
        }
      }
    }

    try (FileWriter file = new FileWriter(outputPath)) {
      file.write(result.toString());
    }
  }

  private List<String> getTermsForField(LeafReader leafReader, String field) throws IOException {
    List<String> termsArray = new ArrayList<>();
    Terms terms = leafReader.terms(field);
    if (terms == null) return termsArray;

    TermsEnum termsEnum = terms.iterator();
    BytesRef term;
    while ((term = termsEnum.next()) != null) {
      termsArray.add(getTerm(leafReader, field, term));
    }
    return termsArray;
  }

  private String getTerm(LeafReader leafReader, String field, BytesRef term) throws IOException {
    String termText = term.utf8ToString();

    PostingsEnum postings = leafReader.postings(new Term(field, term));
    List<String> filenames = new ArrayList<>();
    int docID;
    while ((docID = postings.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
      Document doc = leafReader.document(docID);
      filenames.add(doc.get("filename"));
    }

    return termText + ": " + String.join(", ", filenames);
  }
}
