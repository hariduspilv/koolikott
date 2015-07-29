package ee.hm.dop.oaipmh;

public class ParseException extends Exception {

    public ParseException(String message) {
        super(message);
    }

    public ParseException(Throwable e) {
        super(e);
    }
}
