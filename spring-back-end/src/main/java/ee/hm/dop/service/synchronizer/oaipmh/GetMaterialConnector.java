package ee.hm.dop.service.synchronizer.oaipmh;

import ORG.oclc.oai.harvester2.verb.GetRecord;
import ee.hm.dop.model.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.util.stream.Collectors;

import static ee.hm.dop.service.synchronizer.oaipmh.MaterialParserUtil.nodeStreamOf;
import static ee.hm.dop.service.synchronizer.oaipmh.MaterialParserUtil.notEmpty;
import static java.lang.String.format;

@Component
public class GetMaterialConnector {
    private static final Logger logger = LoggerFactory.getLogger(GetMaterialConnector.class);
    public static final String ERROR_STRING = "Found errors when getting one material from repository, errors are: ";
    public static final String NO_DOCUMENT_FOUND = "No document found in repository response.";
    public static final String GET_MATERIAL = "Getting material with identifier: %s from repo: %s and metadataPrefix: %s";
    public static final String UNEXPECTED_ERROR = "Unexpected error while getting material from repository (url: %s)";

    public Document getMaterial(Repository repository, String identifier) throws Exception {
        logger.info(format(GET_MATERIAL, identifier, repository.getBaseURL(), repository.getMetadataPrefix()));
        GetRecord getRecord = getRecord(repository, identifier);
        return getDocument(getRecord);
    }

    private Document getDocument(GetRecord getRecord) throws TransformerException {
        if (hasErrors(getRecord)) {
            throw new RuntimeException(NO_DOCUMENT_FOUND);
        }
        return getRecord.getDocument();
    }

    private GetRecord getRecord(Repository repository, String identifier) {
        try {
            return new GetRecord(repository.getBaseURL(), identifier, repository.getMetadataPrefix());
        } catch (Exception e) {
            throw new RuntimeException(format(UNEXPECTED_ERROR, repository.getBaseURL()));
        }
    }

    private boolean hasErrors(GetRecord getRecord) throws TransformerException {
        NodeList errors = getRecord.getErrors();
        if (notEmpty(errors)) {
            logErrors(errors);
            return true;
        }
        return false;
    }

    private void logErrors(NodeList errors) {
        String errorString = nodeStreamOf(errors)
                .map(item -> "\n" + item.toString())
                .collect(Collectors.joining("", ERROR_STRING, ""));
        logger.error(errorString);
    }

}
