package ee.hm.dop.oaipmh.waramu;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.Author;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.Taxon;
import ee.hm.dop.oaipmh.MaterialParser;
import ee.hm.dop.oaipmh.ParseException;
import ee.hm.dop.service.AuthorService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.service.TaxonService;

public class MaterialParserWaramu extends MaterialParser {

    protected static final String WEB_PAGE = "WEBPAGE";
    protected static final String WEBSITE = "WEBSITE";

    @Inject
    private LanguageService languageService;

    @Inject
    private TagService tagService;

    @Inject
    private AuthorService authorService;

    @Inject
    private TaxonService taxonService;

    @Override
    protected void setTags(Material material, Document doc) {
        Element lom = (Element) doc.getElementsByTagName("lom").item(0);
        setTags(material, lom);
    }

    @Override
    protected void setDescriptions(Material material, Document doc) {
        Element lom = (Element) doc.getElementsByTagName("lom").item(0);
        setDescriptions(material, lom);

    }

    @Override
    protected void setLanguage(Material material, Document doc) {
        Element lom = (Element) doc.getElementsByTagName("lom").item(0);
        setMaterialLanguage(material, lom);

    }

    @Override
    protected void setTitles(Material material, Document doc) throws ParseException {
        Element lom = (Element) doc.getElementsByTagName("lom").item(0);
        setTitle(material, lom);
    }

    @Override
    protected String getVCardWithNewLines(CharacterData characterData) {
        return characterData.getData().trim().replaceAll("\\s(?=[A-Z]{1,99}:)", "\r\n");
    }

    @Override
    protected String getPathToResourceType() {
        return "//*[local-name()='lom']/*[local-name()='educational']/*[local-name()='learningResourceType']";
    }

    @Override
    protected String getElementValue(Node node) throws XPathExpressionException {
        XPathExpression rolePath = xpath.compile("./*[local-name()='value']");
        Node valueNode = (Node) rolePath.evaluate(node, XPathConstants.NODE);

        String value = valueNode.getTextContent().trim().toUpperCase().replaceAll("\\s", "");

        //Waramu specific values
        if (value.equals(WEB_PAGE)) {
            value = WEBSITE;
        }
        return value;
    }

    @Override
    protected String getPathToContext() {
        return "//*[local-name()='lom']/*[local-name()='educational']/*[local-name()='context']";
    }

    @Override
    protected String getPathToLocation() {
        return "//*[local-name()='lom']/*[local-name()='technical']/*[local-name()='location']";
    }

    @Override
    protected Taxon setEducationalContext(Node node) {
        return null;
    }

    @Override
    protected Taxon setDomain(Node node, Taxon lastTaxon) {
        return null;
    }

    @Override
    protected Taxon getTaxon(String context, Class level) {
        return taxonService.getTaxonByWaramuName(context, level);
    }

    @Override
    protected List<Node> getTaxonPathNodes(Document doc) {
        return null;
    }

    @Override
    protected Taxon setSubject(Node node, Taxon lastTaxon) {
        return null;
    }

    private void setTags(Material material, Element lom) {
        List<Tag> tags = getTags(lom);
        material.setTags(tags);
    }

    protected void setAuthors(Material material, Document doc) {
        List<Author> authors = null;
        try {
            authors = getAuthors(doc);
        } catch (Exception e) {
            //ignore if there is no authors for a material
        }
        material.setAuthors(authors);
    }

    private void setDescriptions(Material material, Element lom) {
        List<LanguageString> descriptions = getDescriptions(lom);
        material.setDescriptions(descriptions);
    }

    private void setMaterialLanguage(Material material, Element lom) {
        Language materialLanguage = getMaterialLanguage(lom);
        material.setLanguage(materialLanguage);
    }

    private void setTitle(Material material, Element lom) throws ParseException {
        List<LanguageString> titles = getTitles(lom);

        if (titles.isEmpty()) {
            throw new ParseException("Error parsing document. No title found");
        }

        material.setTitles(titles);
    }

    private List<Author> getAuthors(Document doc) throws XPathExpressionException {
        List<Author> authors = new ArrayList<>();

        XPathExpression expr = xpath
                .compile("//*[local-name()='lom']/*[local-name()='lifeCycle']/*[local-name()='contribute']/*[local-name()='entity']");
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        NodeList authorNodes = nl.item(0).getChildNodes();

        for (int i = 0; i < authorNodes.getLength(); i++) {

            CharacterData characterData = (CharacterData) authorNodes.item(i);
            String data = getVCardWithNewLines(characterData);

            parseVCard(authors, data, authorService);
        }

        return authors;
    }

    private List<Tag> getTags(Element lom) {
        NodeList keywords = lom.getElementsByTagName("keyword");

        return getTagsFromKeywords(keywords, tagService);
    }

    private List<LanguageString> getDescriptions(Element lom) {
        NodeList descriptionNode = lom.getElementsByTagName("description");
        Node description = descriptionNode.item(0);

        return getLanguageStrings(description, languageService);
    }

    private Language getMaterialLanguage(Element lom) {
        NodeList languageNode = lom.getElementsByTagName("language");
        String materialLanguageString = languageNode.item(0).getTextContent().trim();

        return languageService.getLanguage(materialLanguageString);
    }

    private List<LanguageString> getTitles(Element lom) throws ParseException {
        List<LanguageString> titles;
        try {
            Node title = lom.getElementsByTagName("title").item(0);
            titles = getLanguageStrings(title, languageService);
        } catch (Exception e) {
            throw new ParseException("Error in parsing Material title");
        }

        return titles;
    }
}