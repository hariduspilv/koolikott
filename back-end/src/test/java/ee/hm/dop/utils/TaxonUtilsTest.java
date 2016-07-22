package ee.hm.dop.utils;

import static org.junit.Assert.assertEquals;

import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Subtopic;
import ee.hm.dop.model.taxon.Topic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TaxonUtilsTest {

    @Test
    public void getEducationalContextFromSubtopic() {
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");

        Domain domain = new Domain();
        domain.setId(3L);
        domain.setName("COOL_DOMAIN");
        domain.setEducationalContext(educationalContext);

        Subject subject = new Subject();
        subject.setId(4L);
        subject.setName("COOL_SUBJECT");
        subject.setDomain(domain);

        Topic topic = new Topic();
        topic.setId(5L);
        topic.setName("COOL_TOPIC");
        topic.setSubject(subject);

        Subtopic subtopic = new Subtopic();
        subtopic.setId(6L);
        subtopic.setName("COOL_SUBTOPIC");
        subtopic.setTopic(topic);

        EducationalContext result = TaxonUtils.getEducationalContext(subtopic);
        assertEquals(educationalContext.getName(), result.getName());
        assertEquals(educationalContext.getId(), result.getId());
    }

    @Test
    public void getEducationalContextFromEducationalContext() {
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");

        EducationalContext result = TaxonUtils.getEducationalContext(educationalContext);
        assertEquals(educationalContext.getName(), result.getName());
        assertEquals(educationalContext.getId(), result.getId());
        assertEquals(educationalContext, result);
    }

}
