package ee.hm.dop.model.user;

public class UserSession {

    private int minRemaining;
    private boolean continueSession;

    public UserSession() {
    }

    public UserSession(int minRemaning, boolean continueSession) {
        this.minRemaining = minRemaning;
        this.continueSession = continueSession;
    }

    public int getMinRemaining() {
        return minRemaining;
    }

    public void setMinRemaining(int minRemaining) {
        this.minRemaining = minRemaining;
    }

    public boolean isContinueSession() {
        return continueSession;
    }

    public void setContinueSession(boolean continueSession) {
        this.continueSession = continueSession;
    }
}
