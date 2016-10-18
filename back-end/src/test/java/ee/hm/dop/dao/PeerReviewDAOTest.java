package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import javax.persistence.TypedQuery;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.PeerReview;
import org.junit.Test;

/**
 * Created by joonas on 5.10.16.
 */
public class PeerReviewDAOTest extends DatabaseTestBase {

    @Inject
    private PeerReviewDAO peerReviewDAO;

    @Test
    public void getPeerReviewByURL() {
        Long id = (long) 1;
        String url = "http://postimees.ee";

        PeerReview peerReview = peerReviewDAO.getPeerReviewByURL(url);
        assertNotNull(peerReview);
        assertNotNull(peerReview.getId());
        assertEquals(id, peerReview.getId());
        assertEquals(url, peerReview.getUrl());
    }

    @Test
    public void create() {
        PeerReview peerReview = new PeerReview();
        peerReview.setUrl("http://www.google.com");

        PeerReview created = peerReviewDAO.create(peerReview);

        PeerReview newPeerReview = peerReviewDAO.getPeerReviewByURL(peerReview.getUrl());

        assertEquals(created.getId(), newPeerReview.getId());
        assertEquals(created.getUrl(), newPeerReview.getUrl());
    }

}
