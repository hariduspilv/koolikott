package ee.hm.dop.oaipmh;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ORG.oclc.oai.harvester2.verb.GetRecord;

/**
 * Created by mart.laus on 17.07.2015.
 */
public class GetMaterialConnector {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(IdentifierIterator.class);

    public Document getMaterial(String baseURL, String identifier, String metadataPrefix) throws Exception {
        GetRecord getRecord = null;

        try {
            getRecord = new GetRecord(baseURL, identifier, metadataPrefix);
        } catch (IOException e) {
            String msg = "Repository has identifier %s in ListIdentifiers but no content at that path, moving on to next one.";
            logger.error(String.format(msg, identifier));
        }

        return getDocument(getRecord);

    }

    private Document getDocument(GetRecord getRecord) throws TransformerException {
        Document doc = null;
        if (getRecord != null) {
            NodeList errors = getRecord.getErrors();
            if (!checkForErrors(errors, getRecord)) {
                doc = getRecord.getDocument();
            }
        }
        return doc;
    }

    private boolean checkForErrors(NodeList errors, GetRecord getRecord) {
        if (errors != null && errors.getLength() > 0) {
            logger.error("Found errors when getting one material from repository, errors are:");
            int length = errors.getLength();
            for (int i = 0; i < length; ++i) {
                Node item = errors.item(i);
                logger.error(item.toString());
            }
            logger.error("Error material: " + getRecord.toString());
            return true;
        } else {
            return false;
        }
    }
}
