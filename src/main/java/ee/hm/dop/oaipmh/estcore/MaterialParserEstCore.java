package ee.hm.dop.oaipmh.estcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.Author;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Subject;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.oaipmh.MaterialParser;
import ee.hm.dop.oaipmh.ParseException;
import ee.hm.dop.service.AuthorService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.service.TaxonService;

public class MaterialParserEstCore extends MaterialParser {

    private static final String AUTHOR = "author";
    private static final Map<String, String> taxonMap;

    static {
        taxonMap = new HashMap<>();
        taxonMap.put("preschoolTaxon", "PRESCHOOLEDUCATION");
        taxonMap.put("basicSchoolTaxon", "BASICEDUCATION");
        taxonMap.put("gymnasiumTaxon", "SECONDARYEDUCATION");
        taxonMap.put("vocationalTaxon", "VOCATIONALEDUCATION");
    }

    @Inject
    private LanguageService languageService;

    @Inject
    private AuthorService authorService;

    @Inject
    private TagService tagService;

    @Inject
    private TaxonService taxonService;

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
    protected String getPathToResourceType() {
        return "//*[local-name()='estcore']/*[local-name()='educational']/*[local-name()='learningResourceType']";
    }

    @Override
    protected void setTags(Material material, Document doc) {
        List<Tag> tags = null;
        try {
            tags = getTags(doc);
        } catch (XPathExpressionException e) {
            //ignore
        }
        material.setTags(tags);
    }


    @Override
    protected void setDescriptions(Material material, Document doc) {
        List<LanguageString> descriptions = null;

        try {
            descriptions = getDescriptions(doc);
        } catch (Exception e) {
            //ignore
        }

        material.setDescriptions(descriptions);
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

    @Override
    protected String getPathToContext() {
        return "//*[local-name()='estcore']/*[local-name()='educational']/*[local-name()='context']";
    }

    @Override
    protected String getPathToLocation() {
        return "//*[local-name()='estcore']/*[local-name()='technical']/*[local-name()='location']";
    }

    @Override
    protected Taxon setEducationalContext(Node taxonPath) {
        for (String tag : taxonMap.keySet()) {
            try {
                XPathExpression expr = xpath.compile("./*[local-name()='" + tag + "']");
                Node node = (Node) expr.evaluate(taxonPath, XPathConstants.NODE);

                if (node != null) {
                    return getTaxon(taxonMap.get(tag), EducationalContext.class);
                }
            } catch (XPathExpressionException e) {
                //ignore
            }
        }
        return null;
    }

    @Override
    protected Taxon setDomain(Node taxonPath, Taxon educationalContext) {
        for (String tag : taxonMap.keySet()) {
            try {
                XPathExpression expr = xpath.compile("./*[local-name()='" + tag + "']/*[local-name()='domain']");
                Node node = (Node) expr.evaluate(taxonPath, XPathConstants.NODE);

                if (node != null) {
                    List<Taxon> domains = new ArrayList<>(((EducationalContext) educationalContext).getDomains());
                    String systemName = getTaxon(node.getTextContent(), Domain.class).getName();

                    for (Taxon taxon : domains) {
                        if (taxon.getName().equals(systemName)) {
                            return taxon;
                        }
                    }
                }
            } catch (XPathExpressionException e) {
                //ignore
            }
        }
        return educationalContext;
    }

    @Override
    protected Taxon setSubject(Node taxonPath, Taxon domain) {
        for (String tag : taxonMap.keySet()) {
            try {
                XPathExpression expr = xpath.compile("./*[local-name()='" + tag + "']/*[local-name()='subject']");
                Node node = (Node) expr.evaluate(taxonPath, XPathConstants.NODE);

                if (node != null) {
                    List<Taxon> subjects = new ArrayList<>(((Domain) domain).getSubjects());
                    String systemName = getTaxon(node.getTextContent(), Subject.class).getName();

                    for (Taxon taxon : subjects) {
                        if (taxon.getName().equals(systemName)) {
                            return taxon;
                        }
                    }
                }
            } catch (XPathExpressionException e) {
                //ignore
            }
        }
        return domain;
    }

    @Override
    protected Taxon getTaxon(String context, Class level) {
        return taxonService.getTaxonByEstCoreName(context, level);
    }

    @Override
    protected List<Node> getTaxonPathNodes(Document doc) {
        List<Node> nodes = new ArrayList<>();
        try {
            XPathExpression expr1 = xpath.compile("//*[local-name()='estcore']/*[local-name()='classification']");
            NodeList classifications = (NodeList) expr1.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < classifications.getLength(); i++) {
                Node classification = classifications.item(i);

                XPathExpression expr2 = xpath
                        .compile("./*[local-name()='taxonPath']");
                NodeList nl = (NodeList) expr2.evaluate(classification, XPathConstants.NODESET);

                if (nl != null && nl.getLength() > 0) {
                    for (int j = 0; j < nl.getLength(); j++) {
                        nodes.add(nl.item(j));
                    }
                }
            }

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return nodes;
    }

    private Language getLanguage(Document doc) throws XPathExpressionException {
        Language language;

        XPathExpression expr = xpath
                .compile("//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='language']");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);

        String[] tokens = node.getFirstChild().getTextContent().trim().split("-");
        language = languageService.getLanguage(tokens[0]);

        return language;
    }

    private List<Author> getAuthors(Document doc) throws ParseException, XPathExpressionException {
        List<Author> authors = new ArrayList<>();

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

        XPathExpression expr = xpath
                .compile("//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='title']");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        titles = getLanguageStrings(node, languageService);

        return titles;
    }


    private List<LanguageString> getDescriptions(Document doc) throws XPathExpressionException {
        List<LanguageString> descriptions;

        XPathExpression expr = xpath
                .compile("//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='description']");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        descriptions = getLanguageStrings(node, languageService);

        return descriptions;
    }

    private List<Tag> getTags(Document doc) throws XPathExpressionException {
        XPathExpression expr = xpath
                .compile("//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='keyword']/*[local-name()='string']");
        NodeList keywords = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        return getTagsFromKeywords(keywords, tagService);
    }
}
