package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.EducationalContextC;
import ee.hm.dop.model.enums.ReportingReasonEnum;
import ee.hm.dop.model.enums.TargetGroupEnum;
import ee.hm.dop.model.taxon.*;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class LearningMaterialMetadataResourceTest extends ResourceIntegrationTestBase {

    public static final String BASE = "learningMaterialMetadata/";
    private static final String GET_EDUCATIONAL_CONTEXT_URL = BASE + "educationalContext";
    private static final String GET_TAXON_URL = BASE + "taxon?taxonId=%s";
    private static final String GET_LANGUAGES_URL = BASE + "language";
    private static final String GET_USED_LANGUAGES_URL = BASE + "usedLanguages";
    private static final String GET_TARGET_GROUPS_URL = BASE + "targetGroup";
    private static final String GET_RESOURCE_TYPES_URL = BASE + "resourceType";
    private static final String GET_USED_RESOURCE_TYPES_URL = BASE + "resourceType/used";
    private static final String GET_LICENSE_TYPES_URL = BASE + "licenseType";
    private static final String GET_CROSS_CURRICULAR_THEMES_URL = BASE + "crossCurricularTheme";
    private static final String GET_KEY_COMPETENCES_URL = BASE + "keyCompetence";
    private static final String GET_LO_REPORTING_REASONS_URL = BASE + "learningObjectReportingReasons";
    private static final String GET_TAG_REPORTING_REASONS_URL = BASE + "commentReportingReasons";
    private static final String GET_COMMENT_REPORTING_REASONS_URL = BASE + "tagReportingReasons";
    public static final String MATHEMATICS = "Mathematics";
    public static final String ALGEBRA = "Algebra";
    public static final String TRIGONOMETRIA = "Trigonometria";

    @Test
    public void getEducationalContext() {
        List<EducationalContext> educationalContexts = doGet(GET_EDUCATIONAL_CONTEXT_URL,
                new GenericType<List<EducationalContext>>() {
                });

        assertEquals(9, educationalContexts.stream().distinct().count());

        int domains = 0, subjects = 0;

        for (EducationalContext educationalContext : educationalContexts) {
            if (educationalContext.getName().equals(EducationalContextC.PRESCHOOLEDUCATION)) {
                for (Domain domain : educationalContext.getDomains()) {
                    domains++;
                    if (domain.getName().equals(MATHEMATICS)) {
                        for (Subject subject : domain.getSubjects()) {
                            subjects++;
                            if (subject.getName().equals(MATHEMATICS)) {
                                assertEquals(2, subject.getTopics().size());
                                Topic[] topics = new Topic[2];
                                subject.getTopics().toArray(topics);
                                assertTrue(topics[0].getName().equals(ALGEBRA)
                                        || topics[0].getName().equals(TRIGONOMETRIA));
                                assertTrue(topics[1].getName().equals(ALGEBRA)
                                        || topics[1].getName().equals(TRIGONOMETRIA));
                            }
                        }
                    }
                }
            }
        }

        assertEquals(2, domains);
        assertEquals(2, subjects);
    }

    @Test
    public void getTaxon() {
        Domain taxon = (Domain) doGet(String.format(GET_TAXON_URL, TAXON_MATHEMATICS_DOMAIN.id), Taxon.class);
        assertEquals(TAXON_MATHEMATICS_DOMAIN.id, taxon.getId());
        assertEquals(TAXON_MATHEMATICS_DOMAIN.name, taxon.getName());
    }

    @Test
    public void getAllLanguages() {
        List<String> expectedNames = Arrays.asList("Estonian", "Russian", "English", "Arabic", "Portuguese", "French");
        verifyGetLanguages(expectedNames, GET_LANGUAGES_URL);
    }

    @Test
    public void getUsedLanguages_returns_languages_used_in_materials() throws Exception {
        List<String> expectedNames = Arrays.asList("Estonian", "Russian", "English", "Arabic", "Portuguese");
        verifyGetLanguages(expectedNames, GET_USED_LANGUAGES_URL);
    }

    @Test
    public void getTargetGroups() {
        List<TargetGroup> result = doGet(GET_TARGET_GROUPS_URL, new GenericType<List<TargetGroup>>() {
        });

        assertEquals(12, result.size());

        checkIfAllTargetGroups(result);
    }

    @Test
    public void getUsedResourceTypes() {
        List<ResourceType> result = doGet(GET_USED_RESOURCE_TYPES_URL, new GenericType<List<ResourceType>>() {
        });

        assertEquals(5, result.size());
    }

    @Test
    public void getResourceTypesGroups() {
        List<ResourceType> result = doGet(GET_RESOURCE_TYPES_URL, new GenericType<List<ResourceType>>() {
        });

        assertEquals(7, result.size());

        List<String> expected = Arrays.asList("TEXTBOOK1", "EXPERIMENT1", "COURSE");
        List<String> actual = result.stream().map(ResourceType::getName).collect(Collectors.toList());

        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void getAllLicenseTypes() {
        List<LicenseType> licenseTypes = doGet(GET_LICENSE_TYPES_URL, new GenericType<List<LicenseType>>() {
        });

        assertEquals(3, licenseTypes.size());
        licenseTypes.forEach(this::assertValidLicenseType);
    }

    @Test
    public void getCrossCurricularThemes() {
        List<CrossCurricularTheme> result = doGet(GET_CROSS_CURRICULAR_THEMES_URL,
                new GenericType<List<CrossCurricularTheme>>() {
                });

        assertEquals(2, result.size());

        List<String> expected = Arrays.asList("Lifelong_learning_and_career_planning",
                "Environment_and_sustainable_development");
        List<String> actual = result.stream().map(CrossCurricularTheme::getName).collect(Collectors.toList());

        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void getKeyCompetences() {
        List<KeyCompetence> result = doGet(GET_KEY_COMPETENCES_URL, new GenericType<List<KeyCompetence>>() {
        });

        assertEquals(2, result.size());

        List<String> expected = Arrays.asList("Cultural_and_value_competence", "Social_and_citizenship_competence");
        List<String> actual = result.stream().map(KeyCompetence::getName).collect(Collectors.toList());

        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void learningObject_reportingreasons_returns_LO_reasons() throws Exception {
        List<ReportingReasonEnum> actual = doGet(GET_LO_REPORTING_REASONS_URL, new GenericType<List<ReportingReasonEnum>>() {
        });
        List<ReportingReasonEnum> expected = ReportingReasonEnum.learningObjectReportingReasonsModal();
        assertEquals(expected, actual);
    }

    @Test
    public void comment_reportingreasons_returns_comment_reasons() throws Exception {
        List<ReportingReasonEnum> actual = doGet(GET_COMMENT_REPORTING_REASONS_URL, new GenericType<List<ReportingReasonEnum>>() {
        });
        List<ReportingReasonEnum> expected = ReportingReasonEnum.commentReportingReasons();
        assertEquals(expected, actual);
    }

    @Test
    public void tag_reportingreasons_returns_tag_reasons() throws Exception {
        List<ReportingReasonEnum> actual = doGet(GET_TAG_REPORTING_REASONS_URL, new GenericType<List<ReportingReasonEnum>>() {
        });
        List<ReportingReasonEnum> expected = ReportingReasonEnum.tagReportingReasons();
        assertEquals(expected, actual);
    }

    private void verifyGetLanguages(List<String> expectedNames, String getUsedLanguagesUrl) {
        List<Language> languages = doGet(getUsedLanguagesUrl, new GenericType<List<Language>>() {
        });

        assertEquals(expectedNames.size(), languages.stream().distinct().count());

        List<String> actualNames = languages.stream().map(Language::getName).collect(Collectors.toList());
        assertTrue(actualNames.containsAll(expectedNames));
    }

    private void checkIfAllTargetGroups(List<TargetGroup> targetGroups) {
        if (targetGroups.size() != TargetGroupEnum.values().length) {
            fail();
        }

        for (TargetGroup targetGroup : targetGroups) {
            TargetGroupEnum.valueOf(targetGroup.getName());
        }

    }

    private void assertValidLicenseType(LicenseType licenseType) {
        Map<Long, String> licenseTypes = new HashMap<>();
        licenseTypes.put(1L, "CCBY");
        licenseTypes.put(2L, "CCBYSA");
        licenseTypes.put(3L, "CCBYND");

        assertNotNull(licenseType.getId());
        assertNotNull(licenseType.getName());
        if (licenseTypes.containsKey(licenseType.getId())) {
            assertEquals(licenseTypes.get(licenseType.getId()), licenseType.getName());
        } else {
            fail("LicenseType with unexpected id.");
        }
    }

}
