package ee.hm.dop.service.synchronizer.oaipmh;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ORG.oclc.oai.harvester2.verb.ListIdentifiers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IdentifierIterator implements Iterator<Element> {

    private static final Logger logger = LoggerFactory.getLogger(IdentifierIterator.class);

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
    public Element next() {
        try {
            return (Element) headers.item(index++);
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
