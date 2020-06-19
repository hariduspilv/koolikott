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
import ee.hm.dop.model.taxon.TaxonLevel;
import ee.hm.dop.model.taxon.TaxonPositionDTO;
import ee.hm.dop.service.content.LearningObjectService;
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
    public static final String _TYPE = "type";
    public static final String DOMAIN = "DOMAIN_";
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

        if (showMaterialLdJson(material)) {
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(createCreativeWork(material));
            jsonArray.add(createWebSite());
            jsonArray.add(createOrganisation());
            if (isNotEmpty(material.getTaxonPositionDto())) jsonArray.add(createBreadCrumbList(material));
            if (isNotEmpty(material.getPeerReviews())) jsonArray.add(createPeerReview());

            return jsonArray.toString();
        }
        return "";
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

        itemList_1.addProperty(TYPE, LIST_ITEM);
        itemList_1.addProperty(POSITION, 1);
        itemList_1.addProperty(NAME, listItem_1);
        itemList_1.addProperty(ITEM, "https://e-koolikott.ee/search/result/?taxon=" + material.getTaxonPositionDto().get(0).getTaxonLevelId());
        itemListElement.add(itemList_1);

        JsonObject itemList_2 = new JsonObject();
        String listItem_2 = translationGroupDao.getTranslationByKeyAndLangcode(DOMAIN + (material.getTaxonPositionDto().get(1).getTaxonLevelName()).toUpperCase(), languageDao.findByCode(material.getLanguage().getCode()).getId());

        itemList_2.addProperty(TYPE, LIST_ITEM);
        itemList_2.addProperty(POSITION, 2);
        itemList_2.addProperty(NAME, listItem_2);
        itemList_2.addProperty(ITEM, "https://e-koolikott.ee/search/result/?taxon=" + material.getTaxonPositionDto().get(1).getTaxonLevelId());
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

        JsonObject jsonMaterial = new JsonObject();

        jsonMaterial.addProperty("@id", material.getSource());
        jsonMaterial.addProperty(CONTEXT, HTTP_SCHEMA_ORG);
        jsonMaterial.addProperty(TYPE, CREATIVE_WORK);
        jsonMaterial.add("learningResourceType", createResourceType(material));
        jsonMaterial.add("about", createAbout(material));
        jsonMaterial.addProperty("dateCreated", createDateCreated(material));
        jsonMaterial.addProperty("datePublished", material.getIssueDate() != null ? convertIssueDateToLocalDate(material.getIssueDate()).toString() : NA);
        jsonMaterial.addProperty("thumbnailUrl", createThumbnailUrl(material));
        jsonMaterial.addProperty("license", createLicence(material));
        jsonMaterial.add("typicalAgeRange", createTypicalAgeRange(material));
        jsonMaterial.addProperty("interactionCount", material.getViews() + USER_PAGE_VISITS);
        jsonMaterial.add("headline", createHeadlines(material));
        jsonMaterial.add("keywords", createKeywords(material));
        jsonMaterial.add("abstract", createAbstract(material));
        jsonMaterial.addProperty("inLanguage", convertLanguages(material));
        jsonMaterial.add("author", createAuthors(material));
        jsonMaterial.add("publisher", createPublisher(material));
        jsonMaterial.add("sdPublisher", createSdPublisher());
        jsonMaterial.add("audience", createAudiences(material));
        jsonMaterial.add("educationalAlignment", createEducationalAlignment(material));

        return jsonMaterial;
    }

    private String createLicence(Material material) {
        return findLicenseType(material.getLicenseType().getName());
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

    private JsonArray createAbstract(Material material) {
        JsonArray descriptions = new JsonArray();
        if (isNotEmpty(material.getDescriptions())) {
            material.getDescriptions().forEach(description -> {
                String descriptionWithoutTags = description.getText().replaceAll(REGEX_TAG, "");
                descriptions.add(descriptionWithoutTags);
            });
        }
        return descriptions;
    }

    private JsonArray createHeadlines(Material material) {
        JsonArray titles = new JsonArray();
        if (isNotEmpty(material.getTitles())) {
            material.getTitles().forEach(title -> titles.add(title.getText()));
        }
        return titles;
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
            material.getResourceTypes().forEach(resourceType -> {
                resources.add(translateLearningResourceType(resourceType.getName()));
            });
        }
        return resources;
    }

    private JsonArray createAbout(Material material) {
        JsonArray aboutNotes = new JsonArray();
        List<TaxonPositionDTO> taxonsPositions = findTaxonPositionDtoForTypes(material, newArrayList(SUBTOPIC));
        if (taxonsPositions.isEmpty()) taxonsPositions = findTaxonPositionDtoForTypes(material, newArrayList(TOPIC));

        taxonsPositions.stream().
                map(taxon -> taxon.getTaxonLevelName().
                        replaceAll(REGEX_UNDERSCORE, " ")).
                forEach(aboutNotes::add);

        if (isNotEmpty(material.getTaxons())) {
            material.getTaxons().stream().
                    filter(taxon -> taxon.getTaxonLevel().equalsIgnoreCase(SUBTOPIC)).
                    map(taxon -> taxon.getName().replaceAll(REGEX_UNDERSCORE, " ")).
                    forEach(aboutNotes::add);
        }
        return aboutNotes;
    }

    private JsonArray createEducationalAlignment(Material material) {
        JsonArray alignmentObjects = new JsonArray();
        alignmentObjects.add(createEducationalSubject(material, newArrayList(SUBTOPIC), EDUCATIONAL_SUBJECT));
        alignmentObjects.add(createEducationalSubject(material, newArrayList(TOPIC), EDUCATIONAL_SUBJECT));
        alignmentObjects.add(createEducationalSubject(material, newArrayList(TaxonLevel.DOMAIN), EDUCATIONAL_SUBJECT_AREA));

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
                    map(taxon -> taxon.getName().replaceAll(" ", REGEX_UNDERSCORE)).
                    forEach(finalUniqueTaxonStrings::add));
        }

        List<TaxonPositionDTO> foundTaxons = findTaxonPositionDtoForTypes(material, taxonLevels);

        Set<String> taxonsSet = foundTaxons.stream().
                map(TaxonPositionDTO::getTaxonLevelName).
                distinct().
                map(levelName -> levelName.replaceAll(" ", REGEX_UNDERSCORE)).
                collect(Collectors.toSet());

        finalUniqueTaxonStrings.addAll(taxonsSet);

        String targetUrl = join(",", finalUniqueTaxonStrings);

        taxonLevelAlignment.addProperty(TYPE, ALIGNMENT_OBJECT);
        taxonLevelAlignment.addProperty(ALIGNMENT_TYPE, alignmentType);
        taxonLevelAlignment.addProperty(EDUCATIONAL_FRAMEWORK, ESTONIAN_NATIONAL_CURRICULUM);
        taxonLevelAlignment.addProperty(TARGET_NAME, targetUrl);
        taxonLevelAlignment.addProperty(TARGET_URL, OPPEKAVA_EDU_EE_A + targetUrl);

        return taxonLevelAlignment;
    }

    private JsonArray createSchoolLevelAlignment(Material material) {
        List<String> classValues = new ArrayList<>();
        JsonArray educationalLevels = new JsonArray();
        List<String> realGradesGroups = findIsItAllGroupOrSingle(material);

        realGradesGroups.forEach(gradeGroup -> classValues.add(gradeGroup.startsWith(GR) ? translateClassesLimit(gradeGroup) : translateGradeLimits(gradeGroup)));

        classValues.forEach(classLevel -> {
            JsonObject educationalLevel = new JsonObject();
            educationalLevel.addProperty(_TYPE, ALIGNMENT_OBJECT);
            educationalLevel.addProperty(ALIGNMENT_TYPE, EDUCATIONAL_LEVEL);
            educationalLevel.addProperty(EDUCATIONAL_FRAMEWORK, ESTONIAN_NATIONAL_CURRICULUM);
            educationalLevel.addProperty(TARGET_NAME, classLevel);
            educationalLevel.addProperty(TARGET_URL, OPPEKAVA_EDU_EE_A + classLevel);
            educationalLevels.add(educationalLevel);
        });
        return educationalLevels;
    }

    private List<String> findIsItAllGroupOrSingle(Material material) {
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

    private JsonArray createAuthors(Material material) {
        JsonObject jsonAuthor = new JsonObject();
        JsonArray authors = new JsonArray();
        material.getAuthors().forEach(author -> {
            jsonAuthor.addProperty(TYPE, PERSON);
            jsonAuthor.addProperty(GIVEN_NAME, author.getName());
            jsonAuthor.addProperty(FAMILY_NAME, author.getSurname());
            jsonAuthor.addProperty(NAME, format(NAME_FORMAT, author.getName(), author.getSurname()));
            authors.add(jsonAuthor);
        });
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
        return material.getPicture() != null ? configuration.getString(SERVER_ADDRESS) + REST_PICTURE_THUMBNAIL_LG + material.getPicture().getName() : "";
    }

    private void findAndTranslateAudience(Material material, JsonArray audiences) {
        material.getTaxonPositionDto().stream().
                filter(taxonPosition -> taxonPosition.getTaxonLevel().equalsIgnoreCase(EDUCATIONAL_CONTEXT)).
                map(taxonPosition -> translateEducationalContext(taxonPosition.getTaxonLevelName())).
                distinct().
                forEach(audiences::add);
    }

    private JsonArray createTypicalAgeRange(Material material) {
        JsonArray ageValues = new JsonArray();
        List<String> realGradesGroups = findIsItAllGroupOrSingle(material);

        realGradesGroups.forEach(gradeGroup -> ageValues.add(gradeGroup.startsWith(L) || gradeGroup.startsWith(P) ? generalGradeLimits(gradeGroup) : typicalAgeRanges(gradeGroup)));
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
        return material != null && (material.getVisibility() != Visibility.NOT_LISTED && material.getVisibility() != Visibility.PRIVATE);
    }
}
