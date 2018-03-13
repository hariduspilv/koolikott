package ee.hm.dop.service.synchronizer.oaipmh;

import ee.hm.dop.model.*;
import ee.hm.dop.model.taxon.*;
import ee.hm.dop.service.author.AuthorService;
import ee.hm.dop.service.author.PublisherService;
import ee.hm.dop.service.metadata.LanguageService;
import ee.hm.dop.service.metadata.ResourceTypeService;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.metadata.TargetGroupService;
import ee.hm.dop.service.useractions.PeerReviewService;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;

import javax.inject.Inject;
import javax.xml.xpath.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ee.hm.dop.service.synchronizer.oaipmh.MaterialParserUtil.*;

public abstract class MaterialParser {

    public static final String NO_SUCH_LANGUAGE_FOR_S_LANGUAGE_STRING_WILL_HAVE_NO_LANGUAGE = "No such language for '%s'. LanguageString will have no Language";
    public static final String MATERIAL_HAS_MORE_OR_LESS_THAN_ONE_SOURCE_CAN_T_BE_MAPPED = "Material has more or less than one source, can't be mapped.";
    public static final String ERROR_PARSING_DOCUMENT_INVALID_URL_S = "Error parsing document. Invalid URL %s";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String TAXON_PATH = "./*[local-name()='taxonPath']";
    private static final String[] SCHEMES = {"http", "https"};
    private static final String PUBLISHER = "PUBLISHER";
    private static final String AUTHOR = "AUTHOR";
    private static final Map<String, String> taxonMap = MaterialParserUtil.getTaxonMap();
    private XPath xpath = XPathFactory.newInstance().newXPath();

    @Inject
    private ResourceTypeService resourceTypeService;
    @Inject
    private PeerReviewService peerReviewService;
    @Inject
    private PublisherService publisherService;
    @Inject
    private AuthorService authorService;
    @Inject
    private TargetGroupService targetGroupService;

    public Material parse(Document doc) throws ParseException {
        try {
            Material material = new Material();
            doc.getDocumentElement().normalize();

            setIdentifier(material, doc);
            setContributorsData(material, doc);
            setTitles(material, doc);
            setLanguage(material, doc);
            setDescriptions(material, doc);
            setSource(material, doc);
            setTags(material, doc);
            setLearningResourceType(material, doc);
            setPeerReview(material, doc);
            setTaxon(material, doc);
            setCrossCurricularThemes(material, doc);
            setKeyCompetences(material, doc);
            setIsPaid(material, doc);
            setTargetGroups(material, doc);
            setPicture(material, doc);
            removeDuplicateTaxons(material);
            return material;
        } catch (RuntimeException e) {
            logFail(e);
            throw new ParseException(e);
        }

    }

    protected void setContributorsData(Material material, Document doc) {
        try {
            setAuthors(doc, material);
            setPublishersData(doc, material);
        } catch (Exception e) {
            logger.debug("Error while setting authors or publishers.", e.getMessage());
        }
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

    private void setIdentifier(Material material, Document doc) {
        Element header = (Element) doc.getElementsByTagName("header").item(0);
        Element identifier = (Element) header.getElementsByTagName("identifier").item(0);
        material.setRepositoryIdentifier(value(identifier));
    }

    protected void setAuthorFromVCard(List<Author> authors, String data) {
        if (data.length() > 0) {
            VCard vcard = Ezvcard.parse(data).first();
            String name = vcard.getStructuredName().getGiven();
            String surname = vcard.getStructuredName().getFamily();

            if (name != null && surname != null) {
                Author author = authorService.getAuthorByFullName(name, surname);
                if (author == null) {
                    authors.add(authorService.createAuthor(name, surname));
                } else if (!authors.contains(author)) {
                    authors.add(author);
                }
            }
        }
    }

    private void setPublisherFromVCard(List<Publisher> publishers, String data) {
        if (StringUtils.isNotEmpty(data)) {
            VCard vcard = Ezvcard.parse(data).first();

            if (CollectionUtils.isNotEmpty(vcard.getUrls())) {
                String name = vcard.getFormattedName().getValue();
                String website = vcard.getUrls().get(0).getValue();

                if (name != null && website != null) {
                    Publisher publisher = publisherService.getPublisherByName(name);
                    if (publisher == null) {
                        publishers.add(publisherService.createPublisher(name, website));
                    } else if (!publishers.contains(publisher)) {
                        publishers.add(publisher);
                    }
                }
            }
        }
    }

    protected List<LanguageString> getLanguageStrings(Node node, LanguageService languageService) {
        List<LanguageString> languageStrings = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            String text = value(currentNode);

            if (StringUtils.isNotEmpty(text)) {
                languageStrings.add(new LanguageString(getLanguageCode(languageService, currentNode), text));
            }
        }

        return languageStrings;
    }

    private Language getLanguageCode(LanguageService languageService, Node currentNode) {
        if (!currentNode.hasAttributes()) {
            return null;
        }
        Node item = currentNode.getAttributes().item(0);
        String languageCode = value(item);
        String[] tokens = languageCode.split("-");

        Language language = languageService.getLanguage(tokens[0]);
        if (language != null) {
            return language;
        }
        logger.warn(String.format(NO_SUCH_LANGUAGE_FOR_S_LANGUAGE_STRING_WILL_HAVE_NO_LANGUAGE, languageCode));
        return null;
    }

    protected String getVCardWithNewLines(CharacterData characterData) {
        return characterData.getData().trim().replaceAll("\\n\\s*(?=(\\s*))", "\r\n");
    }

    protected List<Tag> getTagsFromKeywords(NodeList nl, TagService tagService) {
        return getNodeStream(nl)
                .map(MaterialParserUtil::valueToLower)
                .distinct()
                .map(s -> mapTag(tagService, s))
                .collect(Collectors.toList());
    }

    private Tag mapTag(TagService tagService, String s) {
        Tag tag = tagService.getTagByName(s);
        return tag != null ? tag : new Tag(s);
    }

    protected List<ResourceType> getResourceTypes(Document doc, String path) {
        List<String> results = getNodeStream(doc, path)
                .map(this::getElementValue)
                .distinct()
                .collect(Collectors.toList());
        return resourceTypeService.getResourceTypeByName(results);
    }

    private List<PeerReview> getPeerReviews(Document doc, String path) {
        List<String> results = getNodeStream(doc, path)
                .map(this::getElementValue)
                .distinct()
                .collect(Collectors.toList());
        return peerReviewService.getPeerReviewByURL(results);
    }

    private void setTaxon(Material material, Document doc) {
        Set<Taxon> taxons = new HashSet<>();

        try {
            for (Node taxonPath : getTaxonPathNodes(doc)) {
                Taxon parent;
                parent = setEducationalContext(taxonPath);
                parent = setDomain(taxonPath, parent);

                parent = setSubject(taxonPath, parent);
                parent = setSpecialization(taxonPath, parent);
                parent = setModule(taxonPath, parent);

                parent = setTopic(taxonPath, parent);
                parent = setSubTopic(taxonPath, parent);

                if (parent != null) {
                    taxons.add(parent);
                }
            }
        } catch (Exception ignored) {
        }

        //Set contexts that are specified separately, not inside the taxon
        taxons.addAll(getEducationalContexts(doc, getPathToContext()));
        setSpecialEducation(material, doc, getPathToContext());

        material.setTaxons(new ArrayList<>(taxons));
    }

    private List<EducationalContext> getEducationalContexts(Document doc, String path) {
        return getNodeStream(doc, path)
                .map(this::getElementValue)
                .map(context -> (EducationalContext) getTaxon(context, EducationalContext.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void setSpecialEducation(Material material, Document doc, String path) {
        if (getNodeStream(doc, path)
                .map(this::getElementValue)
                .anyMatch(MaterialParserUtil::isSpecialEducation)) {
            material.setSpecialEducation(true);
        }
    }

    protected String getElementValue(Node node) {
        return valueToUpper(node);
    }

    private void setLearningResourceType(Material material, Document doc) {
        try {
            material.setResourceTypes(getResourceTypes(doc, getPathToResourceType()));
        } catch (Exception ignored) {
        }
    }

    private void setPeerReview(Material material, Document doc) {
        try {
            material.setPeerReviews(getPeerReviews(doc, getPathToPeerReview()));
        } catch (Exception ignored) {
        }
    }

    private void setSource(Material material, Document doc) throws ParseException {
        try {
            material.setSource(getSource(doc));
        } catch (Exception e) {
            throw new ParseException("Error parsing document source.", e);
        }
    }

    private String getSource(Document doc) throws ParseException, URISyntaxException {
        NodeList nodeList = getNodeList(doc, getPathToLocation());
        if (nodeList.getLength() != 1) {
            logger.error(MATERIAL_HAS_MORE_OR_LESS_THAN_ONE_SOURCE_CAN_T_BE_MAPPED);
            throw new ParseException(MATERIAL_HAS_MORE_OR_LESS_THAN_ONE_SOURCE_CAN_T_BE_MAPPED);
        }
        String source = getInitialSource(nodeList);

        UrlValidator urlValidator = new UrlValidator(SCHEMES);
        if (!urlValidator.isValid(source)) {
            logger.error(String.format(ERROR_PARSING_DOCUMENT_INVALID_URL_S, source));
            throw new ParseException(String.format(ERROR_PARSING_DOCUMENT_INVALID_URL_S, source));
        }

        return source;
    }

    private String getInitialSource(NodeList nodeList) throws URISyntaxException {
        String source = value(nodeList.item(0));
        URI uri = new URI(source);
        return uri.getScheme() != null ? source : "http://" + source;
    }

    protected void setTargetGroups(Material material, Document doc) {
        Set<TargetGroup> targetGroups = new HashSet<>();
        NodeList ageRanges = getNodeList(doc, getPathToTargetGroups());

        for (int i = 0; i < ageRanges.getLength(); i++) {
            Node item = ageRanges.item(i);
            String[] ranges = value(item).split("-");

            if (ranges.length == 2) {
                int from = Integer.parseInt(ranges[0].trim());
                int to = Integer.parseInt(ranges[1].trim());
                targetGroups.addAll(targetGroupService.getTargetGroupsByAge(from, to));
            }
        }
        material.setTargetGroups(new ArrayList<>(targetGroups));
    }

    protected void setAuthors(Document doc, Material material) {
        List<Author> authors = new ArrayList<>();
        NodeList nodeList = getNodeList(doc, getPathToContribute());

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node contributorNode = nodeList.item(i);
            String role = getRoleString(contributorNode);

            if (AUTHOR.equals(role)) {
                String vCard = getVCard(contributorNode);
                setAuthorFromVCard(authors, vCard);
            }
        }

        material.setAuthors(authors);
    }

    private void setPublishersData(Document doc, Material material) {
        List<Publisher> publishers = new ArrayList<>();
        IssueDate issueDate = null;
        NodeList nodeList = getNodeList(doc, getPathToContribute());

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node contributorNode = nodeList.item(i);
            String role = getRoleString(contributorNode);

            if (PUBLISHER.equals(role)) {
                String vCard = getVCard(contributorNode);
                setPublisherFromVCard(publishers, vCard);

                Node issueDateNode = getNode(contributorNode, "./*[local-name()='date']/*[local-name()='dateTime']");
                if (issueDateNode != null) {
                    issueDate = new IssueDate(new DateTime(value(issueDateNode)));
                }
            }
        }

        material.setPublishers(publishers);
        material.setIssueDate(issueDate);
    }

    private String getRoleString(Node contributorNode) {
        try {
            Node roleNode = getNode(contributorNode, "./*[local-name()='role']/*[local-name()='value']");
            return valueToUpper(roleNode);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getVCard(Node contributorNode) {
        Node node = getNode(contributorNode, "./*[local-name()='entity']");
        if (node == null) {
            return "";
        }
        return getNodeStream(node.getChildNodes())
                .filter(item -> StringUtils.isNotEmpty(value(item)))
                .map(item -> getVCardWithNewLines((CharacterData) item))
                .findFirst()
                .orElse("");
    }

    protected Stream<Node> getNodeStream(Document doc, String path) {
        return getNodeStream(getNodeList(doc, path));
    }

    protected Stream<Node> getNodeStream(NodeList nl) {
        return IntStream.range(0, nl.getLength())
                .mapToObj(nl::item);
    }

    protected NodeList getNodeList(Node node, String path) {
        try {
            XPathExpression expr = xpath.compile(path);
            return (NodeList) expr.evaluate(node, XPathConstants.NODESET);
        } catch (XPathExpressionException ignored) {
            return null;
        }

    }

    protected Node getNode(Node node, String path) {
        try {
            XPathExpression expr = xpath.compile(path);
            return (Node) expr.evaluate(node, XPathConstants.NODE);
        } catch (XPathExpressionException ignored) {
            return null;
        }
    }

    private List<Node> getTaxonPathNodes(Document doc) {
        List<Node> nodes = new ArrayList<>();
        NodeList classifications = getNodeList(doc, getPathToClassification());

        for (int i = 0; i < classifications.getLength(); i++) {
            NodeList nl = getNodeList(classifications.item(i), TAXON_PATH);

            if (notEmpty(nl)) {
                nodes.addAll(getNodeStream(nl).collect(Collectors.toList()));
            }
        }
        return nodes;
    }

    protected Taxon setEducationalContext(Node taxonPath) {
        return taxonMap.entrySet().stream()
                .filter(tag -> getNode(taxonPath, "./*[local-name()='" + tag.getKey() + "']") != null)
                .map(tag -> getTaxon(tag.getValue(), EducationalContext.class))
                .findFirst()
                .orElse(null);
    }

    protected Taxon setDomain(Node taxonPath, Taxon parent) {
        return setTaxon(taxonPath, parent, Domain.class, "domain", (t, key) -> t instanceof EducationalContext);
    }

    protected Taxon setSubject(Node taxonPath, Taxon parent) {
        return setTaxon(taxonPath, parent, Subject.class, "subject", (t, key) -> t instanceof Domain);
    }

    private Taxon setTopic(Node taxonPath, Taxon parent) {
        return setTaxon(taxonPath, parent, Topic.class, "topic", this::topicParentValidation);
    }

    private Taxon setSpecialization(Node taxonPath, Taxon parent) {
        return setTaxon(taxonPath, parent, Specialization.class, "specialization", (t, key) -> t instanceof Domain);
    }

    private Taxon setModule(Node taxonPath, Taxon parent) {
        return setTaxon(taxonPath, parent, Module.class, "module", (t, key) -> t instanceof Specialization);
    }

    private Taxon setSubTopic(Node taxonPath, Taxon parent) {
        return setTaxon(taxonPath, parent, Subtopic.class, "subtopic", (t, key) -> t instanceof Topic);
    }

    private Taxon setTaxon(Node taxonPath, Taxon parent, Class<? extends Taxon> level, String levelString, BiFunction<Taxon, String, Boolean> parentValidator) {
        return taxonMap.keySet().stream()
                .filter(tag -> parentValidator.apply(parent, tag))
                .map(tag -> getTaxonNode(taxonPath, tag, levelString))
                .filter(Objects::nonNull)
                .map(node -> getTaxon(node, parent.getChildrenList(), level))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(parent);
    }

    private boolean topicParentValidation(Taxon parent, String tag) {
        return parent instanceof Module && tag.equals(MaterialParserUtil.VOCATIONAL_TAXON) ||
                parent instanceof Domain && tag.equals(MaterialParserUtil.PRESCHOOL_TAXON) ||
                parent instanceof Subject;
    }

    private Taxon getTaxon(Node node, List<Taxon> list, Class<? extends Taxon> level) {
        String systemName = getTaxon(node.getTextContent(), level).getName();
        return getTaxonByName(list, systemName);
    }

    private Taxon getTaxonByName(List<Taxon> topics, String systemName) {
        return topics.stream().filter(taxon -> taxon.getName().equals(systemName)).findAny().orElse(null);
    }

    protected abstract void setTags(Material material, Document doc);

    protected abstract void setDescriptions(Material material, Document doc);

    protected abstract void setLanguage(Material material, Document doc);

    protected abstract void setTitles(Material material, Document doc) throws ParseException;

    protected abstract String getPathToContext();

    protected abstract String getPathToResourceType();

    protected abstract String getPathToPeerReview();

    protected abstract String getPathToLocation();

    protected abstract String getPathToContribute();

    protected abstract void setIsPaid(Material material, Document doc);

    protected abstract String getPathToTargetGroups();

    protected abstract String getPathToCurriculumLiterature();

    protected abstract void setPicture(Material material, Document doc);

    protected abstract void setCrossCurricularThemes(Material material, Document doc);

    protected abstract void setKeyCompetences(Material material, Document doc);

    protected abstract String getPathToClassification();

    protected abstract Taxon getTaxon(String context, Class level);

    private String taxonPath(String tag, String domain) {
        return "./*[local-name()='" + tag + "']/*[local-name()='" + domain + "']";
    }

    private void logFail(RuntimeException e) {
        logger.error("Unexpected error while parsing document. Document may not"
                + " match mapping or XML structure - " + e.getMessage(), e);
    }

    private Node getTaxonNode(Node taxonPath, String tag, String domain) {
        return getNode(taxonPath, taxonPath(tag, domain));
    }
}
