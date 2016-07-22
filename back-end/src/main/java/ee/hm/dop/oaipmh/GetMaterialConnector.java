package ee.hm.dop.oaipmh;

import static java.lang.String.format;

import javax.xml.transform.TransformerException;

import ORG.oclc.oai.harvester2.verb.GetRecord;
import ee.hm.dop.model.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GetMaterialConnector {
    private static final Logger logger = LoggerFactory.getLogger(GetMaterialConnector.class);

    public Document getMaterial(Repository repository, String identifier, String metadataPrefix) throws Exception {
        String logMessage = "Getting material with identifier: %s from repo: %s and metadataPrefix: %s";
        logger.info(format(logMessage, identifier, repository.getBaseURL(), metadataPrefix));

        GetRecord getRecord;
        try {
            getRecord = newGetRecord(repository, identifier, metadataPrefix);
        } catch (Exception e) {
            String errorMessage = "Unexpected error while getting material from repository (url: %s)";
            throw new RuntimeException(format(errorMessage, repository.getBaseURL()));
        }

        return getDocument(getRecord);
    }

    private Document getDocument(GetRecord getRecord) throws TransformerException {
        if (hasErrors(getRecord)) {
            String message = "No document found in repository response.";
            throw new RuntimeException(message);
        }

        return getRecord.getDocument();
    }

    private boolean hasErrors(GetRecord getRecord) throws TransformerException {
        boolean hasError = false;

        NodeList errors = getRecord.getErrors();
        if (errors != null && errors.getLength() > 0) {
            hasError = true;
            logErrors(errors);
        }

        return hasError;
    }

    private void logErrors(NodeList errors) {
        StringBuilder builder = new StringBuilder();
        builder.append("Found errors when getting one material from repository, errors are: ");

        for (int i = 0; i < errors.getLength(); ++i) {
            Node item = errors.item(i);
            builder.append("\n");
            builder.append(item.toString());
        }

        logger.error(builder.toString());
    }

    protected GetRecord newGetRecord(Repository repository, String identifier, String metadataPrefix) throws Exception {
        return new GetRecord(repository.getBaseURL(), identifier, metadataPrefix);
    }
}
