package ee.hm.dop.service.solr;

import static java.lang.String.format;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.service.solr.SolrService;
import ee.hm.dop.service.solr.SuggestService;
import org.apache.solr.client.solrj.SolrServerException;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by joonas on 1.09.16.
 */

@RunWith(EasyMockRunner.class)
public class SuggestServiceTest extends ResourceIntegrationTestBase {

    @TestSubject
    SuggestService suggestService = new SuggestService();

    @Mock
    SolrService solrService;

    @Test
    public void suggestEmpty() throws IOException, SolrServerException {
        expect(solrService.suggest("", false)).andReturn(null);
        replay(solrService);
        assertEquals(suggestService.suggest("", false).getStatus(), 400);
    }

    @Test
    public void suggestTag() throws IOException, SolrServerException {
        expect(solrService.suggest("matem", true)).andReturn(null);
        replay(solrService);
        assertEquals(suggestService.suggest("matem", true).getStatus(), 200);
    }

}
