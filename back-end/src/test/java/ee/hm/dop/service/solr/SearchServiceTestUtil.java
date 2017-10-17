package ee.hm.dop.service.solr;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.enums.TargetGroupEnum;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;
import ee.hm.dop.model.taxon.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SearchServiceTestUtil {

    public static final User ADMIN = admin();
    public static final List<Searchable> M9_M2_P1 = Arrays.asList(material(9L), material(2L), portfolio(1L));
    public static final List<Searchable> M9_M2 = Arrays.asList(material(9L), material(2L));
    public static final List<Searchable> P9_P2 = Arrays.asList(portfolio(9L), portfolio(2L));
    public static final List<Searchable> M3_M4 = Arrays.asList(material(3L), material(4L));
    public static final String PYTHAGORAS = "pythagoras";
    public static final String EMPTY = "";
    public static final String PEOPLE = "people";
    public static final List<Searchable> M7_M1_P4_P2 = Arrays.asList(material(7L), material(1L), portfolio(4L), portfolio(2L));
    public static final String TEXTBOOKS = "textbooks";
    public static final String SKY = "sky";
    public static final String GERMAN_LANGUAGE = "german language";
    public static final String ENGLISH_LANGUAGE = "english language";
    public static final String AIRPLANE = "airplane";
    public static final String TEST = "test";
    public static final String MATERIAL = "material";
    public static final String PORTFOLIO = "portfolio";
    public static final List<Taxon> CONTEXT = Collections.singletonList(context());
    public static final List<Taxon> EDUCATIONAL_CONTEXT = Collections.singletonList(educationalContext());
    public static final List<Taxon> DOMAIN_EDUC_CONT = Collections.singletonList(domain(educationalContext()));
    public static final List<Taxon> SUBJECT_DOMAIN_EDUC_CONT = Collections.singletonList(subject(domain(educationalContext())));
    public static final List<Taxon> SPECIAL_DOMAIN_EDUC_CONT = Collections.singletonList(specialization(domain(educationalContext())));
    public static final List<Taxon> MODULE_SPECIAL_DOMAIN_EDUC_CONT = Collections.singletonList(module(specialization(domain(educationalContext()))));
    public static final List<Taxon> TOPIC_DOMAIN_EDUC_CONT = Collections.singletonList(topic(domain(educationalContext())));
    public static final List<Taxon> TOPIC_SUBJECT_DOMAIN_EDUC_CONT = Collections.singletonList(topic(subject(domain(educationalContext()))));
    public static final List<Taxon> TOPIC_MODULE_SPECIAL_DOMAIN_EDUC_CONT = Collections.singletonList(topic(module(specialization(domain(educationalContext())))));
    public static final List<Taxon> SUBTOPIC_TOPIC_DOMAIN_EDUC_CONT = Collections.singletonList(subTopic(topic(domain(educationalContext()))));
    public static final List<Taxon> SUBTOPIC_TOPIC_SUBJECT_DOMAIN_EDUC_CONT = Collections.singletonList(subTopic(topic(subject(domain(educationalContext())))));
    public static final List<Taxon> SUBTOPIC_TOPIC_MODULE_SPECIAL_DOMAIN_EDUC_CONT = Collections.singletonList(subTopic(topic(module(specialization(domain(educationalContext()))))));
    public static final String ALPHA_BETA = "alpha beta";
    public static final String UMM = "umm";
    public static final List<KeyCompetence> KEY_COMPETENCE = Collections.singletonList(keyCompetence());
    public static final List<CrossCurricularTheme> CROSS_CURRICULAR_THEMES = Collections.singletonList(crossCurricularTheme());


    public static List<Long> getIdentifiers(List<Searchable> searchables) {
        return searchables.stream().map(Searchable::getId).collect(Collectors.toList());
    }

    public static SearchResponse createSearchResponseWithDocuments(List<Searchable> searchables, long start, long totalResults) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResponse(makeResponse(searchables, start, totalResults));
        return searchResponse;
    }

    public static Response makeResponse(List<Searchable> searchables, long start, long totalResults) {
        List<Document> documents = searchables.stream().map(SearchServiceTestUtil::makeDocument).collect(Collectors.toList());
        Response response = new Response();
        response.setDocuments(documents);
        response.setTotalResults(totalResults);
        response.setStart(start);
        return response;
    }

    public static Document makeDocument(Searchable searchable) {
        Document newDocument = new Document();
        newDocument.setId(searchable.getId().toString());
        newDocument.setType(searchable.getType());
        return newDocument;
    }

    public static List<ReducedMaterial> collectMaterialsFrom(List<Searchable> searchables) {
        return searchables.stream()
                .filter(searchable -> searchable instanceof ReducedMaterial)
                .map(searchable -> (ReducedMaterial) searchable)
                .collect(Collectors.toList());
    }

    public static List<ReducedPortfolio> collectPortfoliosFrom(List<Searchable> searchables) {
        return searchables.stream()
                .filter(searchable -> searchable instanceof ReducedPortfolio)
                .map(searchable -> (ReducedPortfolio) searchable)
                .collect(Collectors.toList());
    }

    public static ReducedMaterial material(Long id) {
        ReducedMaterial material = new ReducedMaterial();
        material.setId(id);
        return material;
    }

    public static ReducedPortfolio portfolio(Long id) {
        ReducedPortfolio portfolio = new ReducedPortfolio();
        portfolio.setId(id);
        return portfolio;
    }

    public static User admin() {
        User loggedInUser = new User();
        loggedInUser.setRole(Role.ADMIN);
        return loggedInUser;
    }

    public static Language language() {
        Language language = new Language();
        language.setCode("mmm");
        return language;
    }

    public static CrossCurricularTheme crossCurricularTheme() {
        CrossCurricularTheme crossCurricularTheme = new CrossCurricularTheme();
        crossCurricularTheme.setId(1L);
        crossCurricularTheme.setName("test_theme");
        return crossCurricularTheme;
    }


    public static EducationalContext educationalContext() {
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");
        return educationalContext;
    }

    public static EducationalContext context() {
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(1L);
        educationalContext.setName("context");
        return educationalContext;
    }

    public static KeyCompetence keyCompetence() {
        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1L);
        keyCompetence.setName("test_competence");
        return keyCompetence;
    }

    public static Specialization specialization(Domain domain) {
        Specialization specialization = new Specialization();
        specialization.setId(4L);
        specialization.setName("COOL_SPECIALIZATION");
        specialization.setDomain(domain);
        return specialization;
    }

    public static Module module(Specialization specialization) {
        Module module = new Module();
        module.setId(5L);
        module.setName("COOL_MODULE");
        module.setSpecialization(specialization);
        return module;
    }

    public static Topic topic(Domain domain) {
        Topic topic = new Topic();
        topic.setId(6L);
        topic.setName("COOL_TOPIC");
        topic.setDomain(domain);
        return topic;
    }

    public static Topic topic(Module module) {
        Topic topic = new Topic();
        topic.setId(6L);
        topic.setName("COOL_TOPIC");
        topic.setModule(module);
        return topic;
    }

    public static Subtopic subTopic(Topic topic) {
        Subtopic subtopic = new Subtopic();
        subtopic.setId(7L);
        subtopic.setName("COOL_SUBTOPIC");
        subtopic.setTopic(topic);
        return subtopic;
    }

    public static Topic topic(Subject subject) {
        Topic topic = new Topic();
        topic.setId(5L);
        topic.setName("COOL_TOPIC");
        topic.setSubject(subject);
        return topic;
    }

    public static Subject subject(Domain domain) {
        Subject subject = new Subject();
        subject.setId(4L);
        subject.setName("COOL_SUBJECT");
        subject.setDomain(domain);
        return subject;
    }

    public static ResourceType resourceType() {
        ResourceType resourceType = new ResourceType();
        resourceType.setId(1L);
        resourceType.setName("EXTRABOOK");
        return resourceType;
    }

    public static Domain domain(EducationalContext educationalContext) {
        Domain domain = new Domain();
        domain.setId(3L);
        domain.setName("COOL_DOMAIN");
        domain.setEducationalContext(educationalContext);
        return domain;
    }

    public static TargetGroup zeroFive() {
        TargetGroup targetGroupZeroFive = new TargetGroup();
        targetGroupZeroFive.setId(1L);
        targetGroupZeroFive.setName(TargetGroupEnum.ZERO_FIVE.name());
        return targetGroupZeroFive;
    }

    public static TargetGroup sixSeven() {
        TargetGroup targetGroupSixSeven = new TargetGroup();
        targetGroupSixSeven.setId(2L);
        targetGroupSixSeven.setName(TargetGroupEnum.SIX_SEVEN.name());
        return targetGroupSixSeven;
    }
}
