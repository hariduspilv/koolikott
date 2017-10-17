package ee.hm.dop.utils;

import ee.hm.dop.model.taxon.EducationalContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static ee.hm.dop.service.solr.SearchServiceTestUtil.*;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TaxonUtilsTest {

    @Test
    public void educ_cont() throws Exception {
        EducationalContext educationalContext = educationalContext();
        EducationalContext result = TaxonUtils.getEducationalContext(educationalContext);
        validate(educationalContext, result);
    }

    @Test
    public void educ_cont_domain() throws Exception {
        EducationalContext educationalContext = educationalContext();
        EducationalContext result = TaxonUtils.getEducationalContext(domain(educationalContext));
        validate(educationalContext, result);
    }

    @Test
    public void educ_cont_domain_subject() throws Exception {
        EducationalContext educationalContext = educationalContext();
        EducationalContext result = TaxonUtils.getEducationalContext(subject(domain(educationalContext)));
        validate(educationalContext, result);
    }

    @Test
    public void educ_cont_domain_subject_topic() throws Exception {
        EducationalContext educationalContext = educationalContext();
        EducationalContext result = TaxonUtils.getEducationalContext(topic(subject(domain(educationalContext))));
        validate(educationalContext, result);
    }

    @Test
    public void educ_cont_domain_subject_topic_subtopic() throws Exception {
        EducationalContext educationalContext = educationalContext();
        EducationalContext result = TaxonUtils.getEducationalContext(subTopic(topic(subject(domain(educationalContext)))));
        validate(educationalContext, result);
    }

    @Test
    public void educ_cont_domain_topic() throws Exception {
        EducationalContext educationalContext = educationalContext();
        EducationalContext result = TaxonUtils.getEducationalContext(topic(domain(educationalContext)));
        validate(educationalContext, result);
    }

    @Test
    public void educ_cont_domain_topic_subtopic() throws Exception {
        EducationalContext educationalContext = educationalContext();
        EducationalContext result = TaxonUtils.getEducationalContext(subTopic(topic(domain(educationalContext))));
        validate(educationalContext, result);
    }

    @Test
    public void educ_cont_domain_specialization() throws Exception {
        EducationalContext educationalContext = educationalContext();
        EducationalContext result = TaxonUtils.getEducationalContext(specialization(domain(educationalContext)));
        validate(educationalContext, result);
    }

    @Test
    public void educ_cont_domain_specialization_module() throws Exception {
        EducationalContext educationalContext = educationalContext();
        EducationalContext result = TaxonUtils.getEducationalContext(module(specialization(domain(educationalContext))));
        validate(educationalContext, result);
    }

    @Test
    public void educ_cont_domain_specialization_module_topic() throws Exception {
        EducationalContext educationalContext = educationalContext();
        EducationalContext result = TaxonUtils.getEducationalContext(topic(module(specialization(domain(educationalContext)))));
        validate(educationalContext, result);
    }

    @Test
    public void educ_cont_domain_specialization_module_topic_subtopic() throws Exception {
        EducationalContext educationalContext = educationalContext();
        EducationalContext result = TaxonUtils.getEducationalContext(subTopic(topic(module(specialization(domain(educationalContext))))));
        validate(educationalContext, result);
    }

    private void validate(EducationalContext educationalContext, EducationalContext result) {
        assertEquals(educationalContext.getName(), result.getName());
        assertEquals(educationalContext.getId(), result.getId());
        assertEquals(educationalContext, result);
    }
}
