package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.GenericType;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Domain;
import ee.hm.dop.model.EducationalContext;

public class LearningMaterialMetadataResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_EDUCATIONAL_CONTEXT_URL = "learningMaterialMetadata/educationalContext";
    private static final String GET_DOMAIN_BY_EDUCATIONAL_CONTEXT_URL = "learningMaterialMetadata/domain?educationalContext=%s";

    @Test
    public void getEducationalContext() {
        List<EducationalContext> educationalContexts = doGet(GET_EDUCATIONAL_CONTEXT_URL,
                new GenericType<List<EducationalContext>>() {
                });

        assertEquals(9, educationalContexts.stream().distinct().count());
    }

    @Test
    public void getAllDomainsByEducationalContext() {
        String educationalContext = "preschooleducation";
        List<Domain> domains = doGet(format(GET_DOMAIN_BY_EDUCATIONAL_CONTEXT_URL, educationalContext),
                new GenericType<List<Domain>>() {
                });

        assertEquals(2, domains.stream().distinct().count());

        List<String> domainNames = domains.stream().map(d -> d.getName()).collect(Collectors.toList());
        assertTrue(domainNames.contains("Mathematics"));
        assertTrue(domainNames.contains("ForeignLanguage"));
    }
}
