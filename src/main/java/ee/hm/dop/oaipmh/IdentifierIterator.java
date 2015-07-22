package ee.hm.dop.oaipmh;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ORG.oclc.oai.harvester2.verb.ListIdentifiers;

public class IdentifierIterator implements Iterator<Node> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(IdentifierIterator.class);

    private String baseURL;
    private NodeList headers;
    private String resumptionToken;
    private int index;

    public IdentifierIterator(NodeList headers, String baseURL, String resumptionToken) throws Exception {
        this.baseURL = baseURL;
        this.resumptionToken = resumptionToken;
        setHeaders(headers);
    }

    private NodeList getHeaders(ListIdentifiers listIdentifiers) {
        Document doc = listIdentifiers.getDocument();
        doc.getDocumentElement().normalize();
        return doc.getElementsByTagName("header");
    }

    private void setHeaders(NodeList headers) {
        this.headers = headers;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        if (headers == null) {
            return false;
        }

        if (index < headers.getLength()) {
            return true;
        }

        ListIdentifiers listIdentifiers = getListIdentifierWithToken();
        if (listIdentifiers != null) {
            setHeaders(getHeaders(listIdentifiers));
        }

        return index < headers.getLength();
    }

    @Override
    public Node next() {
        try {
            return headers.item(index++);
        } catch (Exception e) {
            throw new NoSuchElementException();
        }
    }

    private ListIdentifiers getListIdentifierWithToken() {
        try {
            return newListIdentifier(baseURL, resumptionToken);
        } catch (Exception e) {
            String msg = "Error in getting more identifiers from repo %s with token %s";
            logger.error(String.format(msg, baseURL, resumptionToken), e);
        }

        return null;
    }

    protected ListIdentifiers newListIdentifier(String baseURL, String resumptionToken) throws Exception {
        return new ListIdentifiers(baseURL, resumptionToken);
    }
}
