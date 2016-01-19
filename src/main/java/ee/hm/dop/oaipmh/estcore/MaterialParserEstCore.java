package ee.hm.dop.oaipmh.estcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.Author;
import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.IssueDate;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Publisher;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Module;
import ee.hm.dop.model.taxon.Specialization;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Subtopic;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.Topic;
import ee.hm.dop.oaipmh.MaterialParser;
import ee.hm.dop.oaipmh.ParseException;
import ee.hm.dop.service.AuthorService;
import ee.hm.dop.service.CrossCurricularThemeService;
import ee.hm.dop.service.IssueDateService;
import ee.hm.dop.service.KeyCompetenceService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.PublisherService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.service.TaxonService;

public class MaterialParserEstCore extends MaterialParser {

    private static final String AUTHOR = "AUTHOR";
    private static final Map<String, String> taxonMap;
    public static final String YES = "YES";
    public static final String PUBLISHER = "PUBLISHER";
    public static final String CROSS_CURRICULAR_THEMES = "Cross-curricular_themes";
    public static final String KEY_COMPETENCES = "Key_competences";

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

    @Inject
    private PublisherService publisherService;

    @Inject
    private IssueDateService issueDateService;

    @Inject
    private CrossCurricularThemeService crossCurricularThemeService;

    @Inject
    private KeyCompetenceService keyCompetenceService;

    @Override
    protected void setContributors(Material material, Document doc) {
        try {
            setAuthors(doc, material);
            setPublishersData(doc, material);
        } catch (Exception e) {
            // ignore
        }
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
            // ignore
        }
        material.setTags(tags);
    }

    @Override
    protected void setDescriptions(Material material, Document doc) {
        List<LanguageString> descriptions = null;

        try {
            descriptions = getDescriptions(doc);
        } catch (Exception e) {
            // ignore
        }

        material.setDescriptions(descriptions);
    }

    @Override
    protected void setLanguage(Material material, Document doc) {
        Language language = null;

        try {
            language = getLanguage(doc);
        } catch (Exception e) {
            // ignore
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
                Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']");

                if (node != null) {
                    return getTaxon(taxonMap.get(tag), EducationalContext.class);
                }
            } catch (XPathExpressionException e) {
                // ignore
            }
        }
        return null;
    }

    @Override
    protected Taxon setDomain(Node taxonPath, Taxon educationalContext) {
        for (String tag : taxonMap.keySet()) {
            try {
                Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']/*[local-name()='domain']");

                if (node != null) {
                    List<Taxon> domains = new ArrayList<>(((EducationalContext) educationalContext).getDomains());
                    String systemName = getTaxon(node.getTextContent(), Domain.class).getName();

                    Taxon taxon = getTaxonByName(domains, systemName);
                    if (taxon != null)
                        return taxon;
                }
            } catch (XPathExpressionException e) {
                // ignore
            }
        }
        return educationalContext;
    }

    @Override
    protected Taxon setSubject(Node taxonPath, Taxon domain, Material material) {
        for (String tag : taxonMap.keySet()) {
            try {
                Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']/*[local-name()='subject']");

                if (node != null) {
                    List<Taxon> subjects = new ArrayList<>(((Domain) domain).getSubjects());
                    String systemName = getTaxon(node.getTextContent(), Subject.class).getName();
                    Taxon taxon = getTaxonByName(subjects, systemName);

                    //Special case for adding to Cross-curricular themes to eKoolikott
                    setCrossCurricularThemes(domain, material, taxon);

                    //Special case for adding to Key competences to eKoolikott
                    setKeyCompetences(domain, material, taxon);

                    if (taxon != null)
                        return taxon;
                }
            } catch (XPathExpressionException e) {
                // ignore
            }
        }
        return domain;
    }

    private void setKeyCompetences(Taxon domain, Material material, Taxon taxon) {
        if(taxon != null && domain.getName().equals(KEY_COMPETENCES)) {
            if(material.getKeyCompetences() == null) {
                material.setKeyCompetences(new ArrayList<>());
            }

            List<KeyCompetence> competences = material.getKeyCompetences();
            competences.add(keyCompetenceService.findKeyCompetenceByName(taxon.getName()));
            material.setKeyCompetences(competences);
        }
    }

    private void setCrossCurricularThemes(Taxon domain, Material material, Taxon taxon) {
        if(taxon != null && domain.getName().equals(CROSS_CURRICULAR_THEMES)) {
            if(material.getCrossCurricularThemes() == null) {
                material.setCrossCurricularThemes(new ArrayList<>());
            }

            List<CrossCurricularTheme> themes = material.getCrossCurricularThemes();
            themes.add(crossCurricularThemeService.getThemeByName(taxon.getName()));
            material.setCrossCurricularThemes(themes);
        }
    }

    @Override
    protected Taxon setTopic(Node taxonPath, Taxon parent) {
        for (String tag : taxonMap.keySet()) {
            try {
                Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']/*[local-name()='topic']");

                if (node != null) {
                    List<Taxon> topics = null;
                    if (parent instanceof Module && tag.equals("vocationalTaxon")) {
                        topics = new ArrayList<>(((Module) parent).getTopics());
                    } else if (parent instanceof Domain && tag.equals("preschoolTaxon")) {
                        topics = new ArrayList<>(((Domain) parent).getTopics());
                    } else if (parent instanceof Subject) {
                        topics = new ArrayList<>(((Subject) parent).getTopics());
                    }

                    String systemName = getTaxon(node.getTextContent(), Topic.class).getName();
                    Taxon taxon = getTaxonByName(topics, systemName);
                    if (taxon != null)
                        return taxon;
                }
            } catch (XPathExpressionException e) {
                // ignore
            }
        }
        return parent;
    }

    @Override
    protected Taxon setSpecialization(Node taxonPath, Taxon parent) {
        for (String tag : taxonMap.keySet()) {
            try {
                Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']/*[local-name()='specialization']");

                if (node != null) {
                    List<Taxon> specializations;
                    specializations = new ArrayList<>(((Domain) parent).getSpecializations());

                    String systemName = getTaxon(node.getTextContent(), Specialization.class).getName();
                    Taxon taxon = getTaxonByName(specializations, systemName);
                    if (taxon != null)
                        return taxon;
                }
            } catch (XPathExpressionException e) {
                // ignore
            }
        }
        return parent;
    }

    @Override
    protected Taxon setModule(Node taxonPath, Taxon parent) {
        for (String tag : taxonMap.keySet()) {
            try {
                Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']/*[local-name()='module']");

                if (node != null) {
                    List<Taxon> modules;
                    modules = new ArrayList<>(((Specialization) parent).getModules());

                    String systemName = getTaxon(node.getTextContent(), Module.class).getName();
                    Taxon taxon = getTaxonByName(modules, systemName);
                    if (taxon != null)
                        return taxon;
                }
            } catch (XPathExpressionException e) {
                // ignore
            }
        }
        return parent;
    }

    @Override
    protected Taxon setSubTopic(Node taxonPath, Taxon parent) {
        for (String tag : taxonMap.keySet()) {
            try {
                Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']/*[local-name()='subtopic']");

                if (node != null) {
                    List<Taxon> subtopics;
                    subtopics = new ArrayList<>(((Topic) parent).getSubtopics());

                    String systemName = getTaxon(node.getTextContent(), Subtopic.class).getName();
                    Taxon taxon = getTaxonByName(subtopics, systemName);
                    if (taxon != null)
                        return taxon;
                }
            } catch (XPathExpressionException e) {
                // ignore
            }
        }
        return parent;
    }

    @Override
    protected void setIsPaid(Material material, Document doc) {
        try {
            Node isPaid = getNode(doc,
                    "//*[local-name()='estcore']/*[local-name()='rights']/*[local-name()='cost']/*[local-name()='value']");

            if (isPaid.getTextContent().trim().toUpperCase().equals(YES)) {
                material.setIsPaid(true);
            } else {
                material.setIsPaid(false);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getPathToTargetGroups() {
        return "//*[local-name()='estcore']/*[local-name()='educational']/*[local-name()='typicalAgeRange']";
    }

    @Override
    protected void setPicture(Material material, Document doc) {
        try {
            Node imageNode = getNode(doc, "//*[local-name()='estcore']/*[local-name()='imgSrc']");
            byte[] bytes = Base64.decodeBase64(imageNode.getTextContent());

            material.setPicture(bytes);
        } catch (XPathExpressionException e) {
            // ignore
        }
    }

    @Override
    protected Taxon getTaxon(String context, Class level) {
        return taxonService.getTaxonByEstCoreName(context, level);
    }

    @Override
    protected List<Node> getTaxonPathNodes(Document doc) {
        List<Node> nodes = new ArrayList<>();
        try {
            NodeList classifications = getNodeList(doc, "//*[local-name()='estcore']/*[local-name()='classification']");

            for (int i = 0; i < classifications.getLength(); i++) {
                Node classification = classifications.item(i);

                XPathExpression expr2 = xpath.compile("./*[local-name()='taxonPath']");
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

    private Taxon getTaxonByName(List<Taxon> topics, String systemName) {
        for (Taxon taxon : topics) {
            if (taxon.getName().equals(systemName)) {
                return taxon;
            }
        }
        return null;
    }

    private Language getLanguage(Document doc) throws XPathExpressionException {
        Language language;
        Node node = getNode(doc, "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='language']");

        String[] tokens = node.getFirstChild().getTextContent().trim().split("-");
        language = languageService.getLanguage(tokens[0]);

        return language;
    }

    private void setAuthors(Document doc, Material material) throws ParseException, XPathExpressionException {
        List<Author> authors = new ArrayList<>();
        NodeList nodeList = getNodeList(doc,
                "//*[local-name()='estcore']/*[local-name()='lifeCycle']/*[local-name()='contribute']");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node contributorNode = nodeList.item(i);
            String role = getRoleString(contributorNode);

            if (AUTHOR.equals(role)) {
                String vCard = getVCard(contributorNode);
                setAuthorFromVCard(authors, vCard, authorService);
            }
        }

        material.setAuthors(authors);
    }

    private void setPublishersData(Document doc, Material material) throws ParseException, XPathExpressionException {
        List<Publisher> publishers = new ArrayList<>();
        IssueDate issueDate = null;
        NodeList nodeList = getNodeList(doc,
                "//*[local-name()='estcore']/*[local-name()='lifeCycle']/*[local-name()='contribute']");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node contributorNode = nodeList.item(i);
            String role = getRoleString(contributorNode);

            if (PUBLISHER.equals(role)) {
                String vCard = getVCard(contributorNode);
                setPublisherFromVCard(publishers, vCard, publisherService);

                Node issueDateNode = getNode(contributorNode, "./*[local-name()='date']/*[local-name()='dateTime']");
                DateTime dateTime = new DateTime(issueDateNode.getTextContent().trim());
                issueDate = new IssueDate();

                issueDate.setDay((short) dateTime.getDayOfMonth());
                issueDate.setMonth((short) dateTime.getMonthOfYear());
                issueDate.setYear(dateTime.getYear());
                issueDate = issueDateService.createIssueDate(issueDate);
            }
        }

        material.setPublishers(publishers);
        material.setIssueDate(issueDate);
    }

    private String getRoleString(Node contributorNode) throws XPathExpressionException {
        XPathExpression rolePath = xpath.compile("./*[local-name()='role']/*[local-name()='value']");
        Node roleNode = (Node) rolePath.evaluate(contributorNode, XPathConstants.NODE);
        return roleNode.getTextContent().trim().toUpperCase();
    }

    private String getVCard(Node contributorNode) throws XPathExpressionException {
        String vCard = "";
        NodeList authorNodes = getNode(contributorNode, "./*[local-name()='entity']").getChildNodes();

        for (int j = 0; j < authorNodes.getLength(); j++) {
            if (!authorNodes.item(j).getTextContent().trim().isEmpty()) {
                CharacterData characterData = (CharacterData) authorNodes.item(j);
                return getVCardWithNewLines(characterData);
            }
        }

        return vCard;
    }

    private List<LanguageString> getTitles(Document doc) throws ParseException, XPathExpressionException {
        List<LanguageString> titles;
        Node node = getNode(doc, "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='title']");
        titles = getLanguageStrings(node, languageService);

        return titles;
    }

    private List<LanguageString> getDescriptions(Document doc) throws XPathExpressionException {
        List<LanguageString> descriptions;
        Node node = getNode(doc, "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='description']");
        descriptions = getLanguageStrings(node, languageService);

        return descriptions;
    }

    private List<Tag> getTags(Document doc) throws XPathExpressionException {
        NodeList keywords = getNodeList(doc,
                "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='keyword']/*[local-name()='string']");

        return getTagsFromKeywords(keywords, tagService);
    }
}
