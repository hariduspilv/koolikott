package ee.hm.dop.service.useractions;

<<<<<<< HEAD:back-end/src/main/java/ee/hm/dop/service/useractions/PeerReviewService.java
import javax.inject.Inject;

=======
>>>>>>> new-develop:back-end/src/main/java/ee/hm/dop/service/useractions/PeerReviewService.java
import ee.hm.dop.dao.PeerReviewDao;
import ee.hm.dop.model.PeerReview;

import javax.inject.Inject;

/**
 * Created by joonas on 28.09.16.
 */
public class PeerReviewService {

    @Inject
    private PeerReviewDao peerReviewDao;

    public PeerReview getPeerReviewByURL(String url){
        return peerReviewDao.getPeerReviewByURL(url);
    }

    public PeerReview createPeerReview(String url) {
        PeerReview peerReview = new PeerReview();
        peerReview.setUrl(url);
        return peerReviewDao.createOrUpdate(peerReview);
    }
}
