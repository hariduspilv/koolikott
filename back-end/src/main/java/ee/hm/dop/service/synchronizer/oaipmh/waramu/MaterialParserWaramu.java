package ee.hm.dop.service.synchronizer.oaipmh.waramu;

import ee.hm.dop.model.*;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.metadata.LanguageService;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.metadata.TaxonService;
import ee.hm.dop.service.synchronizer.oaipmh.MaterialParser;
import ee.hm.dop.service.synchronizer.oaipmh.MaterialParserUtil;
import ee.hm.dop.service.synchronizer.oaipmh.ParseException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static ee.hm.dop.service.synchronizer.oaipmh.MaterialParserUtil.getFirst;
import static ee.hm.dop.service.synchronizer.oaipmh.MaterialParserUtil.value;
import static ee.hm.dop.service.synchronizer.oaipmh.MaterialParserUtil.valueToUpper;

public class MaterialParserWaramu extends MaterialParser {

    private static final String WEB_PAGE = "WEBPAGE";
    private static final String WEBSITE = "WEBSITE";

    @Inject
    private LanguageService languageService;
    @Inject
    private TagService tagService;

    @Override
    protected void setTags(Material material, Document doc) {
        Element lom = getFirstLom(doc);
        setTags(material, lom);
    }

    @Override
    protected void setDescriptions(Material material, Document doc) {
        Element lom = getFirstLom(doc);
        setDescriptions(material, lom);
    }

    @Override
    protected void setLanguage(Material material, Document doc) {
        Element lom = getFirstLom(doc);
        setMaterialLanguage(material, lom);
    }

    @Override
    protected void setTitles(Material material, Document doc) throws ParseException {
        Element lom = getFirstLom(doc);
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
    protected String getPathToPeerReview() {
        return "//*[local-name()='lom']/*[local-name()='educational']/*[local-name()='peerReview']";
    }

    @Override
    protected String getElementValue(Node node) {
        Node valueNode = getNode(node, "./*[local-name()='value']");

        String value = valueToUpper(valueNode).replaceAll("\\s", "");

        // Waramu specific values
        if (value.equals(WEB_PAGE)) {
            value = WEBSITE;
        }
        return value;
    }

    @Override
    protected void setAuthors(Document doc, Material material) {
        List<Author> authors = new ArrayList<>();
        NodeList nl = getNodeList(doc, getPathToContribute() + "/*[local-name()='entity']");
        NodeList authorNodes = nl.item(0).getChildNodes();

        for (int i = 0; i < authorNodes.getLength(); i++) {

            CharacterData characterData = (CharacterData) authorNodes.item(i);
            String data = getVCardWithNewLines(characterData);

            setAuthorFromVCard(authors, data);
        }

        material.setAuthors(authors);
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
    protected String getPathToContribute() {
        return "//*[local-name()='lom']/*[local-name()='lifeCycle']/*[local-name()='contribute']";
    }

    @Override
    protected String getPathToTargetGroups() {
        return "//*[local-name()='lom']/*[local-name()='educational']/*[local-name()='typicalAgeRange']/*[local-name()='string']";
    }

    @Override
    protected String getPathToCurriculumLiterature() {
        return "//*[local-name()='lom']/*[local-name()='educational']/*[local-name()='curriculumLiterature']";
    }

    @Override
    protected String getPathToClassification() {
        return "//*[local-name()='lom']/*[local-name()='classification']";
    }

    @Override
    protected void setIsPaid(Material material, Document doc) {
    }

    @Override
    protected void setPicture(Material material, Document doc) {
    }

    @Override
    protected void setCrossCurricularThemes(Material material, Document doc) {
    }

    @Override
    protected void setKeyCompetences(Material material, Document doc) {
    }

    @Override
    protected void setEmbedSource(Material material, Document doc) {

    }

    private void setTags(Material material, Element lom) {
        material.setTags(getTags(lom));
    }

    private void setDescriptions(Material material, Element lom) {
        material.setDescriptions(getDescriptions(lom));
    }

    private void setMaterialLanguage(Material material, Element lom) {
        material.setLanguage(getMaterialLanguage(lom));
    }

    private void setTitle(Material material, Element lom) throws ParseException {
        try {
            Node title = getFirst(lom, "title");
            List<LanguageString> titles = getLanguageStrings(title, languageService);
            if (titles.isEmpty()) {
                throw new ParseException("Error parsing document. No title found");
            }
            material.setTitles(titles);
        } catch (Exception e) {
            throw new ParseException("Error in parsing Material title", e);
        }
    }

    private List<Tag> getTags(Element lom) {
        NodeList keywords = lom.getElementsByTagName("keyword");
        return getTagsFromKeywords(keywords, tagService);
    }

    private List<LanguageString> getDescriptions(Element lom) {
        Node description = getFirst(lom, "description");
        return getLanguageStrings(description, languageService);
    }

    private Language getMaterialLanguage(Element lom) {
        Node item = getFirst(lom, "language");
        return languageService.getLanguage(value(item));
    }

    private Element getFirstLom(Document doc) {
        return (Element) doc.getElementsByTagName("lom").item(0);
    }
}