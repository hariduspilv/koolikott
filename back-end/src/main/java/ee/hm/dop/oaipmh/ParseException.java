package ee.hm.dop.oaipmh;

public class ParseException extends Exception {

    private static final long serialVersionUID = -871467702144641003L;

    public ParseException(String message) {
        super(message);
    }

    public ParseException(Throwable e) {
        super(e);
    }
}
