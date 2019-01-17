package ee.hm.dop.dao;

import ee.hm.dop.model.PeerReview;

import java.util.List;

public class PeerReviewDao extends AbstractDao<PeerReview> {

    public PeerReview getPeerReviewByURL(String url) {
        return findByField("url", url);
    }

    public List<PeerReview> getPeerReviewByURL(List<String> url) {
        return findByFieldListInList("url", url);
    }
}
