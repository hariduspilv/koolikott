package ee.hm.dop.utils;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import ee.hm.dop.service.OaiPmhService;

/**
 * Created by mart.laus on 14.07.2015.
 */
public class RepositoryManager {
    String from = null;
    String until = null;
    String metadataPrefix = "oai_lom";
    String setSpec = null;
    ArrayList<String> identifiers = new ArrayList<>();

    //get all the materials from Waramu
    public void getMaterials(String baseURL)
            throws ParserConfigurationException, TransformerException, SAXException, NoSuchFieldException, IOException {
        OaiPmhService oaiPmhService = new OaiPmhService();

        identifiers = oaiPmhService.getMaterialIdentifiers(baseURL, from, until, metadataPrefix, setSpec);

        System.out.println("Trying to get " + identifiers.size() + "materials");
        if (identifiers.size() > 0) {
            MaterialParser materialParser = new MaterialParser();
            for (String identifier : identifiers) {
                //Document doc = oaiPmhService.getMaterial(baseURL, identifier, metadataPrefix);
                //materialParser.parseXMLtoMaterial(doc);
            }
            System.out.println("done");
        }
    }
}
