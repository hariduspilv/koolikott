package ee.hm.dop.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import org.hibernate.annotations.Type;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Faq implements AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionEst;

    private String questionRus;

    private String questionEng;

    @Column(columnDefinition = "TEXT")
    private String answerEst;

    @Column(columnDefinition = "TEXT")
    private String answerRus;

    @Column(columnDefinition = "TEXT")
    private String answerEng;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionEst() {
        return questionEst;
    }

    public void setQuestionEst(String questionEst) {
        this.questionEst = questionEst;
    }

    public String getQuestionRus() {
        return questionRus;
    }

    public void setQuestionRus(String questionRus) {
        this.questionRus = questionRus;
    }

    public String getQuestionEng() {
        return questionEng;
    }

    public void setQuestionEng(String questionEng) {
        this.questionEng = questionEng;
    }

    public String getAnswerEst() {
        return answerEst;
    }

    public void setAnswerEst(String answerEst) {
        this.answerEst = answerEst;
    }

    public String getAnswerRus() {
        return answerRus;
    }

    public void setAnswerRus(String answerRus) {
        this.answerRus = answerRus;
    }

    public String getAnswerEng() {
        return answerEng;
    }

    public void setAnswerEng(String answerEng) {
        this.answerEng = answerEng;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
