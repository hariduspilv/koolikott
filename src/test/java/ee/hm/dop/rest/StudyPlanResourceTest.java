package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.joda.time.DateTime;
import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.StudyPlan;

public class StudyPlanResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_STUDY_PLAN_URL = "studyPlan?id=%s";

    @Test
    public void getStudyPlan() {
        StudyPlan studyPlan = doGet(format(GET_STUDY_PLAN_URL, 1), StudyPlan.class);

        assertNotNull(studyPlan);
        assertEquals(new Long(1), studyPlan.getId());
        assertEquals("The new stock market", studyPlan.getTitle());
        assertEquals(new Long(2), studyPlan.getSubject().getId());
        assertEquals(new DateTime("2000-12-29T08:00:01.000+02:00"), studyPlan.getCreated());
    }
}
