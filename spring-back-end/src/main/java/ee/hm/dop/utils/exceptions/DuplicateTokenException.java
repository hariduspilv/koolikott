package ee.hm.dop.utils.exceptions;

public class DuplicateTokenException extends RuntimeException {

    private static final long serialVersionUID = -2888848626936045166L;

    public DuplicateTokenException() {
        super();
    }

    public DuplicateTokenException(String message) {
        super(message);
    }

    public DuplicateTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
