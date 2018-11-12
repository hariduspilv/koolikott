package ee.hm.dop.model.user;

public class UserSession {

    private int minRemaning;

    public UserSession() {
    }

    public UserSession(int minRemaning) {
        this.minRemaning = minRemaning;
    }

    public int getMinRemaning() {
        return minRemaning;
    }

    public void setMinRemaning(int minRemaning) {
        this.minRemaning = minRemaning;
    }
}
