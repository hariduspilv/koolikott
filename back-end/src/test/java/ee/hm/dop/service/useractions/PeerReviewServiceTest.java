package ee.hm.dop.service.useractions;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import ee.hm.dop.dao.PeerReviewDao;
import ee.hm.dop.model.PeerReview;
import ee.hm.dop.service.useractions.PeerReviewService;
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
    private PeerReviewDao peerReviewDao;

    @Test
    public void getPeerReviewByURL(){
        PeerReview peerReview = getPeerReview();

        expect(peerReviewDao.getPeerReviewByURL(peerReview.getUrl())).andReturn(peerReview);

        replay(peerReviewDao);

        PeerReview returned = peerReviewService.getPeerReviewByURL(peerReview.getUrl());

        verify(peerReviewDao);

        assertEquals(peerReview.getUrl(), returned.getUrl());
    }

    @Test
    public void createPeerReview() {
        PeerReview peerReview = getPeerReview();

        expect(peerReviewDao.createOrUpdate(anyObject(PeerReview.class))).andReturn(peerReview);

        replay(peerReviewDao);

        PeerReview returned = peerReviewService.createPeerReview(peerReview.getUrl());

        verify(peerReviewDao);

        assertEquals(peerReview.getUrl(), returned.getUrl());
    }

    private PeerReview getPeerReview(){
        PeerReview peerReview = new PeerReview();
        peerReview.setUrl("http://www.microsoft.com");
        return peerReview;
    }
}
