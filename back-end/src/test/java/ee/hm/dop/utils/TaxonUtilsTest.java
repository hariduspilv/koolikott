package ee.hm.dop.utils;

import ee.hm.dop.model.taxon.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static ee.hm.dop.service.solr.SearchServiceTestUtil.*;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TaxonUtilsTest {

    public static final EducationalContext EDUCATIONAL_CONTEXT = educationalContext();
    public static final Domain DOMAIN = domain(EDUCATIONAL_CONTEXT);
    public static final Subject SUBJECT = subject(DOMAIN);
    public static final Specialization SPECIALIZATION = specialization(DOMAIN);
    public static final Module MODULE = module(SPECIALIZATION);
    public static final Topic TOPIC_DOMAIN = topic(DOMAIN);
    public static final Topic TOPIC_SUBJECT = topic(SUBJECT);
    public static final Topic TOPIC_MODULE = topic(MODULE);
    public static final Subtopic SUBTOPIC_DOMAIN = subTopic(TOPIC_DOMAIN);
    public static final Subtopic SUBTOPIC_SUBJECT = subTopic(TOPIC_SUBJECT);
    public static final Subtopic SUBTOPIC_MODULE = subTopic(TOPIC_MODULE);

    //EDUCATIONAL CONTEXT PARENT

    @Test
    public void educ_cont_parent() throws Exception {
        validate(EDUCATIONAL_CONTEXT, TaxonUtils.getEducationalContext(EDUCATIONAL_CONTEXT));
    }

    @Test
    public void educ_cont_parent_domain() throws Exception {
        validate(EDUCATIONAL_CONTEXT, TaxonUtils.getEducationalContext(DOMAIN));
    }

    @Test
    public void educ_cont_parent_domain_subject() throws Exception {
        validate(EDUCATIONAL_CONTEXT, TaxonUtils.getEducationalContext(SUBJECT));
    }

    @Test
    public void educ_cont_parent_domain_subject_topic() throws Exception {
        validate(EDUCATIONAL_CONTEXT, TaxonUtils.getEducationalContext(TOPIC_SUBJECT));
    }

    @Test
    public void educ_cont_parent_domain_subject_topic_subtopic() throws Exception {
        validate(EDUCATIONAL_CONTEXT, TaxonUtils.getEducationalContext(SUBTOPIC_SUBJECT));
    }

    @Test
    public void educ_cont_parent_domain_topic() throws Exception {
        validate(EDUCATIONAL_CONTEXT, TaxonUtils.getEducationalContext(TOPIC_DOMAIN));
    }

    @Test
    public void educ_cont_parent_domain_topic_subtopic() throws Exception {
        validate(EDUCATIONAL_CONTEXT, TaxonUtils.getEducationalContext(SUBTOPIC_DOMAIN));
    }

    @Test
    public void educ_cont_parent_domain_specialization() throws Exception {
        validate(EDUCATIONAL_CONTEXT, TaxonUtils.getEducationalContext(SPECIALIZATION));
    }

    @Test
    public void educ_cont_parent_domain_specialization_module() throws Exception {
        validate(EDUCATIONAL_CONTEXT, TaxonUtils.getEducationalContext(MODULE));
    }

    @Test
    public void educ_cont_parent_domain_specialization_module_topic() throws Exception {
        validate(EDUCATIONAL_CONTEXT, TaxonUtils.getEducationalContext(TOPIC_MODULE));
    }

    @Test
    public void educ_cont_parent_domain_specialization_module_topic_subtopic() throws Exception {
        validate(EDUCATIONAL_CONTEXT, TaxonUtils.getEducationalContext(SUBTOPIC_MODULE));
    }

    // DOMAIN PARENT

    @Test
    public void domain_parent() throws Exception {
        validate(DOMAIN, TaxonUtils.getDomain(DOMAIN));
    }

    @Test
    public void domain_parent_subject() throws Exception {
        validate(DOMAIN, TaxonUtils.getDomain(SUBJECT));
    }

    @Test
    public void domain_parent_subject_topic() throws Exception {
        validate(DOMAIN, TaxonUtils.getDomain(TOPIC_SUBJECT));
    }

    @Test
    public void domain_parent_subject_topic_subtopic() throws Exception {
        validate(DOMAIN, TaxonUtils.getDomain(SUBTOPIC_SUBJECT));
    }

    @Test
    public void domain_parent_topic() throws Exception {
        validate(DOMAIN, TaxonUtils.getDomain(TOPIC_DOMAIN));
    }

    @Test
    public void domain_parent_topic_subtopic() throws Exception {
        validate(DOMAIN, TaxonUtils.getDomain(SUBTOPIC_DOMAIN));
    }

    @Test
    public void domain_parent_specialization() throws Exception {
        validate(DOMAIN, TaxonUtils.getDomain(SPECIALIZATION));
    }

    @Test
    public void domain_parent_specialization_module() throws Exception {
        validate(DOMAIN, TaxonUtils.getDomain(MODULE));
    }

    @Test
    public void domain_parent_specialization_module_topic() throws Exception {
        validate(DOMAIN, TaxonUtils.getDomain(TOPIC_MODULE));
    }

    @Test
    public void domain_parent_specialization_module_topic_subtopic() throws Exception {
        validate(DOMAIN, TaxonUtils.getDomain(SUBTOPIC_MODULE));
    }

    // SUBJECT PARENT

    @Test
    public void subject_parent() throws Exception {
        validate(SUBJECT, TaxonUtils.getSubject(SUBJECT));
    }

    @Test
    public void subject_parent_topic() throws Exception {
        validate(SUBJECT, TaxonUtils.getSubject(TOPIC_SUBJECT));
    }

    @Test
    public void subject_parent_topic_subtopic() throws Exception {
        validate(SUBJECT, TaxonUtils.getSubject(SUBTOPIC_SUBJECT));
    }

    // SPECIALIZATION PARENT

    @Test
    public void specialization_parent() throws Exception {
        validate(SPECIALIZATION, TaxonUtils.getSpecialization(SPECIALIZATION));
    }

    @Test
    public void specialization_parent_module() throws Exception {
        validate(SPECIALIZATION, TaxonUtils.getSpecialization(MODULE));
    }

    @Test
    public void specialization_parent_module_topic() throws Exception {
        validate(SPECIALIZATION, TaxonUtils.getSpecialization(TOPIC_MODULE));
    }

    @Test
    public void specialization_parent_module_topic_subtopic() throws Exception {
        validate(SPECIALIZATION, TaxonUtils.getSpecialization(SUBTOPIC_MODULE));
    }

    // MODULE PARENT

    @Test
    public void module_parent() throws Exception {
        validate(MODULE, TaxonUtils.getModule(MODULE));
    }

    @Test
    public void module_parent_topic() throws Exception {
        validate(MODULE, TaxonUtils.getModule(TOPIC_MODULE));
    }

    @Test
    public void module_parent_topic_subtopic() throws Exception {
        validate(MODULE, TaxonUtils.getModule(SUBTOPIC_MODULE));
    }

    // TOPIC PARENT

    @Test
    public void domain_topic_parent() throws Exception {
        validate(TOPIC_DOMAIN, TaxonUtils.getTopic(TOPIC_DOMAIN));
    }

    @Test
    public void domain_topic_parent_subtopic() throws Exception {
        validate(TOPIC_DOMAIN, TaxonUtils.getTopic(SUBTOPIC_DOMAIN));
    }

    @Test
    public void module_topic_parent() throws Exception {
        validate(TOPIC_MODULE, TaxonUtils.getTopic(TOPIC_MODULE));
    }

    @Test
    public void module_topic_parent_subtopic() throws Exception {
        validate(TOPIC_MODULE, TaxonUtils.getTopic(SUBTOPIC_MODULE));
    }

    @Test
    public void subject_topic_parent() throws Exception {
        validate(TOPIC_SUBJECT, TaxonUtils.getTopic(TOPIC_SUBJECT));
    }

    @Test
    public void subject_topic_parent_subtopic() throws Exception {
        validate(TOPIC_SUBJECT, TaxonUtils.getTopic(SUBTOPIC_SUBJECT));
    }

    // SUBTOPIC PARENT

    @Test
    public void domain_subtopic_parent() throws Exception {
        validate(SUBTOPIC_DOMAIN, TaxonUtils.getSubtopic(SUBTOPIC_DOMAIN));
    }

    @Test
    public void module_subtopic_parent() throws Exception {
        validate(SUBTOPIC_MODULE, TaxonUtils.getSubtopic(SUBTOPIC_MODULE));
    }

    @Test
    public void subject_subtopic_parent() throws Exception {
        validate(SUBTOPIC_SUBJECT, TaxonUtils.getSubtopic(SUBTOPIC_SUBJECT));
    }

    private void validate(Taxon educationalContext, Taxon result) {
        assertEquals(educationalContext.getName(), result.getName());
        assertEquals(educationalContext.getId(), result.getId());
        assertEquals(educationalContext, result);
    }
}
