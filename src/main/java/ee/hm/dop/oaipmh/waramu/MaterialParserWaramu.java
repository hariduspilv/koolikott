package ee.hm.dop.oaipmh.waramu;

import java.util.ArrayList;
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
import ee.hm.dop.oaipmh.MaterialParser;
import ee.hm.dop.oaipmh.ParseException;
import ee.hm.dop.service.AuthorService;
import ee.hm.dop.service.EducationalContextService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.ResourceTypeService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.utils.ParserUtils;
import ezvcard.Ezvcard;
import ezvcard.VCard;

public class MaterialParserWaramu implements MaterialParser {
    private static final Logger logger = LoggerFactory.getLogger(MaterialParserWaramu.class);

    private static final String[] SCHEMES = {"http", "https"};
    public static final String WEB_PAGE = "WEBPAGE";
    public static final String WEBSITE = "WEBSITE";
    public static final String COMPULSORYEDUCATION = "COMPULSORYEDUCATION";
    public static final String BASICEDUCATION = "BASICEDUCATION";

    @Inject
    private LanguageService languageService;

    @Inject
    private TagService tagService;

    @Inject
    private ResourceTypeService resourceTypeService;

    @Inject
    private EducationalContextService educationalContextService;

    @Inject
    private AuthorService authorService;

    @Override
    public Material parse(Document doc) throws ParseException {
        Material material;

        try {
            doc.getDocumentElement().normalize();

            material = new Material();
            Element header = (Element) doc.getElementsByTagName("header").item(0);
            Element identifier = (Element) header.getElementsByTagName("identifier").item(0);
            material.setRepositoryIdentifier(identifier.getTextContent().trim());

            Element lom = (Element) doc.getElementsByTagName("lom").item(0);
            setTitle(material, lom);
            setMaterialLanguage(material, lom);
            setDescriptions(material, lom);
            setSource(material, lom);
            setTags(material, lom);
            setLearningResourceType(material, doc);
            setEducationalContext(material, doc);
            setAuthors(material, doc);
        } catch (RuntimeException e) {
            logger.error("Unexpected error while parsing document. Document may not"
                    + " match Waramu mapping or XML structure.", e);
            throw new ParseException(e);
        }

        return material;
    }

    private void setAuthors(Material material, Document doc) {
        List<Author> authors = null;
        try {
            authors = getAuthors(doc);
        } catch (Exception e) {
            //ignore if there is no educational context for a material
        }
        material.setAuthors(authors);
    }

    private void setEducationalContext(Material material, Document doc) {
        List<EducationalContext> educationalContexts = null;
        try {
            educationalContexts = getEducationalContexts(doc);
        } catch (Exception e) {
            //ignore if there is no educational context for a material
        }
        material.setEducationalContexts(educationalContexts);
    }

    private void setLearningResourceType(Material material, Document doc) {
        List<ResourceType> resourceTypes = null;
        try {
            resourceTypes = getResourceTypes(doc);
        } catch (Exception e) {
            //ignore if there is no resource type for a material
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

    private void setDescriptions(Material material, Element lom) throws ParseException {
        List<LanguageString> descriptions = getDescriptions(lom);
        material.setDescriptions(descriptions);
    }

    private void setMaterialLanguage(Material material, Element lom) throws ParseException {
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
        XPathExpression expr = xpath.compile("//*[local-name()='lom']/*[local-name()='educational']/*[local-name()='learningResourceType']");
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            String type = e.getElementsByTagName("value").item(0).getFirstChild().getTextContent().trim().toUpperCase().replaceAll("\\s", "");

            //The only special case where waramu and est-core are different
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

    private List<EducationalContext> getEducationalContexts(Document doc) throws XPathExpressionException {
        List<EducationalContext> educationalContexts = new ArrayList<>();

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("//*[local-name()='lom']/*[local-name()='educational']/*[local-name()='context']");
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            String context = element.getElementsByTagName("value").item(0).getFirstChild().getTextContent().trim().toUpperCase().replaceAll("\\s", "");

            if (context.equals(COMPULSORYEDUCATION)) {
                context = BASICEDUCATION;
            }

            EducationalContext educationalContext = educationalContextService.getEducationalContextByName(context);
            if (!educationalContexts.contains(educationalContext) && educationalContext != null) {
                educationalContexts.add(educationalContext);
            }
        }

        return educationalContexts;
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

            CharacterData child = (CharacterData) authorNodes.item(i);
            String data = child.getData().trim().replaceAll(" (?=[A-Z]{1,99}:)", "\r\n");

            if (data.length() > 0) {
                VCard vcard = Ezvcard.parse(data).first();
                String name = vcard.getStructuredName().getGiven();
                String surname = vcard.getStructuredName().getFamily();

                if (name != null && surname != null) {
                    Author author = authorService.getAuthorByFullName(name, surname);
                    if (author == null) {
                        author = authorService.createAuthor(name, surname);
                        authors.add(author);
                    } else if (!authors.contains(author)) {
                        authors.add(author);
                    }
                }
            }
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

    private List<LanguageString> getDescriptions(Element lom) throws ParseException {
        NodeList descriptionNode = lom.getElementsByTagName("description");
        Node description = descriptionNode.item(0);
        try {
            return ParserUtils.getLanguageStrings(description, languageService);
        } catch (Exception e) {
            throw new ParseException("Error in parsing Material descriptions");
        }
    }

    private Language getMaterialLanguage(Element lom) throws ParseException {
        Language language;
        NodeList languageNode = lom.getElementsByTagName("language");

        try {
            String materialLanguageString = languageNode.item(0).getTextContent().trim();
            language = languageService.getLanguage(materialLanguageString);
        } catch (Exception e) {
            throw new ParseException("Error in parsing Material language");
        }

        return language;
    }

    private List<LanguageString> getTitles(Element lom) throws ParseException {
        List<LanguageString> titles;
        try {
            Node title = lom.getElementsByTagName("title").item(0);
            titles = ParserUtils.getLanguageStrings(title, languageService);
        } catch (Exception e) {
            throw new ParseException("Error in parsing Material title");
        }

        return titles;
    }
}
