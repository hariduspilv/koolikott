package ee.hm.dop.service;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import ee.hm.dop.dao.PeerReviewDAO;
import ee.hm.dop.model.PeerReview;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class PeerReviewServiceTest {

    @TestSubject
    private PeerReviewService peerReviewService = new PeerReviewService();

    @Mock
    private PeerReviewDAO peerReviewDAO;

    @Test
    public void getPeerReviewByURL(){
        PeerReview peerReview = getPeerReview();

        expect(peerReviewDAO.getPeerReviewByURL(peerReview.getUrl())).andReturn(peerReview);

        replay(peerReviewDAO);

        PeerReview returned = peerReviewService.getPeerReviewByURL(peerReview.getUrl());

        verify(peerReviewDAO);

        assertEquals(peerReview.getUrl(), returned.getUrl());
    }

    @Test
    public void createPeerReview() {
        PeerReview peerReview = getPeerReview();

        expect(peerReviewDAO.create(anyObject(PeerReview.class))).andReturn(peerReview);

        replay(peerReviewDAO);

        PeerReview returned = peerReviewService.createPeerReview(peerReview.getUrl());

        verify(peerReviewDAO);

        assertEquals(peerReview.getUrl(), returned.getUrl());
    }

    private PeerReview getPeerReview(){
        PeerReview peerReview = new PeerReview();
        peerReview.setUrl("http://www.microsoft.com");
        return peerReview;
    }
}
