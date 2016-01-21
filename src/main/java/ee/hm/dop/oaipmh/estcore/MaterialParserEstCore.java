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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
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
import ee.hm.dop.service.CrossCurricularThemeService;
import ee.hm.dop.service.KeyCompetenceService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.service.TaxonService;

public class MaterialParserEstCore extends MaterialParser {

    private static final Map<String, String> taxonMap;
    public static final String YES = "YES";

    static {
        taxonMap = new HashMap<>();
        taxonMap.put("preschoolTaxon", "preschoolEducation");
        taxonMap.put("basicSchoolTaxon", "basicEducation");
        taxonMap.put("gymnasiumTaxon", "secondaryEducation");
        taxonMap.put("vocationalTaxon", "vocationalEducation");
    }

    @Inject
    private LanguageService languageService;

    @Inject
    private TagService tagService;

    @Inject
    private TaxonService taxonService;

    @Inject
    private CrossCurricularThemeService crossCurricularThemeService;

    @Inject
    private KeyCompetenceService keyCompetenceService;

    @Override
    protected String getPathToResourceType() {
        return "//*[local-name()='estcore']/*[local-name()='educational']/*[local-name()='learningResourceType']";
    }

    @Override
    protected void setTags(Material material, Document doc) {
        List<Tag> tags = getTags(doc);
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
    protected String getPathToContribute() {
        return "//*[local-name()='estcore']/*[local-name()='lifeCycle']/*[local-name()='contribute']";
    }

    @Override
    protected Taxon setEducationalContext(Node taxonPath) {
        for (String tag : taxonMap.keySet()) {
            Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']");

            if (node != null) {
                return getTaxon(taxonMap.get(tag), EducationalContext.class);
            }

        }
        return null;
    }

    @Override
    protected Taxon setDomain(Node taxonPath, Taxon educationalContext) {
        for (String tag : taxonMap.keySet()) {
            Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']/*[local-name()='domain']");

            if (node != null) {
                List<Taxon> domains = new ArrayList<>(((EducationalContext) educationalContext).getDomains());
                String systemName = getTaxon(node.getTextContent(), Domain.class).getName();

                Taxon taxon = getTaxonByName(domains, systemName);
                if (taxon != null)
                    return taxon;
            }
        }

        return educationalContext;
    }

    @Override
    protected Taxon setSubject(Node taxonPath, Taxon domain, Material material) {
        for (String tag : taxonMap.keySet()) {
            Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']/*[local-name()='subject']");

            if (node != null) {
                List<Taxon> subjects = new ArrayList<>(((Domain) domain).getSubjects());
                String systemName = getTaxon(node.getTextContent(), Subject.class).getName();
                Taxon taxon = getTaxonByName(subjects, systemName);

                if (taxon != null)
                    return taxon;
            }
        }

        return domain;
    }

    @Override
    protected Taxon setTopic(Node taxonPath, Taxon parent) {
        for (String tag : taxonMap.keySet()) {
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
        }

        return parent;
    }

    @Override
    protected Taxon setSpecialization(Node taxonPath, Taxon parent) {
        for (String tag : taxonMap.keySet()) {
            Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']/*[local-name()='specialization']");

            if (node != null) {
                List<Taxon> specializations;
                specializations = new ArrayList<>(((Domain) parent).getSpecializations());

                String systemName = getTaxon(node.getTextContent(), Specialization.class).getName();
                Taxon taxon = getTaxonByName(specializations, systemName);
                if (taxon != null)
                    return taxon;
            }
        }

        return parent;
    }

    @Override
    protected Taxon setModule(Node taxonPath, Taxon parent) {
        for (String tag : taxonMap.keySet()) {
            Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']/*[local-name()='module']");

            if (node != null) {
                List<Taxon> modules;
                modules = new ArrayList<>(((Specialization) parent).getModules());

                String systemName = getTaxon(node.getTextContent(), Module.class).getName();
                Taxon taxon = getTaxonByName(modules, systemName);
                if (taxon != null)
                    return taxon;
            }
        }

        return parent;
    }

    @Override
    protected Taxon setSubTopic(Node taxonPath, Taxon parent) {
        for (String tag : taxonMap.keySet()) {
            Node node = getNode(taxonPath, "./*[local-name()='" + tag + "']/*[local-name()='subtopic']");

            if (node != null) {
                List<Taxon> subtopics;
                subtopics = new ArrayList<>(((Topic) parent).getSubtopics());

                String systemName = getTaxon(node.getTextContent(), Subtopic.class).getName();
                Taxon taxon = getTaxonByName(subtopics, systemName);
                if (taxon != null)
                    return taxon;
            }
        }

        return parent;
    }

    @Override
    protected void setIsPaid(Material material, Document doc) {
        Node isPaid = getNode(doc,
                "//*[local-name()='estcore']/*[local-name()='rights']/*[local-name()='cost']/*[local-name()='value']");

        if (isPaid.getTextContent().trim().toUpperCase().equals(YES)) {
            material.setIsPaid(true);
        } else {
            material.setIsPaid(false);
        }
    }

    @Override
    protected String getPathToTargetGroups() {
        return "//*[local-name()='estcore']/*[local-name()='educational']/*[local-name()='typicalAgeRange']";
    }

    @Override
    protected void setPicture(Material material, Document doc) {
        Node imageNode = getNode(doc, "//*[local-name()='estcore']/*[local-name()='imgSrc']");
        byte[] bytes = Base64.decodeBase64(imageNode.getTextContent());

        material.setPicture(bytes);
    }

    @Override
    protected void setCrossCurricularThemes(Material material, Document doc) {

        List<CrossCurricularTheme> crossCurricularThemes = new ArrayList<>();
        try {
            NodeList classifications = getNodeList(doc, "//*[local-name()='estcore']/*[local-name()='classification']");

            for (int i = 0; i < classifications.getLength(); i++) {
                Node classification = classifications.item(i);

                XPathExpression expr2 = xpath.compile("./*[local-name()='crossCurricularThemesAndCompetences']/*[local-name()='crossCurricularTheme']/*[local-name()='subject']");
                NodeList nl = (NodeList) expr2.evaluate(classification, XPathConstants.NODESET);

                if (nl != null && nl.getLength() > 0) {
                    for (int j = 0; j < nl.getLength(); j++) {
                        CrossCurricularTheme crossCurricularTheme = crossCurricularThemeService.getThemeByName(nl.item(j).getTextContent().trim().replace(" ", "_"));
                        crossCurricularThemes.add(crossCurricularTheme);
                    }
                }
            }

        } catch (XPathExpressionException e) {
            // ignore
        }

        material.setCrossCurricularThemes(crossCurricularThemes);
    }

    @Override
    protected void setKeyCompetences(Material material, Document doc) {

        List<KeyCompetence> keyCompetences = new ArrayList<>();
        try {
            NodeList classifications = getNodeList(doc, "//*[local-name()='estcore']/*[local-name()='classification']");

            for (int i = 0; i < classifications.getLength(); i++) {
                Node classification = classifications.item(i);

                XPathExpression expr2 = xpath.compile("./*[local-name()='crossCurricularThemesAndCompetences']/*[local-name()='keyCompetence']/*[local-name()='subject']");
                NodeList nl = (NodeList) expr2.evaluate(classification, XPathConstants.NODESET);

                if (nl != null && nl.getLength() > 0) {
                    for (int j = 0; j < nl.getLength(); j++) {
                        KeyCompetence keyCompetence = keyCompetenceService.findKeyCompetenceByName(nl.item(j).getTextContent().trim().replace(" ", "_"));
                        keyCompetences.add(keyCompetence);
                    }
                }
            }

        } catch (XPathExpressionException e) {
            // ignore
        }

        material.setKeyCompetences(keyCompetences);
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
            // ignore
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

    private Language getLanguage(Document doc) {
        Language language;
        Node node = getNode(doc, "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='language']");

        String[] tokens = node.getFirstChild().getTextContent().trim().split("-");
        language = languageService.getLanguage(tokens[0]);

        return language;
    }

    private List<LanguageString> getTitles(Document doc) throws ParseException {
        List<LanguageString> titles;
        Node node = getNode(doc, "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='title']");
        titles = getLanguageStrings(node, languageService);

        return titles;
    }

    private List<LanguageString> getDescriptions(Document doc) {
        List<LanguageString> descriptions;
        Node node = getNode(doc, "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='description']");
        descriptions = getLanguageStrings(node, languageService);

        return descriptions;
    }

    private List<Tag> getTags(Document doc) {
        NodeList keywords = getNodeList(doc,
                "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='keyword']/*[local-name()='string']");

        return getTagsFromKeywords(keywords, tagService);
    }
}
