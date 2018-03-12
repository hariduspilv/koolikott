package ee.hm.dop.service.synchronizer.oaipmh.estcore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import ee.hm.dop.model.*;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.synchronizer.oaipmh.MaterialParser;
import ee.hm.dop.service.synchronizer.oaipmh.MaterialParserUtil;
import ee.hm.dop.service.synchronizer.oaipmh.ParseException;
import ee.hm.dop.service.metadata.CrossCurricularThemeService;
import ee.hm.dop.service.metadata.KeyCompetenceService;
import ee.hm.dop.service.metadata.LanguageService;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.metadata.TaxonService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static ee.hm.dop.service.synchronizer.oaipmh.MaterialParserUtil.value;

public class MaterialParserEstCore extends MaterialParser {
    private static final String YES = "YES";
    public static final String LOCAL_NAME_ESTCORE_LOCAL_NAME_CLASSIFICATION = "//*[local-name()='estcore']/*[local-name()='classification']";
    public static final String CROSS_CURRICULAR_THEME = getCrossOrKeyComp("crossCurricularTheme");
    public static final String KEY_COMPETENCE = getCrossOrKeyComp("keyCompetence");
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
        material.setTags(getTags(doc));
    }

    @Override
    protected void setDescriptions(Material material, Document doc) {
        try {
            material.setDescriptions(getDescriptions(doc));
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void setLanguage(Material material, Document doc) {
        try {
            material.setLanguage(getLanguage(doc));
        } catch (Exception ignored) {
        }

    }

    @Override
    protected void setTitles(Material material, Document doc) throws ParseException {
        try {
            List<LanguageString> titles = getTitles(doc);
            if (CollectionUtils.isEmpty(titles)) {
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
        Node isPaid = getNode(doc, "//*[local-name()='estcore']/*[local-name()='rights']/*[local-name()='cost']/*[local-name()='value']");
        material.setIsPaid(isPaid != null && MaterialParserUtil.valueToUpper(isPaid).equals(YES));
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
        return LOCAL_NAME_ESTCORE_LOCAL_NAME_CLASSIFICATION;
    }

    @Override
    protected void setPicture(Material material, Document doc) {
        Node imageNode = getNode(doc, "//*[local-name()='estcore']/*[local-name()='imgSrc']");
        if (imageNode != null) {
            byte[] bytes = Base64.decodeBase64(imageNode.getTextContent());
            if (bytes.length > 0) {
                Picture picture = new OriginalPicture();
                picture.setData(bytes);
                material.setPicture(picture);
            }
        }
    }

    @Override
    protected void setCrossCurricularThemes(Material material, Document doc) {
        NodeList classifications = getNodeList(doc, LOCAL_NAME_ESTCORE_LOCAL_NAME_CLASSIFICATION);
        List<String> names = getNamesForCrossCurricularOrCompetence(classifications, CROSS_CURRICULAR_THEME);
        material.setCrossCurricularThemes(crossCurricularThemeService.getThemeByName(names));
    }

    @Override
    protected void setKeyCompetences(Material material, Document doc) {
        NodeList classifications = getNodeList(doc, LOCAL_NAME_ESTCORE_LOCAL_NAME_CLASSIFICATION);
        List<String> names = getNamesForCrossCurricularOrCompetence(classifications, KEY_COMPETENCE);
        material.setKeyCompetences(keyCompetenceService.findKeyCompetenceByName(names));
    }

    private List<String> getNamesForCrossCurricularOrCompetence(NodeList classifications, String crossCurricularTheme) {
        List<String> names  = new ArrayList<>();
        for (int i = 0; i < classifications.getLength(); i++) {
            Node classification = classifications.item(i);

            NodeList nl = getNodeList(classification, crossCurricularTheme);

            if (MaterialParserUtil.notEmpty(nl)) {
                List<String> subNames = IntStream.range(0, nl.getLength())
                        .mapToObj(nl::item)
                        .map(MaterialParserUtil::value)
                        .map(s -> s.replace(" ", "_"))
                        .collect(Collectors.toList());
                names.addAll(subNames);
            }
        }
        return names;
    }

    private static String getCrossOrKeyComp(String keyCompetence2) {
        return "./*[local-name()='crossCurricularThemesAndCompetences']/*[local-name()='" + keyCompetence2 + "']/*[local-name()='subject']";
    }

    @Override
    protected Taxon getTaxon(String context, Class level) {
        return taxonService.getTaxonByEstCoreName(context, level);
    }

    private Language getLanguage(Document doc) {
        Node node = getNode(doc, "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='language']");
        String[] tokens = value(node.getFirstChild()).split("-");
        return languageService.getLanguage(tokens[0]);
    }

    private List<LanguageString> getTitles(Document doc) {
        Node node = getNode(doc, "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='title']");
        return getLanguageStrings(node, languageService);
    }

    private List<LanguageString> getDescriptions(Document doc) {
        Node node = getNode(doc, "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='description']");
        return getLanguageStrings(node, languageService);
    }

    private List<Tag> getTags(Document doc) {
        NodeList keywords = getNodeList(doc,
                "//*[local-name()='estcore']/*[local-name()='general']/*[local-name()='keyword']/*[local-name()='string']");
        return getTagsFromKeywords(keywords, tagService);
    }

}
