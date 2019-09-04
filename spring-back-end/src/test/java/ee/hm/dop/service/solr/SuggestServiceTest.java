package ee.hm.dop.service.solr;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import org.apache.solr.client.solrj.SolrServerException;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.fail;
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
    public void suggestEmpty() throws IOException, SolrServerException {
        expect(solrService.suggest("")).andReturn(null);
        replay(solrService);
        try {
            Response r = (Response) suggestService.suggest("");
            fail("Exception expected");
        } catch (Exception e) {
            String msg = e.getMessage();
            assertEquals("400 BAD_REQUEST", msg);
        }
    }
}
