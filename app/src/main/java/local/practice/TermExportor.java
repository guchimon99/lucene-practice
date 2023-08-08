package local.practice;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

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
          Set<String> termsForField = getTermsForField(leafReader, fieldInfo.name);
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

  private Set<String> getTermsForField(LeafReader leafReader, String field) throws IOException {
    Set<String> termsSet = new HashSet<>();
    Terms terms = leafReader.terms(field);
    if (terms == null) return termsSet;

    TermsEnum termsEnum = terms.iterator();
    BytesRef term;
    while ((term = termsEnum.next()) != null) {
      termsSet.add(term.utf8ToString());
    }
    return termsSet;
  }
}
