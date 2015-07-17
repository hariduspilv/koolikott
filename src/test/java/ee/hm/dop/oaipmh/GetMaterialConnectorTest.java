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

import ORG.oclc.oai.harvester2.verb.GetRecord;

/**
 * Created by mart.laus on 17.07.2015.
 */

@RunWith(EasyMockRunner.class)
public class GetMaterialConnectorTest {

    @Test
    public void getMaterialWrongIdentifier() throws Exception {
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);

        String baseURL = "hostUrl/correct";
        String identifier = "wrongIdentifier";
        String metadataPrefix = "metadataPrefix";
        expect(getMaterialConnector.getMaterial(baseURL, identifier, metadataPrefix)).andReturn(null);

        replay(getMaterialConnector);

        Document doc = getMaterialConnector.getMaterial(baseURL, identifier, metadataPrefix);

        verify(getMaterialConnector);

        assertNull(doc);
    }

    @Test
    public void getMaterialNullIdentifier() throws Exception {
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);

        String baseURL = "hostUrl/correct";
        String identifier = null;
        String metadataPrefix = "metadataPrefix";
        expect(getMaterialConnector.getMaterial(baseURL, identifier, metadataPrefix)).andReturn(null);

        replay(getMaterialConnector);

        Document doc = getMaterialConnector.getMaterial(baseURL, identifier, metadataPrefix);

        verify(getMaterialConnector);

        assertNull(doc);
    }

    @Test
    public void getMaterialURLNull() throws Exception {
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);

        String baseURL = null;
        String identifier = "identifier";
        String metadataPrefix = "metadataPrefix";
        String errorMsg = "Error happened";

        expect(getMaterialConnector.getMaterial(baseURL, identifier, metadataPrefix))
                .andThrow(new RuntimeException(errorMsg));

        replay(getMaterialConnector);

        try {
            getMaterialConnector.getMaterial(baseURL, identifier, metadataPrefix);
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals(errorMsg, e.getMessage());
        }

        verify(getMaterialConnector);
    }

    @Test
    public void getMaterialURLWrong() throws Exception {
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);

        String baseURL = "http://invalidURL/oai";
        String identifier = "identifier";
        String metadataPrefix = "metadataPrefix";

        expect(getMaterialConnector.getMaterial(baseURL, identifier, metadataPrefix)).andReturn(null);

        replay(getMaterialConnector);

        Document doc = getMaterialConnector.getMaterial(baseURL, identifier, metadataPrefix);

        verify(getMaterialConnector);

        assertNull(doc);
    }

    @Test
    public void getMaterial() throws Exception {
        String baseURL = "http://invalidURL/oai";
        String identifier = "identifier";
        String metadataPrefix = "metadataPrefix";

        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);
        GetRecord getRecord = createMock(GetRecord.class);
        Document doc = createMock(Document.class);

        expect(getMaterialConnector.getMaterial(baseURL, identifier, metadataPrefix)).andReturn(doc);
        expect(getRecord.getDocument()).andReturn(doc);

        replay(getMaterialConnector, getRecord, doc);
        Document returnedDoc = getMaterialConnector.getMaterial(baseURL, identifier, metadataPrefix);

        verify(getMaterialConnector);

        assertSame(returnedDoc, doc);
    }
}
