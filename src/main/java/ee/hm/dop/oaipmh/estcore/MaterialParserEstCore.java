package ee.hm.dop.oaipmh.estcore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.oaipmh.MaterialParser;
import ee.hm.dop.oaipmh.ParseException;
import ee.hm.dop.service.LanguageService;

/**
 * Created by mart on 4.11.15.
 */
public class MaterialParserEstCore implements MaterialParser {

    private static final Logger logger = LoggerFactory.getLogger(MaterialParserEstCore.class);

    @Inject
    private LanguageService languageService;

    @Override
    public Material parse(Document doc) throws ParseException {
        Material material;

        try {
            doc.getDocumentElement().normalize();

            material = new Material();
            Element header = (Element) doc.getElementsByTagName("header").item(0);
            Element identifier = (Element) header.getElementsByTagName("identifier").item(0);
            material.setRepositoryIdentifier(identifier.getTextContent().trim());

            setTitle(material, doc);


        } catch (Exception e) {
            logger.error("Unexpected error while parsing document. Document may not"
                    + " match EstCore mapping or XML structure.", e);
            throw new ParseException(e);
        }

        return material;
    }

    private void setTitle(Material material, Document doc) throws ParseException {
        List<LanguageString> titles;

        try {
            titles = getTitles(doc);
            if (titles.isEmpty()) {
                throw new ParseException("No titles found.");
            }
        } catch (Exception e) {
            throw new ParseException("Error parsing document.");
        }



        material.setTitles(titles);
    }

    private List<LanguageString> getTitles(Document doc) throws ParseException, XPathExpressionException {
        List<LanguageString> titles;

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath
                .compile("//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='title']");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        titles = getLanguageStrings(node);


        return titles;
    }


    private List<LanguageString> getLanguageStrings(Node node) {
        List<LanguageString> languageStrings = new ArrayList<>();
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            LanguageString languageString = new LanguageString();

            String text = node.getChildNodes().item(i).getTextContent().trim();
            if (!text.isEmpty()) {
                languageString.setText(text);

                if (node.getChildNodes().item(i).hasAttributes()) {
                    String languageCode = node.getChildNodes().item(i).getAttributes().item(0).getTextContent().trim();
                    String[] tokens = languageCode.split("-");

                    Language language = languageService.getLanguage(tokens[0]);
                    if (language != null) {
                        languageString.setLanguage(language);
                    } else {
                        String message = "No such language for '%s'. LanguageString will have no Language";
                        logger.warn(String.format(message, languageCode));
                    }
                }

                languageStrings.add(languageString);
            }
        }

        return languageStrings;
    }
}
