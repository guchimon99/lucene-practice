package local.practice;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;

public class JapaneseNGramAnalyzer extends JapaneseAnalyzer {
    private int minGram;
    private int maxGram;

    public JapaneseNGramAnalyzer(int minGram, int maxGram) {
        super();
        this.minGram = minGram;
        this.maxGram = maxGram;
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        TokenStream input = super.normalize(fieldName, in);
        return new NGramTokenFilter(input, minGram, maxGram, true);
    }
}
