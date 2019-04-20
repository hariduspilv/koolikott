package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailToCreator implements AbstractEntity, Searchable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false)
    private String senderEmail;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column
    private boolean sentSuccessfully;

    @Column
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @Column
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime sentAt;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;


    @Column
    private String errorMessage;

    @Column(nullable = false)
    private int sentTries;

    @Transient
    private String creatorEmail;

    @Transient
    private Long creatorId;

    @Transient
    private Long learningObjectId;

    @Transient
    private String learningObjectTitle;

    public EmailToCreator() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSentSuccessfully() {
        return sentSuccessfully;
    }

    public void setSentSuccessfully(boolean sentSuccessfully) {
        this.sentSuccessfully = sentSuccessfully;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getSentTries() {
        return sentTries;
    }

    public void setSentTries(int sentTries) {
        this.sentTries = sentTries;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public Long getLearningObjectId() {
        return learningObjectId;
    }

    public void setLearningObjectId(Long learningObjectId) {
        this.learningObjectId = learningObjectId;
    }

    public String getLearningObjectTitle() {
        return learningObjectTitle;
    }

    public void setLearningObjectTitle(String learningObjectTitle) {
        this.learningObjectTitle = learningObjectTitle;
    }
}

