package ee.hm.dop.rest.administration;

import com.google.common.collect.Lists;
import ee.hm.dop.common.test.TestTaxon;
import ee.hm.dop.common.test.TestUser;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReportingReasonEnum;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.model.taxon.Taxon;

import static org.junit.Assert.*;

public class ReviewableChangeAdminResourceTestUtil {


    public static void assertDoesntHave(Material material, TestTaxon... testTaxon) {
        for (TestTaxon taxon : testTaxon) {
            assertDoesntHave(material, taxon);
        }
    }

    public static void assertDoesntHave(Material material, TestTaxon testTaxon) {
        assertFalse(material.getTags().stream().map(Tag::getName).anyMatch(t -> t.equals(testTaxon.name)));
        assertFalse(material.getTaxons().stream().map(Taxon::getId).anyMatch(t -> t.equals(testTaxon.id)));
        assertTrue(material.getChanged() == 0);
    }

    public static void assertNotChanged(Material material, String source) {
        assertEquals(source, material.getSource());
        assertTrue(material.getChanged() == 0);
    }

    public static void assertChanged(Material material, String source) {
        assertEquals(source, material.getSource());
        assertFalse(material.getChanged() == 0);
    }
//    related to system tag deletion
//    public static void assertHas(Material material, TestTaxon... testTaxon) {
//        for (TestTaxon taxon : testTaxon) {
//            assertHas(material, taxon);
//        }
//    }
//
//    public static void assertHas(Material material, TestTaxon testTaxon) {
//        assertTrue(material.getTags().stream().map(Tag::getName).anyMatch(t -> t.equals(testTaxon.name)));
//        assertTrue(material.getTaxons().stream().map(Taxon::getId).anyMatch(t -> t.equals(testTaxon.id)));
//        assertFalse(material.getChanged() == 0);
//    }

    public static void assertHasChangesDontMatter(Material material, TestTaxon testTaxon) {
        assertTrue(material.getTags().stream().map(Tag::getName).anyMatch(t -> t.equals(testTaxon.name)));
        assertTrue(material.getTaxons().stream().map(Taxon::getId).anyMatch(t -> t.equals(testTaxon.id)));
    }

    public static void assertHasTaxonNotTag(Material material, TestTaxon testTaxon) {
        assertFalse(material.getTags().stream().map(Tag::getName).anyMatch(t -> t.equals(testTaxon.name)));
        assertTrue(material.getTaxons().stream().map(Taxon::getId).anyMatch(t -> t.equals(testTaxon.id)));
        assertFalse(material.getChanged() == 0);
    }

    public static void assertHasNoTaxonNoTag(Material material, TestTaxon testTaxon) {
        assertFalse(material.getTags().stream().map(Tag::getName).anyMatch(t -> t.equals(testTaxon.name)));
        assertFalse(material.getTaxons().stream().map(Taxon::getId).anyMatch(t -> t.equals(testTaxon.id)));
        assertTrue(material.getChanged() == 0);
    }

    public static void assertHasTagNotTaxon(Material material, TestTaxon testTaxon) {
        assertTrue(material.getTags().stream().map(Tag::getName).anyMatch(t -> t.equals(testTaxon.name)));
        assertFalse(material.getTaxons().stream().map(Taxon::getId).anyMatch(t -> t.equals(testTaxon.id)));
        assertTrue(material.getChanged() == 0);
    }

    public static void assertHasNoTagsNoTaxonsChangesAre1(Material material, TestTaxon testTaxon) {
        assertFalse(material.getTags().stream().map(Tag::getName).anyMatch(t -> t.equals(testTaxon.name)));
        assertFalse(material.getTaxons().stream().map(Taxon::getId).anyMatch(t -> t.equals(testTaxon.id)));
        assertTrue(material.getChanged() == 1);
    }

    public static void assertDoesntHave(Material material) {
        assertTrue(material.getImproper() == 0);
        assertTrue(material.getUnReviewed() == 0);
        assertTrue(material.getChanged() == 0);
    }

    public static void assertHas(Material material, ReviewType reviewType) {
        if (reviewType == ReviewType.IMPROPER) {
            assertFalse(material.getImproper() == 0);
        }
        if (reviewType == ReviewType.FIRST) {
            assertFalse(material.getUnReviewed() == 0);
        }
        assertTrue(material.getChanged() == 0);
    }

    public static void assertIsReviewed(ReviewableChange review, TestUser testUser) {
        assertTrue(review.isReviewed());
        assertNotNull(review.getReviewedAt());
        assertEquals(testUser.id, review.getReviewedBy().getId());
    }

    public static ImproperContentDto improper(Material material) {
        ImproperContentDto json = new ImproperContentDto();
        ReportingReason reason = new ReportingReason();
        reason.setReason(ReportingReasonEnum.LO_CONTENT);
        json.setLearningObject(material);
        json.setReportingReasons(Lists.newArrayList(reason));
        return json;
    }

    private static class ImproperContentDto extends ImproperContent{
        private LearningObject learningObject;

        @Override
        public LearningObject getLearningObject() {
            return learningObject;
        }

        @Override
        public void setLearningObject(LearningObject learningObject) {
            this.learningObject = learningObject;
        }
    }
}
