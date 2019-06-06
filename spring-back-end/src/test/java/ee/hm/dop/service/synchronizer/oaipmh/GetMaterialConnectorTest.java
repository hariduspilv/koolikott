package ee.hm.dop.service.synchronizer.oaipmh;

import ORG.oclc.oai.harvester2.verb.GetRecord;
import ee.hm.dop.model.Repository;
import org.easymock.EasyMockRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

@Ignore
@RunWith(EasyMockRunner.class)
public class GetMaterialConnectorTest {

    public static final String IDENTIFIER = "identifier";
    public static final String WRONG_IDENTIFIER = "wrongIdentifier";

    @Test
    public void getMaterialWrongIdentifier() throws Exception {
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);
        Repository repository = createMock(Repository.class);

        expect(getMaterialConnector.getMaterial(repository, WRONG_IDENTIFIER)).andReturn(null);

        replay(getMaterialConnector, repository);

        Document doc = getMaterialConnector.getMaterial(repository, WRONG_IDENTIFIER);

        verify(getMaterialConnector, repository);

        assertNull(doc);
    }

    @Test
    public void getMaterialNullIdentifier() throws Exception {
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);
        Repository repository = createMock(Repository.class);

        expect(getMaterialConnector.getMaterial(repository, null)).andReturn(null);

        replay(getMaterialConnector, repository);

        Document doc = getMaterialConnector.getMaterial(repository, null);

        verify(getMaterialConnector, repository);

        assertNull(doc);
    }

    @Test
    public void getMaterialURLNull() throws Exception {
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);
        Repository repository = createMock(Repository.class);

        String errorMsg = "Error happened";

        expect(repository.getBaseURL()).andReturn(null);
        expect(getMaterialConnector.getMaterial(repository, IDENTIFIER)).andThrow(
                new RuntimeException(errorMsg));

        replay(getMaterialConnector, repository);

        try {
            if (repository.getBaseURL() == null) {
                getMaterialConnector.getMaterial(repository, IDENTIFIER);
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

        expect(repository.getBaseURL()).andReturn(baseURL);
        expect(getMaterialConnector.getMaterial(repository, IDENTIFIER)).andReturn(null);

        replay(getMaterialConnector, repository);

        Document doc = getMaterialConnector.getMaterial(repository, IDENTIFIER);

        verify(getMaterialConnector, repository);

        assertNull(doc);
    }

    @Test
    public void getMaterial() throws Exception {
        Repository repository = createMock(Repository.class);

        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);
        Document doc = createMock(Document.class);

        expect(getMaterialConnector.getMaterial(repository, IDENTIFIER)).andReturn(doc);

        replay(getMaterialConnector, doc);
        Document returnedDoc = getMaterialConnector.getMaterial(repository, IDENTIFIER);

        verify(getMaterialConnector, doc);

        assertSame(returnedDoc, doc);
    }

    @Test(expected = RuntimeException.class)
    public void getMaterialNullData() throws Exception {
        GetMaterialConnector getMaterialConnector = new GetMaterialConnector();
        Repository repository = new Repository();
        getMaterialConnector.getMaterial(repository, IDENTIFIER);

    }

    @Test
    public void getMaterialNoDocument() throws Exception {
        GetMaterialConnector getMaterialConnector = new GetMaterialConnector();
        String errorMessage = "No document found in repository response.";
        Repository repository = getRepository();

        try {
            getMaterialConnector.getMaterial(repository, IDENTIFIER);
            fail("Exception expected.");
        } catch (Exception e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }

    @Test
    public void newGetRecord() throws Exception {
        Repository repository = getRepository();
        GetRecord newGetRecord = new GetRecord(repository.getBaseURL(), IDENTIFIER, repository.getMetadataPrefix());
        assertNotNull(newGetRecord);
    }

    private Repository getRepository() {
        Repository repository = new Repository();
        repository.setBaseURL("http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler");
        repository.setSchema("waramu");
        return repository;
    }
}
