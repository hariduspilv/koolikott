package ee.hm.dop.service;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ORG.oclc.oai.harvester2.verb.ListIdentifiers;

/**
 * Created by mart.laus on 9.07.2015.
 */
public class OaiPmhService {
    public static ArrayList<String> identifiers = new ArrayList<>();

    /**
     * Method to get all the material identifiers from Waramu
     *
     * @param baseURL        the url to the repository (Example: http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler)
     * @param from           the date String from which to get the material identifiers
     * @param until          the date String until which to get the material identifiers
     * @param metadataPrefix String to specify which metadata prefix to use (Example: oai_lom)
     * @param setSpec        Specify the set in the query
     * @return an ArrayList of material identifier Strings
     */
    public ArrayList<String> getMaterialIdentifiers(String baseURL, String from, String until, String metadataPrefix,
            String setSpec)
            throws IOException, ParserConfigurationException, SAXException, TransformerException, NoSuchFieldException {

        ListIdentifiers listIdentifiers = getFirstListIdentifier(baseURL, from, until, metadataPrefix, setSpec);
        while (listIdentifiers != null) {
            NodeList errors = listIdentifiers.getErrors();
            if (checkForErrors(errors, listIdentifiers)) {
                break;
            }
            Document doc = listIdentifiers.getDocument();
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("header");
            addIdentifiersToList(nList);

            String resumptionToken = listIdentifiers.getResumptionToken();
            if (resumptionToken == null || resumptionToken.length() == 0) {
                return identifiers;
            } else {
                try {
                    listIdentifiers = getListIdentifierWithToken(baseURL, resumptionToken);
                } catch (Exception e) {
                    //ignore
                }
            }
        }
        return null;
    }

    public ListIdentifiers getFirstListIdentifier(String baseURL, String from, String until, String metadataPrefix,
            String setSpec) throws ParserConfigurationException, TransformerException, SAXException, IOException {
        return new ListIdentifiers(baseURL, from, until, setSpec, metadataPrefix);

    }

    public ListIdentifiers getListIdentifierWithToken(String baseURL, String resumptionToken)
            throws ParserConfigurationException, TransformerException, SAXException, IOException {
        return new ListIdentifiers(baseURL, resumptionToken);

    }

    public void addIdentifiersToList(NodeList nList) {
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String identifier = eElement.getElementsByTagName("identifier").item(0).getTextContent();
                identifiers.add(identifier);
            }
        }
    }

    public boolean checkForErrors(NodeList errors, ListIdentifiers listIdentifiers) {
        if (errors != null && errors.getLength() > 0) {
            System.out.println("Found errors");
            int length = errors.getLength();
            for (int i = 0; i < length; ++i) {
                Node item = errors.item(i);
                System.out.println(item);
            }
            System.out.println("Error record: " + listIdentifiers.toString());
            return true;
        } else {
            return false;
        }
    }
}
