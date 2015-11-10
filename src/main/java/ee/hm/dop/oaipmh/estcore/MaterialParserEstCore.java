package ee.hm.dop.oaipmh.estcore;

import java.util.List;

import javax.inject.Inject;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.oaipmh.MaterialParser;
import ee.hm.dop.oaipmh.ParseException;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.utils.ParserUtils;

/**
 * Created by mart on 4.11.15.
 */
public class MaterialParserEstCore implements MaterialParser {

    private static final Logger logger = LoggerFactory.getLogger(MaterialParserEstCore.class);

    private static final String[] SCHEMES = {"http", "https"};

    @Inject
    private LanguageService languageService;

    @Override
    public Material parse(Document doc) throws ParseException {
        Material material;

        try {
            doc.getDocumentElement().normalize();
            material = new Material();


            setTitle(material, doc);
            setSource(material, doc);

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

    private void setSource(Material material, Document doc) throws ParseException {
        String source;
        try {
            source = getSource(doc);

        } catch (Exception e) {
            throw new ParseException("Error parsing document.");
        }


        material.setSource(source);
    }

    private List<LanguageString> getTitles(Document doc) throws ParseException, XPathExpressionException {
        List<LanguageString> titles;

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath
                .compile("//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='title']");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        titles = ParserUtils.getLanguageStrings(node, languageService);


        return titles;
    }

    private String getSource(Document doc) throws ParseException, XPathExpressionException {
        String source;

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath
                .compile("//*[local-name()='estcore']/*[local-name()='technical']/*[local-name()='location']");
        NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        if (nodeList.getLength() != 1) {
            throw new ParseException("Material has more or less than one source, can't be mapped.");
        }

        source = nodeList.item(0).getTextContent().trim();
        UrlValidator urlValidator = new UrlValidator(SCHEMES);
        if (!urlValidator.isValid(source)) {
            String message = "Error parsing document. Invalid URL %s";
            throw new ParseException(String.format(message, source));
        }

        return source;
    }
}
