package ee.hm.dop.service.synchronizer.oaipmh;

public class ParseException extends Exception {

    private static final long serialVersionUID = -871467702144641003L;

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable e) {
        super(e);
    }
}
