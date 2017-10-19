package ee.hm.dop.service.solr;
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/service/solr/SuggestServiceTest.java

import static java.lang.String.format;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
=======
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/service/solr/SuggestServiceTest.java

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.service.SuggestionStrategy;
import org.apache.solr.client.solrj.SolrServerException;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/service/solr/SuggestServiceTest.java
=======
import java.io.IOException;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/service/solr/SuggestServiceTest.java
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
        expect(solrService.suggest("", SuggestionStrategy.SUGGEST_URL)).andReturn(null);
        replay(solrService);
        assertEquals(suggestService.suggest("", SuggestionStrategy.SUGGEST_URL).getStatus(), 400);
    }

    @Test
    public void suggestTag() throws IOException, SolrServerException {
        expect(solrService.suggest("matem", SuggestionStrategy.SUGGEST_TAG)).andReturn(null);
        replay(solrService);
        assertEquals(suggestService.suggest("matem", SuggestionStrategy.SUGGEST_TAG).getStatus(), 200);
    }

}
