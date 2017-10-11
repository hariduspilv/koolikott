package ee.hm.dop.common.test;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.enums.LanguageC;
import ee.hm.dop.model.enums.TargetGroupEnum;
import ee.hm.dop.model.taxon.Subject;
import org.joda.time.DateTime;

import static org.junit.Assert.*;

public class Material1Validation {

    public static void assertMaterial1(Material material, TestLayer layer) {
        assertEquals(2, material.getTitles().size());
        assertEquals("Matemaatika õpik üheksandale klassile", material.getTitles().get(0).getText());
        assertEquals(2, material.getDescriptions().size());
        assertEquals("Test description in estonian. (Russian available)", material.getDescriptions().get(0).getText());
        Language descriptionLanguage = material.getDescriptions().get(0).getLanguage();
        assertEquals(LanguageC.EST, descriptionLanguage.getCode());
        assertNotNull(descriptionLanguage.getName());
        assertNotNull(descriptionLanguage.getCodes());
        Language language = material.getLanguage();
        assertNotNull(language);
        assertEquals(LanguageC.EST, language.getCode());
        assertEquals("Estonian", language.getName());
        assertNotNull(language.getCodes());
        assertEquals(new Long(1), material.getPicture().getId());
        assertEquals("picture1", material.getPicture().getName());
        if (layer == TestLayer.REST) {
            assertNull(material.getPicture().getData());
        }
        assertNotNull(material.getTaxons());
        assertEquals(2, material.getTaxons().size());
        assertEquals(new Long(2), material.getTaxons().get(0).getId());
        assertEquals(new Long(20), material.getTaxons().get(1).getId());
        assertNotNull(material.getRepositoryIdentifier());
        assertEquals(new Long(1), material.getCreator().getId());
        assertFalse(material.isEmbeddable());

        assertEquals(2, material.getTargetGroups().size());
        assertTrue(TargetGroupEnum.containsTargetGroup(material.getTargetGroups(), TargetGroupEnum.ZERO_FIVE));
        assertTrue(TargetGroupEnum.containsTargetGroup(material.getTargetGroups(), TargetGroupEnum.SIX_SEVEN));
        assertTrue(material.isSpecialEducation());
        assertEquals("Lifelong_learning_and_career_planning", material.getCrossCurricularThemes().get(0).getName());
        assertEquals("Cultural_and_value_competence", material.getKeyCompetences().get(0).getName());

        assertEquals("CCBY", material.getLicenseType().getName());
        assertEquals("Koolibri", material.getPublishers().get(0).getName());
        assertEquals(new DateTime("1999-01-01T02:00:01.000+02:00"), material.getAdded());

        assertEquals(5, material.getTags().size());
        assertEquals("matemaatika", material.getTags().get(0).getName());
        assertEquals("põhikool", material.getTags().get(1).getName());
        assertEquals("õpik", material.getTags().get(2).getName());
        assertEquals("mathematics", material.getTags().get(3).getName());
        assertEquals("book", material.getTags().get(4).getName());

        assertEquals(2, material.getTitles().size());
        assertEquals("Matemaatika õpik üheksandale klassile", material.getTitles().get(0).getText());
        assertEquals(2, material.getDescriptions().size());
        assertEquals("Test description in estonian. (Russian available)", material.getDescriptions().get(0).getText());
        assertEquals(LanguageC.EST, descriptionLanguage.getCode());
        assertEquals("Estonian", descriptionLanguage.getName());
        assertNotNull(language);
        assertEquals(LanguageC.EST, language.getCode());
        assertEquals("Estonian", language.getName());
        assertEquals("et", language.getCodes().get(0));
        assertNotNull(material.getPicture());
        assertNotNull(material.getTaxons());
        assertEquals(2, material.getTaxons().size());
        assertEquals(new Long(2), material.getTaxons().get(0).getId());

        Subject biology = (Subject) material.getTaxons().get(1);
        assertEquals(new Long(20), biology.getId());
        assertEquals(2, biology.getDomain().getSubjects().size());
        assertEquals(2, biology.getDomain().getEducationalContext().getDomains().size());

        if (layer == TestLayer.DAO) {
            assertEquals(new Long(1), material.getRepository().getId());
            assertEquals("http://repo1.ee", material.getRepository().getBaseURL());
            assertEquals("isssiiaawej", material.getRepositoryIdentifier());
        } else {
            assertNull(material.getRepository());
        }
        assertEquals(new Long(1), material.getCreator().getId());
        assertFalse(material.isEmbeddable());

        assertEquals(2, material.getTargetGroups().size());
        assertTrue(TargetGroupEnum.containsTargetGroup(material.getTargetGroups(), TargetGroupEnum.ZERO_FIVE));
        assertTrue(TargetGroupEnum.containsTargetGroup(material.getTargetGroups(), TargetGroupEnum.SIX_SEVEN));
        assertTrue(material.isSpecialEducation());
        assertEquals("Lifelong_learning_and_career_planning", material.getCrossCurricularThemes().get(0).getName());
        assertEquals("Cultural_and_value_competence", material.getKeyCompetences().get(0).getName());

        Recommendation recommendation = material.getRecommendation();
        assertNotNull(recommendation);
        assertEquals(Long.valueOf(1L), recommendation.getId());
    }
}
