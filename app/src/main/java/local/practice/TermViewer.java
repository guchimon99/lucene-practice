package local.practice;

import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class TermViewer {
  private final Directory indexDirectory;

  public TermViewer(String indexDirectoryPath) throws IOException {
    indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
  }

  public void view() throws IOException {
    Set<String> uniqueTerms = new HashSet<>();

    try (IndexReader reader = DirectoryReader.open(indexDirectory)) {
      for (LeafReaderContext context : reader.leaves()) {
        LeafReader leafReader = context.reader();
        FieldInfos fieldInfos = leafReader.getFieldInfos();
        for (FieldInfo fieldInfo : fieldInfos) {
          collectUniqueTermsForField(leafReader, fieldInfo.name, uniqueTerms);
        }
      }
    }

    System.out.println("Total Unique Term Count: " + uniqueTerms.size());
  }

  private void collectUniqueTermsForField(LeafReader leafReader, String field, Set<String> uniqueTerms) throws IOException {
    Terms terms = leafReader.terms(field);
    if (terms == null) return;

    TermsEnum termsEnum = terms.iterator();
    BytesRef term;
    while ((term = termsEnum.next()) != null) {
      String termText = term.utf8ToString();
      uniqueTerms.add(termText);
    }
  }
}
