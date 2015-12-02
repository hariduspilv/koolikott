package ee.hm.dop.oaipmh.estcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.Author;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TargetGroup;
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
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.service.TaxonService;

public class MaterialParserEstCore extends MaterialParser {

    private static final String AUTHOR = "AUTHOR";
    private static final Map<String, String> taxonMap;
    public static final String YES = "YES";

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
                Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']");

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
                Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']/*[local-name()='domain']");

                if (node != null) {
                    List<Taxon> domains = new ArrayList<>(((EducationalContext) educationalContext).getDomains());
                    String systemName = getTaxon(node.getTextContent(), Domain.class).getName();

                    Taxon taxon = getTaxonByName(domains, systemName);
                    if (taxon != null) return taxon;
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
                Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']/*[local-name()='subject']");

                if (node != null) {
                    List<Taxon> subjects = new ArrayList<>(((Domain) domain).getSubjects());
                    String systemName = getTaxon(node.getTextContent(), Subject.class).getName();

                    Taxon taxon = getTaxonByName(subjects, systemName);
                    if (taxon != null) return taxon;
                }
            } catch (XPathExpressionException e) {
                //ignore
            }
        }
        return domain;
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
                    if (taxon != null) return taxon;
                }
            } catch (XPathExpressionException e) {
                //ignore
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
                    if (taxon != null) return taxon;
                }
            } catch (XPathExpressionException e) {
                //ignore
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
                    if (taxon != null) return taxon;
                }
            } catch (XPathExpressionException e) {
                //ignore
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
                    if (taxon != null) return taxon;
                }
            } catch (XPathExpressionException e) {
                //ignore
            }
        }
        return parent;
    }

    @Override
    protected void setIsPaid(Material material, Document doc) {
        try {
            Node isPaid = getNode(doc, "//*[local-name()='estcore']/*[local-name()='rights']/*[local-name()='cost']/*[local-name()='value']");

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
    protected void setTargetGroups(Material material, Document doc) {
        Set<TargetGroup> targetGroups = new HashSet<>();
        try {
            NodeList ageRanges = getNodeList(doc, "//*[local-name()='estcore']/*[local-name()='educational']/*[local-name()='typicalAgeRange']");

            for (int i = 0; i < ageRanges.getLength(); i++) {
                String ageRange = ageRanges.item(i).getTextContent().trim();
                String[] ranges = ageRange.split("-");

                if (ranges.length == 2) {
                    int from = Integer.parseInt(ranges[0]);
                    int to = Integer.parseInt(ranges[1]);
                    targetGroups.addAll(TargetGroup.getTargetGroupsByAge(from, to));
                }
            }
        } catch (XPathExpressionException e) {
            //ignore
        }

        material.setTargetGroups(new ArrayList<>(targetGroups));
    }

    @Override
    protected void setPicture(Material material, Document doc) {
        try {
            Node imageNode = getNode(doc, "//*[local-name()='estcore']/*[local-name()='imgSrc']");
            byte[] bytes = Base64.decodeBase64(imageNode.getTextContent());

            material.setPicture(bytes);
        } catch (XPathExpressionException e) {
            //ignore
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

    private List<Author> getAuthors(Document doc) throws ParseException, XPathExpressionException {
        List<Author> authors = new ArrayList<>();
        NodeList nodeList = getNodeList(doc, "//*[local-name()='estcore']/*[local-name()='lifeCycle']/*[local-name()='contribute']");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node contributorNode = nodeList.item(i);
            XPathExpression rolePath = xpath.compile("./*[local-name()='role']/*[local-name()='value']");
            Node role = (Node) rolePath.evaluate(contributorNode, XPathConstants.NODE);

            if (AUTHOR.equals(role.getTextContent().trim().toUpperCase())) {
                getAuthor(authors, contributorNode);
            }
        }

        return authors;
    }

    private void getAuthor(List<Author> authors, Node contributorNode) throws XPathExpressionException {
        String vCard = "";
        NodeList authorNodes = getNode(contributorNode, "./*[local-name()='entity']").getChildNodes();

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
        NodeList keywords = getNodeList(doc, "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='keyword']/*[local-name()='string']");

        return getTagsFromKeywords(keywords, tagService);
    }
}
