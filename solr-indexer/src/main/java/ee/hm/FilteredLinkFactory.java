package ee.hm;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class FilteredLinkFactory extends TokenFilterFactory {

    public FilteredLinkFactory(Map<String, String> args) {
        super(args);
    }

    @Override
    public TokenStream create(TokenStream ts) {
        return new FilteredLink(ts);
    }

}