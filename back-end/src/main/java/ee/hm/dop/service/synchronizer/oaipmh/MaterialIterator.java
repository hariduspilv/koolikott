package ee.hm.dop.service.synchronizer.oaipmh;

import static java.lang.String.format;

import java.util.Iterator;

import javax.inject.Inject;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MaterialIterator implements Iterator<Material> {

    private static final Logger logger = LoggerFactory.getLogger(MaterialIterator.class);

    @Inject
    private ListIdentifiersConnector listIdentifiersConnector;

    @Inject
    private GetMaterialConnector getMaterialConnector;

    private MaterialParser materialParser;
    private Iterator<Element> identifierIterator;
    private Repository repository;

    public Iterator<Material> connect(Repository repository) throws Exception {
        this.repository = repository;
        identifierIterator = listIdentifiersConnector
                .connect(repository.getBaseURL(), repository.getLastSynchronization(), repository.getMetadataPrefix())
                .iterator();
        return this;
    }

    @Override
    public boolean hasNext() {
        return identifierIterator.hasNext();
    }

    @Override
    public Material next() {
        Material material;

        Element header = identifierIterator.next();
        String identifier = header.getElementsByTagName("identifier").item(0).getTextContent();
        logger.info("Next material identifier is: " + identifier);

        if (isDeleted(header)) {
            return buildDeletedMaterial(identifier);
        }

        try {
            Document doc = getMaterialConnector.getMaterial(repository, identifier, repository.getMetadataPrefix());
            material = materialParser.parse(doc);
        } catch (Exception e) {
            String message = "Error getting material (id = %s) from repository (url = %s).";
            logger.error(format(message, identifier, repository.getBaseURL()), e);
            throw new RuntimeException(e);
        }

        return material;
    }

    private Material buildDeletedMaterial(String identifier) {
        Material material = new Material();
        material.setRepositoryIdentifier(identifier);
        material.setDeleted(true);
        return material;
    }

    private boolean isDeleted(Element header) {
        String status = header.getAttribute("status");
        return "deleted".equalsIgnoreCase(status);
    }

    public void setParser(MaterialParser materialParser) {
        this.materialParser = materialParser;
    }
}
