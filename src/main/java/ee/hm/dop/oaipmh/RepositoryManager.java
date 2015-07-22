package ee.hm.dop.oaipmh;

import javax.inject.Inject;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ee.hm.dop.model.Material;
import ee.hm.dop.service.MaterialService;

/**
 * Created by mart.laus on 14.07.2015.
 */
public class RepositoryManager {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(IdentifierIterator.class);
    String metadataPrefix = "oai_lom";
    @Inject
    private MaterialParser materialParser;
    @Inject
    private MaterialService materialService;

    public void getMaterials(String baseURL) throws Exception {
        ListIdentifiersConnector listIdentifiersConnector = new ListIdentifiersConnector();
        GetMaterialConnector getMaterialConnector = new GetMaterialConnector();
        IdentifierIterator identifierIterator = (IdentifierIterator) listIdentifiersConnector
                .connect(baseURL, metadataPrefix).iterator();

        while (identifierIterator.hasNext()) {
            Node header = identifierIterator.next();

            if (header.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) header;
                String identifier = eElement.getElementsByTagName("identifier").item(0).getTextContent();
                Document doc = getMaterialConnector.getMaterial(baseURL, identifier, metadataPrefix);
                Material material = null;
                if (doc != null) {
                    material = materialParser.parseXMLtoMaterial(doc);
                }
                if (material != null) {
                    materialService.persistMaterial(material);
                }
            }
        }
    }
}
