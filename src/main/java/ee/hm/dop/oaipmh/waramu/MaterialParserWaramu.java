package ee.hm.dop.oaipmh.waramu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.validator.routines.UrlValidator;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.Author;
import ee.hm.dop.model.EducationalContext;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.Taxon;
import ee.hm.dop.oaipmh.MaterialParser;
import ee.hm.dop.oaipmh.ParseException;
import ee.hm.dop.service.AuthorService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.ResourceTypeService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.service.TaxonService;
import ee.hm.dop.utils.ParserUtils;
import ezvcard.Ezvcard;
import ezvcard.VCard;

public class MaterialParserWaramu extends MaterialParser {

    private static final String[] SCHEMES = { "http", "https" };
    public static final String WEB_PAGE = "WEBPAGE";
    public static final String WEBSITE = "WEBSITE";
    public static final String COMPULSORY_EDUCATION = "COMPULSORYEDUCATION";
    public static final String BASIC_EDUCATION = "BASICEDUCATION";

    @Inject
    private LanguageService languageService;

    @Inject
    private TagService tagService;

    @Inject
    private ResourceTypeService resourceTypeService;

    @Inject
    private TaxonService taxonService;

    @Inject
    private AuthorService authorService;

    @Override
    protected void setTags(Material material, Document doc) {
        Element lom = (Element) doc.getElementsByTagName("lom").item(0);
        setTags(material, lom);
    }

    @Override
    protected void setSource(Material material, Document doc) throws ParseException {
        Element lom = (Element) doc.getElementsByTagName("lom").item(0);
        setSource(material, lom);
    }

    @Override
    protected void setDescriptions(Material material, Document doc) {
        Element lom = (Element) doc.getElementsByTagName("lom").item(0);
        setDescriptions(material, lom);

    }
            Element lom = (Element) doc.getElementsByTagName("lom").item(0);
            setTitle(material, lom);
            setMaterialLanguage(material, lom);
            setDescriptions(material, lom);
            setSource(material, lom);
            setTags(material, lom);
            setLearningResourceType(material, doc);
            setTaxon(material, doc);
            setAuthors(material, doc);
        } catch (RuntimeException e) {
            logger.error("Unexpected error while parsing document. Document may not"
                    + " match Waramu mapping or XML structure.", e);
            throw new ParseException(e);
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

    protected void setAuthors(Material material, Document doc) {
        List<Author> authors = null;
        try {
            authors = getAuthors(doc);
        } catch (Exception e) {
            // ignore if there is no educational context for a material
        }
        material.setAuthors(authors);
    }

    private void setTaxon(Material material, Document doc) {
        List<Taxon> taxons = null;
    protected void setEducationalContext(Material material, Document doc) {
        List<EducationalContext> educationalContexts = null;
        try {
            taxons = getTaxons(doc);
        } catch (Exception e) {
            // ignore if there is no educational context for a material
        }
        material.setTaxons(taxons);
    }

    protected void setLearningResourceType(Material material, Document doc) {
        List<ResourceType> resourceTypes = null;
        try {
            resourceTypes = getResourceTypes(doc);
        } catch (Exception e) {
            // ignore if there is no resource type for a material
        }
        material.setResourceTypes(resourceTypes);
    }

    private void setTags(Material material, Element lom) {
        List<Tag> tags = getTags(lom);
        material.setTags(tags);
    }

    private void setSource(Material material, Element lom) throws ParseException {
        if (lom.getElementsByTagName("location").getLength() != 1) {
            throw new ParseException("Material has more or less than one source, can't be mapped.");
        }

        NodeList location = lom.getElementsByTagName("location");
        if (location == null) {
            throw new ParseException("Required element 'Location' not found.");
        }

        String url = location.item(0).getTextContent().trim();
        UrlValidator urlValidator = new UrlValidator(SCHEMES);
        if (!urlValidator.isValid(url)) {
            String message = "Error parsing document. Invalid URL %s";
            throw new ParseException(String.format(message, url));
        }

        material.setSource(url);
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

    private List<ResourceType> getResourceTypes(Document doc) throws XPathExpressionException {
        List<ResourceType> resourceTypes = new ArrayList<>();

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath
                .compile("//*[local-name()='lom']/*[local-name()='educational']/*[local-name()='learningResourceType']");
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            String type = e.getElementsByTagName("value").item(0).getFirstChild().getTextContent().trim().toUpperCase()
                    .replaceAll("\\s", "");

            // The only special case where waramu and est-core are different
            if (type.equals(WEB_PAGE)) {
                type = WEBSITE;
            }

            ResourceType resourceType = resourceTypeService.getResourceTypeByName(type);
            if (!resourceTypes.contains(resourceType) && resourceType != null) {
                resourceTypes.add(resourceType);
            }
        }

        return resourceTypes;
    }

    private List<Taxon> getTaxons(Document doc) throws XPathExpressionException {
        Set<Taxon> taxons = new HashSet<>();

        addEducationalContexts(doc, taxons);

        return new ArrayList<Taxon>(taxons);
    }

    private void addEducationalContexts(Document doc, Set<Taxon> taxons) throws XPathExpressionException {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath
                .compile("//*[local-name()='lom']/*[local-name()='educational']/*[local-name()='context']");
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            String context = element.getElementsByTagName("value").item(0).getFirstChild().getTextContent().trim()
                    .toUpperCase().replaceAll("\\s", "");

            if (context.equals(COMPULSORY_EDUCATION)) {
                context = BASIC_EDUCATION;
            }

            EducationalContext educationalContext = taxonService.getEducationalContextByName(context);
            if (educationalContext != null) {
                taxons.add(educationalContext);
            }
        }
    }

    private List<Author> getAuthors(Document doc) throws XPathExpressionException {
        List<Author> authors = new ArrayList<>();

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
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
        List<Tag> tags = new ArrayList<>();
        NodeList keywords = lom.getElementsByTagName("keyword");
        for (int i = 0; i < keywords.getLength(); i++) {
            String keyword = keywords.item(i).getTextContent().trim().toLowerCase();

            if (!keyword.isEmpty()) {
                Tag tag = tagService.getTagByName(keyword);
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(keyword);
                }

                if (!tags.contains(tag)) {
                    tags.add(tag);
                }
            }
        }

        return tags;
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
