package ee.hm.dop.exceptions;

public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException() {
        super();
    }

    public DuplicateUserException(String message) {
        super(message);
    }

    public DuplicateUserException(Throwable e) {
        super(e);
    }
}
