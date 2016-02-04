package ee.hm.dop.model;

/**
 * Created by mart on 2.02.16.
 */
public class TagUpVoteForm {

    public TagUpVoteForm() {
    }

    public TagUpVoteForm(Tag tag, int upVoteCount, boolean hasUpVoted) {
        this.tag = tag;
        this.upVoteCount = upVoteCount;
        this.hasUpVoted = hasUpVoted;
    }

    private Tag tag;

    private int upVoteCount;

    private boolean hasUpVoted;

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public int getUpVoteCount() {
        return upVoteCount;
    }

    public void setUpVoteCount(int upVoteCount) {
        this.upVoteCount = upVoteCount;
    }

    public boolean isHasUpVoted() {
        return hasUpVoted;
    }

    public void setHasUpVoted(boolean hasUpVoted) {
        this.hasUpVoted = hasUpVoted;
    }
}
