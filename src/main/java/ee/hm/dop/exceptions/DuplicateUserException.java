package ee.hm.dop.exceptions;

public class DuplicateUserException extends RuntimeException {

    private static final long serialVersionUID = 1308287654098945692L;

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
