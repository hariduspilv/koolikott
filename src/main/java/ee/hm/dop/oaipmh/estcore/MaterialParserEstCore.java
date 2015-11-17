package ee.hm.dop.oaipmh.estcore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.validator.routines.UrlValidator;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.Author;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.oaipmh.MaterialParser;
import ee.hm.dop.oaipmh.ParseException;
import ee.hm.dop.service.AuthorService;
import ee.hm.dop.service.LanguageService;


/**
 * Created by mart on 4.11.15.
 */
public class MaterialParserEstCore extends MaterialParser {

    private static final String[] SCHEMES = {"http", "https"};
    public static final String AUTHOR = "author";

    @Inject
    private LanguageService languageService;

    @Inject
    private AuthorService authorService;

    private XPathFactory xPathfactory = XPathFactory.newInstance();

    @Override
    protected void setAuthors(Material material, Document doc) {
        List<Author> authors = null;

        try {
            authors = getAuthors(doc);
        } catch (Exception e) {
            //ignore
        }

        material.setAuthors(authors);
    }

    @Override
    protected void setEducationalContext(Material material, Document doc) {

    }

    @Override
    protected void setLearningResourceType(Material material, Document doc) {

    }

    @Override
    protected void setTags(Material material, Document doc) {

    }

    @Override
    protected void setSource(Material material, Document doc) throws ParseException {
        String source;
        try {
            source = getSource(doc);

        } catch (Exception e) {
            throw new ParseException ("Error parsing document source.");
        }

        material.setSource(source);
    }

    @Override
    protected void setDescriptions(Material material, Document doc) {

    }

    @Override
    protected void setLanguage(Material material, Document doc) {
        Language language = null;

        try {
            language = getLanguage(doc);
        } catch (Exception e) {
            //ignore
       }

        material.setLanguage(language);
    }

    @Override
    protected void setTitles(Material material, Document doc) throws ParseException {
        List<LanguageString> titles;

        try {
            titles = getTitles(doc);
            if (titles.isEmpty()) {
                throw new ParseException("No titles found.");
            }
        } catch (Exception e) {
            throw new ParseException("Error parsing document title.");
        }


        material.setTitles(titles);
    }

    private Language getLanguage(Document doc) throws XPathExpressionException {
        Language language;

        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath
                .compile("//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='language']");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);

        String[] tokens = node.getFirstChild().getTextContent().trim().split("-");
        language = languageService.getLanguage(tokens[0]);

        return language;
    }

    private List<Author> getAuthors(Document doc) throws ParseException, XPathExpressionException {
        List<Author> authors = new ArrayList<>();

        XPath xpath = xPathfactory.newXPath();
        XPathExpression contributePath = xpath
                .compile("//*[local-name()='estcore']/*[local-name()='lifeCycle']/*[local-name()='contribute']");
        NodeList nodeList = (NodeList) contributePath.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node contributorNode = nodeList.item(i);
            XPathExpression rolePath = xpath.compile("./*[local-name()='role']/*[local-name()='value']");
            Node role = (Node) rolePath.evaluate(contributorNode, XPathConstants.NODE);

            if (AUTHOR.equals(role.getTextContent().trim())) {
                getAuthor(authors, contributorNode);
            }
        }

        return authors;
    }

    private void getAuthor(List<Author> authors, Node contributorNode) throws XPathExpressionException {
        XPath xpath = xPathfactory.newXPath();
        String vCard = "";

        XPathExpression vCardPath = xpath.compile("./*[local-name()='entity']");
        NodeList authorNodes = ((Node) vCardPath.evaluate(contributorNode, XPathConstants.NODE)).getChildNodes();
        for (int j = 0; j < authorNodes.getLength(); j++) {
            if (!authorNodes.item(j).getTextContent().trim().isEmpty()) {
                CharacterData characterData = (CharacterData) authorNodes.item(j);
                vCard = getVCardWithNewLines(characterData);
            }
        }

        parseVCard(authors, vCard, authorService);
    }



    private List<LanguageString> getTitles(Document doc) throws ParseException, XPathExpressionException {
        List<LanguageString> titles;

        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath
                .compile("//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='title']");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        titles = getLanguageStrings(node, languageService);


        return titles;
    }

    private String getSource(Document doc) throws ParseException, XPathExpressionException {
        String source;

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
