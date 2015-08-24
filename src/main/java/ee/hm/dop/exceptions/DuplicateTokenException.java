package ee.hm.dop.exceptions;

/**
 * Created by mart.laus on 13.08.2015.
 */
public class DuplicateTokenException extends RuntimeException {

    private static final long serialVersionUID = -2888848626936045166L;

    public DuplicateTokenException() {
        super();
    }

    public DuplicateTokenException(String message) {
        super(message);
    }

    public DuplicateTokenException(Throwable e) {
        super(e);
    }
}
