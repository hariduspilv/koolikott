package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.PeerReviewDAO;
import ee.hm.dop.model.PeerReview;

/**
 * Created by joonas on 28.09.16.
 */
public class PeerReviewService {

    @Inject
    private PeerReviewDAO peerReviewDAO;

    public PeerReview getPeerReviewByURL(String url){
        return peerReviewDAO.getPeerReviewByURL(url);
    }

    public PeerReview createPeerReview(String url) {
        PeerReview peerReview = new PeerReview();
        peerReview.setUrl(url);
        return peerReviewDAO.create(peerReview);
    }

}
