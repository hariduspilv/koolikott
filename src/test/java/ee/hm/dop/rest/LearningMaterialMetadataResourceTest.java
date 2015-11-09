package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.core.GenericType;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.EducationalContext;

public class LearningMaterialMetadataResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_EDUCATIONAL_CONTEXT_URL = "learningMaterialMetadata/educationalContext";

    @Test
    public void getEducationalContext() {
        List<EducationalContext> educationalContexts = doGet(GET_EDUCATIONAL_CONTEXT_URL,
                new GenericType<List<EducationalContext>>() {
                });

        assertEquals(9, educationalContexts.stream().distinct().count());
    }
}
