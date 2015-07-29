package ee.hm.dop.oaipmh;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.TestCase.assertNull;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.fail;

import org.easymock.EasyMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;

import ee.hm.dop.model.Repository;

/**
 * Created by mart.laus on 17.07.2015.
 */

@RunWith(EasyMockRunner.class)
public class GetMaterialConnectorTest {

    @Test
    public void getMaterialWrongIdentifier() throws Exception {
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);
        Repository repository = createMock(Repository.class);

        String identifier = "wrongIdentifier";
        String metadataPrefix = "metadataPrefix";

        expect(getMaterialConnector.getMaterial(repository, identifier, metadataPrefix)).andReturn(null);

        replay(getMaterialConnector, repository);

        Document doc = getMaterialConnector.getMaterial(repository, identifier, metadataPrefix);

        verify(getMaterialConnector, repository);

        assertNull(doc);
    }

    @Test
    public void getMaterialNullIdentifier() throws Exception {
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);
        Repository repository = createMock(Repository.class);

        String identifier = null;
        String metadataPrefix = "metadataPrefix";
        expect(getMaterialConnector.getMaterial(repository, identifier, metadataPrefix)).andReturn(null);

        replay(getMaterialConnector, repository);

        Document doc = getMaterialConnector.getMaterial(repository, identifier, metadataPrefix);

        verify(getMaterialConnector, repository);

        assertNull(doc);
    }

    @Test
    public void getMaterialURLNull() throws Exception {
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);
        String baseURL = null;
        Repository repository = createMock(Repository.class);

        String identifier = "identifier";
        String metadataPrefix = "metadataPrefix";
        String errorMsg = "Error happened";

        expect(repository.getBaseURL()).andReturn(baseURL);
        expect(getMaterialConnector.getMaterial(repository, identifier, metadataPrefix))
                .andThrow(new RuntimeException(errorMsg));

        replay(getMaterialConnector, repository);

        try {
            if (repository.getBaseURL() == null) {
                getMaterialConnector.getMaterial(repository, identifier, metadataPrefix);
            }
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals(errorMsg, e.getMessage());
        }

        verify(getMaterialConnector, repository);
    }

    @Test
    public void getMaterialURLWrong() throws Exception {
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);
        Repository repository = createMock(Repository.class);
        String baseURL = "http://invalidURL.com/oai";

        String identifier = "identifier";
        String metadataPrefix = "metadataPrefix";

        expect(repository.getBaseURL()).andReturn(baseURL);
        expect(getMaterialConnector.getMaterial(repository, identifier, metadataPrefix)).andReturn(null);

        replay(getMaterialConnector, repository);

        repository.getBaseURL();
        Document doc = getMaterialConnector.getMaterial(repository, identifier, metadataPrefix);

        verify(getMaterialConnector, repository);

        assertNull(doc);
    }

    @Test
    public void getMaterial() throws Exception {
        Repository repository = createMock(Repository.class);
        String identifier = "identifier";
        String metadataPrefix = "metadataPrefix";

        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);
        Document doc = createMock(Document.class);

        expect(getMaterialConnector.getMaterial(repository, identifier, metadataPrefix)).andReturn(doc);

        replay(getMaterialConnector, doc);
        Document returnedDoc = getMaterialConnector.getMaterial(repository, identifier, metadataPrefix);

        verify(getMaterialConnector, doc);

        assertSame(returnedDoc, doc);
    }
}
