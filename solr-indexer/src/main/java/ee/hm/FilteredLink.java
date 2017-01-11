package ee.hm;

/**
 * Created by joonas on 27.12.16.
 */

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.TokenFilterFactory;

final class FilteredLink extends TokenFilter {
    private CharTermAttribute charTermAttr;
    private int step;
    private String originalString;

    protected FilteredLink(TokenStream ts) {
        super(ts);
        this.charTermAttr = addAttribute(CharTermAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken() && step == 0) {
            return false;
        }
        if(step > 2){step = 0;}
        if(step == 0)originalString = charTermAttr.toString();

        /* youtube.com */
        Pattern pattern = Pattern.compile("(?:http://|https://)(?:www.)?([^:/\\n]+)");
        /* youtube.com/watch?.... */
        Pattern pattern2 = Pattern.compile("(?:http://|https://)(?:www.)?([^\\n]+)");
        /* youtube */
        Pattern pattern3 = Pattern.compile("(?:http://|https://)(?:www.)?([^.\\n]+)");
        Matcher m = pattern.matcher(originalString);
        Matcher m2 = pattern2.matcher(originalString);
        Matcher m3 = pattern3.matcher(originalString);
        charTermAttr.setEmpty();
        if(step == 2 && m2.find()){
            charTermAttr.append(m2.group(1));
            this.step++;
        }
        if(step == 1 && m.find()){
            charTermAttr.append(m.group(1));
            this.step++;
        }
        if(step == 0 && m3.find()){
            charTermAttr.append(m3.group(1));
            this.step++;
        }
        return step != 0;
    }
}

