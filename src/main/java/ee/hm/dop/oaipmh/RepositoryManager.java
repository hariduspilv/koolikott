package ee.hm.dop.oaipmh;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by mart.laus on 14.07.2015.
 */
public class RepositoryManager {
    String metadataPrefix = "oai_lom";

    //get all the materials from Waramu
    public void getMaterials(String baseURL) throws Exception {
        ListIdentifiersConnector listIdentifiersConnector = new ListIdentifiersConnector();
        IdentifierIterator identifierIterator = (IdentifierIterator) listIdentifiersConnector
                .connect(baseURL, metadataPrefix).iterator();

        while (identifierIterator.hasNext()) {
            Node header = identifierIterator.next();

            if (header.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) header;
                String identifier = eElement.getElementsByTagName("identifier").item(0).getTextContent();
            }
        }
    }
}
