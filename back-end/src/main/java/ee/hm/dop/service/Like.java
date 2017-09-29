package ee.hm.dop.service;

public enum Like {
    LIKE, DISLIKE;

    public boolean isLiked(){
        return this == LIKE;
    }
}
