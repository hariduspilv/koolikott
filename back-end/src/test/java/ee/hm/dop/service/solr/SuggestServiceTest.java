package ee.hm.dop.service.solr;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

@RunWith(EasyMockRunner.class)
public class SuggestServiceTest extends ResourceIntegrationTestBase {

    @TestSubject
    SuggestService suggestService = new SuggestService();
    @Mock
    SolrService solrService;

    @Test
    public void suggestEmpty() {
        expect(solrService.suggest("")).andReturn(null);
        replay(solrService);
        assertEquals(suggestService.suggest("").getStatus(), 400);
    }
}
