package ee.hm.dop.service.synchronizer.oaipmh.estcore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import ee.hm.dop.model.*;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.synchronizer.oaipmh.MaterialParser;
import ee.hm.dop.service.synchronizer.oaipmh.ParseException;
import ee.hm.dop.service.metadata.CrossCurricularThemeService;
import ee.hm.dop.service.metadata.KeyCompetenceService;
import ee.hm.dop.service.metadata.LanguageService;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.metadata.TaxonService;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MaterialParserEstCore extends MaterialParser {

    private static final String YES = "YES";

    @Inject
    private LanguageService languageService;

    @Inject
    private TagService tagService;

    @Inject
    private CrossCurricularThemeService crossCurricularThemeService;

    @Inject
    private KeyCompetenceService keyCompetenceService;

    @Inject
    private TaxonService taxonService;

    @Override
    protected String getPathToResourceType() {
        return "//*[local-name()='estcore']/*[local-name()='educational']/*[local-name()='learningResourceType']";
    }

    @Override
    protected String getPathToPeerReview() {
        return "//*[local-name()='estcore']/*[local-name()='educational']/*[local-name()='peerReview']";
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
            if (titles == null || titles.isEmpty()) {
                throw new ParseException("No titles found.");
            }

            material.setTitles(titles);
        } catch (Exception e) {
            throw new ParseException("Error parsing and setting document titles, repository id: " + material.getRepositoryIdentifier());
        }
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
    protected void setIsPaid(Material material, Document doc) {
        Node isPaid = getNode(doc,
                "//*[local-name()='estcore']/*[local-name()='rights']/*[local-name()='cost']/*[local-name()='value']");

        if (isPaid != null && isPaid.getTextContent().trim().toUpperCase().equals(YES)) {
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
    protected String getPathToCurriculumLiterature() {
        return "//*[local-name()='estcore']/*[local-name()='educational']/*[local-name()='curriculumLiterature']";
    }

    @Override
    protected String getPathToClassification() {
        return "//*[local-name()='estcore']/*[local-name()='classification']";
    }

    @Override
    protected void setPicture(Material material, Document doc) {
        Picture picture = null;

        Node imageNode = getNode(doc, "//*[local-name()='estcore']/*[local-name()='imgSrc']");
        if (imageNode != null) {
            byte[] bytes = Base64.decodeBase64(imageNode.getTextContent());

            if (bytes.length > 0) {
                picture = new OriginalPicture();
                picture.setData(bytes);
            }
        }

        material.setPicture(picture);
    }

    @Override
    protected void setCrossCurricularThemes(Material material, Document doc) {

        List<CrossCurricularTheme> crossCurricularThemes = new ArrayList<>();
        try {
            NodeList classifications = getNodeList(doc, "//*[local-name()='estcore']/*[local-name()='classification']");

            for (int i = 0; i < classifications.getLength(); i++) {
                Node classification = classifications.item(i);

                XPathExpression expr2 = xpath.compile(
                        "./*[local-name()='crossCurricularThemesAndCompetences']/*[local-name()='crossCurricularTheme']/*[local-name()='subject']");
                NodeList nl = (NodeList) expr2.evaluate(classification, XPathConstants.NODESET);

                if (nl != null && nl.getLength() > 0) {
                    for (int j = 0; j < nl.getLength(); j++) {
                        CrossCurricularTheme crossCurricularTheme = crossCurricularThemeService
                                .getThemeByName(nl.item(j).getTextContent().trim().replace(" ", "_"));
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

                XPathExpression expr2 = xpath.compile(
                        "./*[local-name()='crossCurricularThemesAndCompetences']/*[local-name()='keyCompetence']/*[local-name()='subject']");
                NodeList nl = (NodeList) expr2.evaluate(classification, XPathConstants.NODESET);

                if (nl != null && nl.getLength() > 0) {
                    for (int j = 0; j < nl.getLength(); j++) {
                        KeyCompetence keyCompetence = keyCompetenceService
                                .findKeyCompetenceByName(nl.item(j).getTextContent().trim().replace(" ", "_"));
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

    private Language getLanguage(Document doc) {
        Language language;
        Node node = getNode(doc, "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='language']");

        String[] tokens = node.getFirstChild().getTextContent().trim().split("-");
        language = languageService.getLanguage(tokens[0]);

        return language;
    }

    private List<LanguageString> getTitles(Document doc) {
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
