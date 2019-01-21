package ee.hm.dop.service.synchronizer.oaipmh;

import ee.hm.dop.model.Author;
import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.PeerReview;
import ee.hm.dop.model.Publisher;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.enums.TargetGroupEnum;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Module;
import ee.hm.dop.model.taxon.Specialization;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Subtopic;
import ee.hm.dop.model.taxon.Topic;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class BaseParserTest {

    public TargetGroup targetGroup(TargetGroupEnum sixSeven) {
        TargetGroup targetGroupSixSeven = new TargetGroup();
        targetGroupSixSeven.setName(sixSeven.name());
        return targetGroupSixSeven;
    }

    public KeyCompetence keyCompetence() {
        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setName("Cultural_and_value_competence");
        return keyCompetence;
    }

    public CrossCurricularTheme crossCurricularTheme() {
        CrossCurricularTheme crossCurricularTheme = new CrossCurricularTheme();
        crossCurricularTheme.setName("Lifelong_learning_and_career_planning");
        return crossCurricularTheme;
    }

    public Publisher publisher() {
        Publisher publisher = new Publisher();
        publisher.setName("BigPublisher");
        publisher.setWebsite("https://www.google.com/");
        return publisher;
    }

    public Specialization specialization(Domain domain3) {
        Specialization specialization = new Specialization();
        specialization.setName("Computers_and_Networks");
        specialization.setDomain(domain3);
        return specialization;
    }

    public PeerReview peerReview() {
        PeerReview peerReview = new PeerReview();
        peerReview.setUrl("http://www.facebook.com");
        return peerReview;
    }

    public Subtopic subTopic(Topic topic3, String subtopic_for_preschool_topic1) {
        Subtopic subtopic1 = new Subtopic();
        subtopic1.setName(subtopic_for_preschool_topic1);
        subtopic1.setTopic(topic3);
        return subtopic1;
    }

    public Topic topic(Module module) {
        Topic topic4 = new Topic();
        topic4.setName("Vocational_Education_Topic1");
        topic4.setModule(module);
        return topic4;
    }

    public Module module(Specialization specialization) {
        Module module = new Module();
        module.setName("Majanduse_alused");
        module.setSpecialization(specialization);
        return module;
    }

    public File getResourceAsFile(String resourcePath) throws URISyntaxException {
        URI resource = getClass().getClassLoader().getResource(resourcePath).toURI();
        return new File(resource);
    }
    
    public Topic topic(Subject subject1, String basic_history) {
        Topic topic1 = new Topic();
        topic1.setName(basic_history);
        topic1.setSubject(subject1);
        return topic1;
    }

    public Subject subject(Domain domain4, String estonian) {
        Subject subject1 = new Subject();
        subject1.setName(estonian);
        subject1.setDomain(domain4);
        return subject1;
    }

    public Domain domain(EducationalContext educationalContext2, String foreign_language) {
        Domain domain1 = new Domain();
        domain1.setName(foreign_language);
        domain1.setEducationalContext(educationalContext2);
        return domain1;
    }

    public EducationalContext educationalContext(String preschoolEducation) {
        EducationalContext educationalContext1 = new EducationalContext();
        educationalContext1.setName(preschoolEducation);
        return educationalContext1;
    }

    public ResourceType resourceType(long id, String audio) {
        ResourceType resourceType1 = new ResourceType();
        resourceType1.setId(id);
        resourceType1.setName(audio);
        return resourceType1;
    }

    public Tag tag(long id, String tag12) {
        Tag tag1 = new Tag();
        tag1.setId(id);
        tag1.setName(tag12);
        return tag1;
    }

    public LanguageString languageString(Language english, String text) {
        LanguageString description1 = new LanguageString();
        description1.setLanguage(english);
        description1.setText(text);
        return description1;
    }

    public Author author(String jonathan, String doe) {
        Author author1 = new Author();
        author1.setName(jonathan);
        author1.setSurname(doe);
        return author1;
    }

    public Language language(long id, String english2) {
        Language english = new Language();
        english.setId(id);
        english.setName(english2);
        return english;
    }

}
