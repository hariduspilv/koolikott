package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.PeerReview;

/**
 * Created by joonas on 28.09.16.
 */
public class PeerReviewDAO extends BaseDAO<PeerReview>{

    @Inject
    private EntityManager entityManager;

    public PeerReview getPeerReviewByURL(String url){
        TypedQuery<PeerReview> findByURL = createQuery("SELECT pr FROM PeerReview pr WHERE pr.url = :url",
                PeerReview.class).
                setParameter("url", url);
        return getSingleResult(findByURL);
    }

    public PeerReview create(PeerReview peerReview) {
        PeerReview merged = entityManager.merge(peerReview);
        entityManager.persist(merged);
        return merged;
    }

}
