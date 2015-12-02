package ee.hm.dop.oaipmh;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.Author;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.AuthorService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.ResourceTypeService;
import ee.hm.dop.service.TagService;
import ezvcard.Ezvcard;
import ezvcard.VCard;

public abstract class MaterialParser {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected static final String[] SCHEMES = {"http", "https"};
    protected XPathFactory xPathfactory = XPathFactory.newInstance();
    protected XPath xpath = xPathfactory.newXPath();

    @Inject
    private ResourceTypeService resourceTypeService;

    public Material parse(Document doc) throws ParseException {
        Material material;

        try {
            material = new Material();
            doc.getDocumentElement().normalize();

            setIdentifier(material, doc);
            setTitles(material, doc);
            setLanguage(material, doc);
            setDescriptions(material, doc);
            setSource(material, doc);
            setTags(material, doc);
            setLearningResourceType(material, doc);
            setTaxon(material, doc);
            setAuthors(material, doc);
            setIsPaid(material, doc);
            setTargetGroups(material, doc);
            removeDuplicateTaxons(material);
        } catch (RuntimeException e) {
            logger.error("Unexpected error while parsing document. Document may not"
                    + " match mapping or XML structure - " + e.getMessage(), e);
            throw new ParseException(e);
        }

        return material;
    }

    private void removeDuplicateTaxons(Material material) {
        List<Taxon> taxons = material.getTaxons();
        List<Taxon> uniqueTaxons = new ArrayList<>(taxons);

        for (int i = 0; i < taxons.size(); i++) {
            Taxon first = taxons.get(i);

            for (int j = 0; j < taxons.size(); j++) {
                Taxon second = taxons.get(j);

                if (second.containsTaxon(first) && j != i) {
                    uniqueTaxons.remove(first);
                } else if (first.containsTaxon(second) && j != i) {
                    uniqueTaxons.remove(second);
                }
            }
        }
        material.setTaxons(uniqueTaxons);
    }

    protected void setIdentifier(Material material, Document doc) {
        Element header = (Element) doc.getElementsByTagName("header").item(0);
        Element identifier = (Element) header.getElementsByTagName("identifier").item(0);
        material.setRepositoryIdentifier(identifier.getTextContent().trim());
    }

    protected void parseVCard(List<Author> authors, String data, AuthorService authorService) {
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

    protected List<LanguageString> getLanguageStrings(Node node, LanguageService languageService) {
        List<LanguageString> languageStrings = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            LanguageString languageString = new LanguageString();
            Node currentNode = nodeList.item(i);

            String text = currentNode.getTextContent().trim();
            if (!text.isEmpty()) {
                languageString.setText(text);

                if (currentNode.hasAttributes()) {
                    String languageCode = currentNode.getAttributes().item(0).getTextContent().trim();
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

    protected String getVCardWithNewLines(CharacterData characterData) {
        return characterData.getData().trim().trim().replaceAll("\\n\\s*(?=(\\s*))", "\r\n");
    }

    protected List<Tag> getTagsFromKeywords(NodeList keywords, TagService tagService) {
        List<Tag> tags = new ArrayList<>();
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

    protected List<ResourceType> getResourceTypes(Document doc, String path) throws XPathExpressionException {
        List<ResourceType> resourceTypes = new ArrayList<>();

        NodeList nl = getNodeList(doc, path);

        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            String type = getElementValue(node);

            ResourceType resourceType = resourceTypeService.getResourceTypeByName(type);
            if (!resourceTypes.contains(resourceType) && resourceType != null) {
                resourceTypes.add(resourceType);
            }
        }

        return resourceTypes;
    }

    protected void setEducationalContexts(Document doc, Set<Taxon> taxons, String path) throws XPathExpressionException {
        NodeList nl = getNodeList(doc, path);

        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            String context = getElementValue(node);

            EducationalContext educationalContext = (EducationalContext) getTaxon(context, EducationalContext.class);
            if (educationalContext != null) {
                taxons.add(educationalContext);
            }
        }
    }

    protected String getElementValue(Node node) throws XPathExpressionException {
        return node.getTextContent().trim().toUpperCase();
    }

    protected void setTaxon(Material material, Document doc) {
        Set<Taxon> taxons = new HashSet<>();
        Taxon parent = null;

        try {
            for (Node taxonPath : getTaxonPathNodes(doc)) {
                parent = null;
                parent = setEducationalContext(taxonPath);
                parent = setDomain(taxonPath, parent);

                parent = setSubject(taxonPath, parent);
                parent = setSpecialization(taxonPath, parent);
                parent = setModule(taxonPath, parent);

                parent = setTopic(taxonPath, parent);
                parent = setSubTopic(taxonPath, parent);

                taxons.add(parent);
            }
        } catch (Exception e) {
            taxons.add(parent);
        }

        try {
            setEducationalContexts(doc, taxons, getPathToContext());
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        taxons.removeAll(Collections.singleton(null));
        material.setTaxons(new ArrayList<>(taxons));
    }

    protected void setLearningResourceType(Material material, Document doc) {
        List<ResourceType> resourceTypes = null;

        try {
            resourceTypes = getResourceTypes(doc, getPathToResourceType());
        } catch (Exception e) {
            //ignore if there is no resource type for a material
        }
        material.setResourceTypes(resourceTypes);
    }

    protected void setSource(Material material, Document doc) throws ParseException {
        String source;
        try {
            source = getSource(doc);

        } catch (Exception e) {
            throw new ParseException("Error parsing document source.");
        }

        material.setSource(source);
    }

    private String getSource(Document doc) throws ParseException, XPathExpressionException, URISyntaxException {
        String source;

        NodeList nodeList = getNodeList(doc, getPathToLocation());
        if (nodeList.getLength() != 1) {
            String message = "Material has more or less than one source, can't be mapped.";
            logger.error(String.format(message, message));
            throw new ParseException(message);
        }

        source = nodeList.item(0).getTextContent().trim();

        URI uri = new URI(source);
        if (uri.getScheme() == null) {
            source = "http://" + source;
        }

        UrlValidator urlValidator = new UrlValidator(SCHEMES);
        if (!urlValidator.isValid(source)) {
            String message = "Error parsing document. Invalid URL %s";
            logger.error(String.format(message, source));
            throw new ParseException(String.format(message, source));
        }

        return source;
    }

    protected NodeList getNodeList(Document doc, String path) throws XPathExpressionException {
        XPathExpression expr = xpath.compile(path);
        return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
    }

    protected Node getNode(Node node, String path) throws XPathExpressionException {
        XPathExpression expr = xpath.compile(path);
        return (Node) expr.evaluate(node, XPathConstants.NODE);
    }

    protected abstract void setAuthors(Material material, Document doc);

    protected abstract void setTags(Material material, Document doc);

    protected abstract void setDescriptions(Material material, Document doc);

    protected abstract void setLanguage(Material material, Document doc);

    protected abstract void setTitles(Material material, Document doc) throws ParseException;

    protected abstract String getPathToContext();

    protected abstract String getPathToResourceType();

    protected abstract String getPathToLocation();

    protected abstract Taxon setEducationalContext(Node node);

    protected abstract Taxon setDomain(Node node, Taxon lastTaxon);

    protected abstract Taxon getTaxon(String context, Class level);

    protected abstract List<Node> getTaxonPathNodes(Document doc);

    protected abstract Taxon setSubject(Node node, Taxon lastTaxon);

    protected abstract Taxon setTopic(Node taxonPath, Taxon parent);

    protected abstract Taxon setSpecialization(Node taxonPath, Taxon parent);

    protected abstract Taxon setModule(Node taxonPath, Taxon parent);

    protected abstract Taxon setSubTopic(Node taxonPath, Taxon parent);

    protected abstract void setIsPaid(Material material, Document doc);

    protected abstract void setTargetGroups(Material material, Document doc);
}
