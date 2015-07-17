package ee.hm.dop.oaipmh;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ORG.oclc.oai.harvester2.verb.ListIdentifiers;

public class ListIdentifiersConnector {

    String resumptionToken;
    private NodeList headers;
    private String baseURL;

    public ListIdentifiersConnector connect(String baseURL, String metadataPrefix) throws Exception {
        ListIdentifiers listIdentifiers = newListIdentifier(baseURL, metadataPrefix);
        headers = getHeaders(listIdentifiers);
        resumptionToken = listIdentifiers.getResumptionToken();
        this.baseURL = baseURL;
        return this;
    }

    public Iterator<Node> iterator() throws Exception {
        return new IdentifierIterator(headers, baseURL, resumptionToken);
    }

    protected ListIdentifiers newListIdentifier(String baseURL, String metadataPrefix) throws Exception {
        return new ListIdentifiers(baseURL, null, null, null, metadataPrefix);
    }

    private NodeList getHeaders(ListIdentifiers listIdentifiers) {
        Document doc = listIdentifiers.getDocument();
        doc.getDocumentElement().normalize();
        return doc.getElementsByTagName("header");
    }
}
