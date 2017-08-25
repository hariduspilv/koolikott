package ee.hm.dop.dao;

import ee.hm.dop.model.PeerReview;

public class PeerReviewDao extends AbstractDao<PeerReview> {

    public PeerReview getPeerReviewByURL(String url) {
        return findByField("url", url);
    }
}
