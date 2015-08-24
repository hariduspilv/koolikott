package ee.hm.dop.oaipmh;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.easymock.EasyMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;

import ORG.oclc.oai.harvester2.verb.GetRecord;
import ee.hm.dop.model.Repository;

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

        String metadataPrefix = "metadataPrefix";
        expect(getMaterialConnector.getMaterial(repository, null, metadataPrefix)).andReturn(null);

        replay(getMaterialConnector, repository);

        Document doc = getMaterialConnector.getMaterial(repository, null, metadataPrefix);

        verify(getMaterialConnector, repository);

        assertNull(doc);
    }

    @Test
    public void getMaterialURLNull() throws Exception {
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);
        Repository repository = createMock(Repository.class);

        String identifier = "identifier";
        String metadataPrefix = "metadataPrefix";
        String errorMsg = "Error happened";

        expect(repository.getBaseURL()).andReturn(null);
        expect(getMaterialConnector.getMaterial(repository, identifier, metadataPrefix)).andThrow(
                new RuntimeException(errorMsg));

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

    @Test(expected = RuntimeException.class)
    public void getMaterialNullData() throws Exception {
        GetMaterialConnector getMaterialConnector = new GetMaterialConnector();

        String identifier = "identifier";
        String metadataPrefix = "metadataPrefix";
        Repository repository = new Repository();

        getMaterialConnector.getMaterial(repository, identifier, metadataPrefix);

    }

    @Test
    public void getMaterialNoDocument() throws Exception {
        GetMaterialConnector getMaterialConnector = new GetMaterialConnector();
        String errorMessage = "No document found in repository response.";
        String identifier = "identifier";
        String metadataPrefix = "metadataPrefix";
        Repository repository = getRepository();

        try {
            getMaterialConnector.getMaterial(repository, identifier, metadataPrefix);
            fail("Exception expected.");
        } catch (Exception e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }

    @Test
    public void newGetRecord() throws Exception {
        GetMaterialConnector getMaterialConnector = new GetMaterialConnector();
        String identifier = "identifier";
        String metadataPrefix = "metadataPrefix";
        Repository repository = getRepository();

        GetRecord newGetRecord = getMaterialConnector.newGetRecord(repository, identifier, metadataPrefix);

        assertNotNull(newGetRecord);
    }

    private Repository getRepository() {
        Repository repository = new Repository();
        repository.setBaseURL("http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler");
        repository.setSchema("waramu");
        return repository;
    }
}
