package ee.hm.dop.service.metadata.LdJson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ee.hm.dop.config.Configuration;
import ee.hm.dop.dao.LanguageDao;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.TranslationGroupDao;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.model.taxon.TaxonPositionDTO;
import ee.hm.dop.service.content.LearningObjectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static ee.hm.dop.model.taxon.TaxonLevel.*;
import static ee.hm.dop.service.metadata.LdJson.LdJsonUtils.*;
import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Service
public class MaterialLdJsonService {

    public static final String HTTP_SCHEMA_ORG = "http://schema.org/";
    public static final String CREATIVE_WORK = "CreativeWork";
    public static final String USER_PAGE_VISITS = " UserPageVisits";
    public static final String ORGANIZATION = "Organization";
    public static final String HITSA = "HITSA";
    public static final String TYPE = "@type";
    public static final String REST_PICTURE_THUMBNAIL_LG = "/rest/picture/thumbnail/lg/";
    public static final String NAME = "name";
    public static final String REGEX_TAG = "\\<.*?\\>";
    public static final String REGEX_UNDERSCORE = "_";
    public static final String OPPEKAVA_EDU_EE_A = "https://oppekava.edu.ee/a/";
    public static final String CONTEXT = "@context";
    public static final String URL = "url";
    public static final String ESTONIAN_NATIONAL_CURRICULUM = "Estonian National Curriculum";
    public static final String EDUCATIONAL_SUBJECT = "educationalSubject";
    public static final String EDUCATIONAL_SUBJECT_AREA = "educationalSubjectArea";
    public static final String TARGET_URL = "targetUrl";
    public static final String LIST_ITEM = "ListItem";
    public static final String POSITION = "position";
    public static final String ITEM = "item";
    public static final String TARGET_NAME = "targetName";
    public static final String EDUCATIONAL_FRAMEWORK = "educationalFramework";
    public static final String ALIGNMENT_OBJECT = "AlignmentObject";
    public static final String ALIGNMENT_TYPE = "alignmentType";
    public static final String TYPE_ = "type";
    public static final String DOMAIN_ = "DOMAIN_";
    public static final String EKOOLIKOTT_PNG = "https://e-koolikott.ee/ekoolikott.png";
    public static final String LOGO = "logo";
    public static final String E_KOOLIKOTT_EE = "https://www.e-koolikott.ee/";
    public static final String BREADCRUMB_LIST = "BreadcrumbList";
    public static final String WEB_SITE = "WebSite";
    public static final String SEARCH_TERM_STRING = "https://e-koolikott.ee/search/result?q={search_term_string}";
    public static final String E_KOOLIKOTT = "E-koolikott";
    public static final String GR = "GR";
    public static final String EDUCATIONAL_LEVEL = "educationalLevel";
    public static final String PERSON = "Person";
    public static final String GIVEN_NAME = "givenName";
    public static final String FAMILY_NAME = "familyName";
    public static final String NAME_FORMAT = "%s %s";
    public static final String L = "L";
    public static final String P = "P";
    public static final String REQUIRED_NAME_SEARCH_TERM_STRING = "required name=search_term_string";
    public static final String NA = "NA";
    public static final String EMPTY_STRING = "";
    public static final String SPACE_STRING = " ";
    public static final String HTTPS_E_KOOLIKOTT_EE_SEARCH_RESULT_TAXON = "https://e-koolikott.ee/search/result/?taxon=";

    private final LearningObjectService learningObjectService;
    private final MaterialDao materialDao;
    private final TranslationGroupDao translationGroupDao;
    private final LanguageDao languageDao;
    private final Configuration configuration;

    public MaterialLdJsonService(LearningObjectService learningObjectService, MaterialDao materialDao, TranslationGroupDao translationGroupDao, LanguageDao languageDao, Configuration configuration) {
        this.learningObjectService = learningObjectService;
        this.materialDao = materialDao;
        this.translationGroupDao = translationGroupDao;
        this.languageDao = languageDao;
        this.configuration = configuration;
    }

    public String getMaterialLdJson(long materialId) {
        Material material = findAndSetTaxonPosition(materialId);

        if (material != null && showMaterialLdJson(material)) {
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(createCreativeWork(material));
            jsonArray.add(createWebSite());
            jsonArray.add(createOrganisation());
            if (isNotEmpty(material.getTaxonPositionDto()) && createBreadCrumbList(material) != null) {
                jsonArray.add(createBreadCrumbList(material));
            }
            if (isNotEmpty(material.getPeerReviews())) jsonArray.add(createPeerReview());

            return jsonArray.toString();
        }
        return EMPTY_STRING;
    }

    private JsonObject createOrganisation() {
        JsonObject jsonOrganisation = new JsonObject();
        jsonOrganisation.addProperty(CONTEXT, HTTP_SCHEMA_ORG);
        jsonOrganisation.addProperty(TYPE, ORGANIZATION);
        jsonOrganisation.addProperty(URL, E_KOOLIKOTT_EE);
        jsonOrganisation.addProperty(LOGO, EKOOLIKOTT_PNG);

        return jsonOrganisation;
    }

    private JsonObject createBreadCrumbList(Material material) {
        JsonObject breadCrumbList = new JsonObject();
        JsonArray itemListElement = new JsonArray();

        breadCrumbList.addProperty(CONTEXT, HTTP_SCHEMA_ORG);
        breadCrumbList.addProperty(TYPE, BREADCRUMB_LIST);

        JsonObject itemList_1 = new JsonObject();
        String listItem_1 = translationGroupDao.getTranslationByKeyAndLangcode(material.getTaxonPositionDto().get(0).getTaxonLevelName(), languageDao.findByCode(material.getLanguage().getCode()).getId());

        if (listItem_1 == null) {
            return null;
        }

        itemList_1.addProperty(TYPE, LIST_ITEM);
        itemList_1.addProperty(POSITION, 1);
        itemList_1.addProperty(NAME, listItem_1);
        itemList_1.addProperty(ITEM, HTTPS_E_KOOLIKOTT_EE_SEARCH_RESULT_TAXON + material.getTaxonPositionDto().get(0).getTaxonLevelId());
        itemListElement.add(itemList_1);

        JsonObject itemList_2 = new JsonObject();
        String listItem_2 = translationGroupDao.getTranslationByKeyAndLangcode(DOMAIN_ + (material.getTaxonPositionDto().get(1).getTaxonLevelName()).toUpperCase(), languageDao.findByCode(material.getLanguage().getCode()).getId());

        if (listItem_2 == null) {
            return null;
        }

        itemList_2.addProperty(TYPE, LIST_ITEM);
        itemList_2.addProperty(POSITION, 2);
        itemList_2.addProperty(NAME, listItem_2);
        itemList_2.addProperty(ITEM, HTTPS_E_KOOLIKOTT_EE_SEARCH_RESULT_TAXON + material.getTaxonPositionDto().get(1).getTaxonLevelId());
        itemListElement.add(itemList_2);
        breadCrumbList.add("itemListElement", itemListElement);

        return breadCrumbList;
    }

    private JsonObject createWebSite() {
        JsonObject jsonWebSite = new JsonObject();
        JsonObject potentialAction = new JsonObject();

        jsonWebSite.addProperty(CONTEXT, HTTP_SCHEMA_ORG);
        jsonWebSite.addProperty(TYPE, WEB_SITE);
        jsonWebSite.addProperty(URL, E_KOOLIKOTT_EE);

        potentialAction.addProperty(TYPE, "SearchAction");
        potentialAction.addProperty("target", SEARCH_TERM_STRING);
        potentialAction.addProperty("query-input", REQUIRED_NAME_SEARCH_TERM_STRING);
        jsonWebSite.add("potentialAction", potentialAction);

        return jsonWebSite;
    }

    private JsonObject createPeerReview() {
        JsonObject peerReview = new JsonObject();
        JsonObject reviewRating = new JsonObject();
        JsonObject publisher = new JsonObject();

        peerReview.addProperty(TYPE, "Review");

        reviewRating.addProperty(TYPE, "Rating");
        reviewRating.addProperty("ratingValue", 5);
        reviewRating.addProperty("bestRating", 5);
        peerReview.add("reviewRating", reviewRating);

        peerReview.addProperty("reviewBody", "Vastab nõuetele");

        publisher.addProperty(TYPE, ORGANIZATION);
        publisher.addProperty(NAME, "e-koolikott.ee");
        peerReview.add("publisher", publisher);

        return peerReview;
    }

    private JsonObject createCreativeWork(Material material) {

        String language = convertLanguages(material);

        JsonObject jsonMaterial = new JsonObject();
        if (material.getSource() != null) {
            jsonMaterial.addProperty("@id", material.getSource());
        }
        jsonMaterial.addProperty(CONTEXT, HTTP_SCHEMA_ORG);
        jsonMaterial.addProperty(TYPE, CREATIVE_WORK);
        if (createResourceType(material).size() > 0) {
            jsonMaterial.add("learningResourceType", createResourceType(material));
        }
        if (createAbout(material).size() > 0) {
            jsonMaterial.add("about", createAbout(material));
        }
        if (createDateCreated(material) != null) {
            jsonMaterial.addProperty("datePublished", createDateCreated(material));
        }
        if (material.getIssueDate() != null) {
            jsonMaterial.addProperty("dateCreated", convertIssueDateToLocalDate(material.getIssueDate()).toString());
        }
        if (createThumbnailUrl(material) != null) {
            jsonMaterial.addProperty("thumbnailUrl", createThumbnailUrl(material));
        }
        if (createLicence(material) != null) {
            jsonMaterial.addProperty("license", createLicence(material));
        }
        if (createTypicalAgeRange(material).size() > 0) {
            jsonMaterial.add("typicalAgeRange", createTypicalAgeRange(material));
        }
        if (material.getViews() != null) {
            jsonMaterial.addProperty("interactionCount", material.getViews() + USER_PAGE_VISITS);
        }
        if (createHeadline(material, language) != null) {
            jsonMaterial.addProperty("headline", createHeadline(material, language));
        }
        if (createKeywords(material).size() > 0) {
            jsonMaterial.add("keywords", createKeywords(material));
        }
        if (createAbstract(material, language) != null) {
            jsonMaterial.addProperty("abstract", createAbstract(material, language));
        }
        jsonMaterial.addProperty("inLanguage", language);
        if (createAuthors(material).size() > 0) {
            jsonMaterial.add("author", createAuthors(material));
        }
        jsonMaterial.add("publisher", createPublisher(material));
        jsonMaterial.add("sdPublisher", createSdPublisher());
        if (createAudience(material).size() > 0) {
            jsonMaterial.add("audience", createAudiences(material));
        }
        jsonMaterial.add("educationalAlignment", createEducationalAlignment(material));

        return jsonMaterial;
    }

    private String createLicence(Material material) {
        return material.getLicenseType() != null && material.getLicenseType().getName() != null ? findLicenseType(material.getLicenseType().getName()) : null;
    }

    private JsonObject createAudiences(Material material) {
        JsonObject jsonAudience = new JsonObject();
        jsonAudience.addProperty(TYPE, "Audience");
        jsonAudience.add("audienceType", createAudience(material));
        return jsonAudience;
    }

    private JsonObject createSdPublisher() {
        JsonObject jsonSdPublisher = new JsonObject();
        jsonSdPublisher.addProperty(TYPE, ORGANIZATION);
        jsonSdPublisher.addProperty(NAME, HITSA);
        return jsonSdPublisher;
    }

    private JsonObject createPublisher(Material material) {
        JsonObject jsonPublisher = new JsonObject();
        if (isNotEmpty(material.getPublishers())) {
            jsonPublisher.addProperty(TYPE, ORGANIZATION);
            jsonPublisher.addProperty(NAME, material.getPublishers().get(0).getName());

        } else {
            jsonPublisher.addProperty(TYPE, ORGANIZATION);
            jsonPublisher.addProperty(NAME, E_KOOLIKOTT);
            jsonPublisher.addProperty(URL, E_KOOLIKOTT_EE);
            jsonPublisher.addProperty(LOGO, EKOOLIKOTT_PNG);
        }
        return jsonPublisher;
    }

    private String createAbstract(Material material, String language) {
        String description = null;
        if (isNotEmpty(material.getDescriptions())) {
            switch (language.toLowerCase()) {
                case (ET):
                    description = material.getDescriptions().get(0).getText().replaceAll(REGEX_TAG, EMPTY_STRING);
                    break;
                case (RU):
                    description = material.getDescriptions().get(2).getText().replaceAll(REGEX_TAG, EMPTY_STRING);
                    break;
                case (EN):
                    description = material.getDescriptions().get(1).getText().replaceAll(REGEX_TAG, EMPTY_STRING);
                    break;
                default:
                    description = material.getDescriptions().get(0).getText().replaceAll(REGEX_TAG, EMPTY_STRING);
                    break;
            }
        }
        return description;
    }

    private String createHeadline(Material material, String language) {

        String headline = null;
        if (isNotEmpty(material.getTitles())) {
            switch (language.toLowerCase()) {
                case (ET):
                    headline = material.getTitles().get(0).getText();
                    break;
                case (RU):
                    headline = material.getTitles().get(2).getText();
                    break;
                case (EN):
                    headline = material.getTitles().get(1).getText();
                    break;
                default:
                    headline = material.getTitles().get(0).getText();
                    break;
            }
        }
        return headline;
    }

    private JsonArray createKeywords(Material material) {
        JsonArray keywords = new JsonArray();
        if (isNotEmpty(material.getTags())) {
            material.getTags().forEach(tag -> keywords.add(tag.getName()));
        }
        return keywords;
    }

    private JsonArray createResourceType(Material material) {
        JsonArray resources = new JsonArray();
        if (isNotEmpty(material.getResourceTypes())) {
            material.getResourceTypes().forEach(resourceType -> resources.add(translateLearningResourceType(resourceType.getName())));
        }
        return resources;
    }

    private JsonArray createAbout(Material material) {
        JsonArray aboutNotes = new JsonArray();
        List<TaxonPositionDTO> taxonsPositions = findTaxonPositionDtoForTypes(material, newArrayList(SUBTOPIC));
        if (taxonsPositions.isEmpty()) taxonsPositions = findTaxonPositionDtoForTypes(material, newArrayList(TOPIC));

        if (isNotEmpty(taxonsPositions)) {
            taxonsPositions.stream().
                    map(taxon -> taxon.getTaxonLevelName().
                            replaceAll(REGEX_UNDERSCORE, SPACE_STRING)).
                    forEach(aboutNotes::add);
        }

        if (isNotEmpty(material.getTaxons())) {
            material.getTaxons().stream().
                    filter(taxon -> taxon.getTaxonLevel().equalsIgnoreCase(SUBTOPIC)).
                    map(taxon -> taxon.getName().replaceAll(REGEX_UNDERSCORE, SPACE_STRING)).
                    forEach(aboutNotes::add);
        }
        return aboutNotes;
    }

    private JsonArray createEducationalAlignment(Material material) {
        JsonArray alignmentObjects = new JsonArray();

        if (createEducationalSubject(material, newArrayList(SUBTOPIC), EDUCATIONAL_SUBJECT) != null) {
            alignmentObjects.add(createEducationalSubject(material, newArrayList(SUBTOPIC), EDUCATIONAL_SUBJECT));
        }
        if (createEducationalSubject(material, newArrayList(TOPIC), EDUCATIONAL_SUBJECT) != null) {
            alignmentObjects.add(createEducationalSubject(material, newArrayList(TOPIC), EDUCATIONAL_SUBJECT));
        }
        if (createEducationalSubject(material, newArrayList(DOMAIN), EDUCATIONAL_SUBJECT_AREA) != null) {
            alignmentObjects.add(createEducationalSubject(material, newArrayList(DOMAIN), EDUCATIONAL_SUBJECT_AREA));
        }
        JsonArray educationalLevels = createSchoolLevelAlignment(material);
        educationalLevels.forEach(alignmentObjects::add);

        return alignmentObjects;
    }

    private JsonObject createEducationalSubject(Material material, List<String> taxonLevels, String alignmentType) {
        JsonObject taxonLevelAlignment = new JsonObject();
        Set<String> finalUniqueTaxonStrings = new HashSet<>();

        if (isNotEmpty(material.getTaxons())) {
            taxonLevels.forEach(taxonLevel -> material.getTaxons().stream().
                    filter(taxon -> taxon.getTaxonLevel().equalsIgnoreCase(taxonLevel)).
                    map(taxon -> taxon.getName()).
                    forEach(finalUniqueTaxonStrings::add));
        }

        List<TaxonPositionDTO> foundTaxons = findTaxonPositionDtoForTypes(material, taxonLevels);

        Set<String> taxonsSet = foundTaxons.stream().
                map(TaxonPositionDTO::getTaxonLevelName).
                distinct().
                map(levelName -> levelName.replaceAll(SPACE_STRING, REGEX_UNDERSCORE)).
                collect(Collectors.toSet());

        finalUniqueTaxonStrings.addAll(taxonsSet);

        String targetUrl = join(",", finalUniqueTaxonStrings);

        if (targetUrl == null || targetUrl.equals(SPACE_STRING) || targetUrl.equals(EMPTY_STRING)) {
            return null;
        }

        taxonLevelAlignment.addProperty(TYPE, ALIGNMENT_OBJECT);
        taxonLevelAlignment.addProperty(ALIGNMENT_TYPE, alignmentType);
        taxonLevelAlignment.addProperty(EDUCATIONAL_FRAMEWORK, ESTONIAN_NATIONAL_CURRICULUM);
        taxonLevelAlignment.addProperty(TARGET_NAME, StringUtils.capitalize(targetUrl.replaceAll("_", SPACE_STRING)));
        taxonLevelAlignment.addProperty(TARGET_URL, OPPEKAVA_EDU_EE_A + targetUrl);

        return taxonLevelAlignment;
    }

    private JsonArray createSchoolLevelAlignment(Material material) {
        List<String> classValues = new ArrayList<>();
        JsonArray educationalLevels = new JsonArray();
        List<String> realGradesGroups = findIsItAllGroupOrSingle(material);

        if (isNotEmpty(realGradesGroups)) {
            realGradesGroups.forEach(gradeGroup -> classValues.add(gradeGroup.startsWith(GR) ? translateClassesLimit(gradeGroup) : translateGradeLimits(gradeGroup)));
            if (isNotEmpty(classValues)) {
                classValues.forEach(classLevel -> {
                    JsonObject educationalLevel = new JsonObject();
                    educationalLevel.addProperty(TYPE_, ALIGNMENT_OBJECT);
                    educationalLevel.addProperty(ALIGNMENT_TYPE, EDUCATIONAL_LEVEL);
                    educationalLevel.addProperty(EDUCATIONAL_FRAMEWORK, ESTONIAN_NATIONAL_CURRICULUM);
                    educationalLevel.addProperty(TARGET_NAME, classLevel.replaceAll("_", SPACE_STRING));
                    educationalLevel.addProperty(TARGET_URL, OPPEKAVA_EDU_EE_A + classLevel);
                    educationalLevels.add(educationalLevel);
                });
            }
        }
        return educationalLevels;
    }

    private List<String> findIsItAllGroupOrSingle(Material material) {
        if (isNotEmpty(material.getTargetGroups())) {
            List<String> realGradesGroups = material.getTargetGroups().stream().
                    map(TargetGroup::getName).
                    collect(toList());

            definedGradeGroups.forEach((key, values) -> {
                if (realGradesGroups.containsAll(values)) {
                    realGradesGroups.add(key);
                    ArrayList<String> valuesToRemove = definedGradeGroups.get(key);
                    valuesToRemove.forEach(realGradesGroups::remove);
                }
            });
            return realGradesGroups;
        }
        return emptyList();
    }


    private JsonArray createAuthors(Material material) {
        JsonObject jsonAuthor = new JsonObject();
        JsonArray authors = new JsonArray();
        if (isNotEmpty(material.getAuthors())) {
            material.getAuthors().forEach(author -> {
                String name = author.getName() != null ? author.getName() : NA;
                String familyName = author.getSurname() != null ? author.getSurname() : NA;

                jsonAuthor.addProperty(TYPE, PERSON);
                jsonAuthor.addProperty(GIVEN_NAME, name);
                jsonAuthor.addProperty(FAMILY_NAME, familyName);
                jsonAuthor.addProperty(NAME, format(NAME_FORMAT, name, familyName));
                authors.add(jsonAuthor);
            });
        }
        return authors;
    }

    private JsonArray createAudience(Material material) {
        JsonArray audiences = new JsonArray();
        if (isNotEmpty(material.getTaxonPositionDto())) {
            findAndTranslateAudience(material, audiences);
        }
        return audiences;
    }

    private String createThumbnailUrl(Material material) {
        try {
            return material.getPicture() != null && material.getPicture().getName() != null ? configuration.getString(SERVER_ADDRESS) + REST_PICTURE_THUMBNAIL_LG + material.getPicture().getName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void findAndTranslateAudience(Material material, JsonArray audiences) {
        if (isNotEmpty(material.getTaxonPositionDto())) {
            material.getTaxonPositionDto().stream().
                    filter(taxonPosition -> taxonPosition.getTaxonLevel().equalsIgnoreCase(EDUCATIONAL_CONTEXT)).
                    map(taxonPosition -> translateEducationalContext(taxonPosition.getTaxonLevelName())).
                    distinct().
                    forEach(audiences::add);
        }
    }

    private JsonArray createTypicalAgeRange(Material material) {
        JsonArray ageValues = new JsonArray();
        List<String> realGradesGroups = findIsItAllGroupOrSingle(material);

        if (isNotEmpty(realGradesGroups)) {
            realGradesGroups.forEach(gradeGroup -> ageValues.add(gradeGroup.startsWith(L) || gradeGroup.startsWith(P) ? generalGradeLimits(gradeGroup) : typicalAgeRanges(gradeGroup)));
        }
        return ageValues;
    }

    private Material findAndSetTaxonPosition(long materialId) {
        Material material = materialDao.findById(materialId);
        learningObjectService.setTaxonPosition(material);
        return material;
    }

    private List<TaxonPositionDTO> findTaxonPositionDtoForTypes(Material material, List<String> taxonType) {
        List<TaxonPositionDTO> foundTaxons = new ArrayList<>();
        if (isNotEmpty(material.getTaxonPositionDto())) {
            material.getTaxonPositionDto().
                    forEach(taxonPosition -> taxonType.stream().
                            filter(type -> taxonPosition.getTaxonLevel().equalsIgnoreCase(type)).
                            map(type -> taxonPosition).
                            forEach(foundTaxons::add));

            return foundTaxons;
        } else
            return emptyList();
    }

    private boolean showMaterialLdJson(Material material) {
        return material != null && material.getVisibility() != null && (material.getVisibility() != Visibility.NOT_LISTED && material.getVisibility() != Visibility.PRIVATE);
    }
}
