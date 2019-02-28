package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailToCreator implements AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String sender;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private boolean sentSuccessfully;

    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime createdAt;

    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime sentAt;

    @OneToOne
    @JoinColumn(name = "user")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    @Column(nullable = false)
    private String errorMessage;

    @Column(nullable = false)
    private int unsuccessfull;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSentSuccessfully() {
        return sentSuccessfully;
    }

    public void setSentSuccessfully(boolean sentSuccessfully) {
        this.sentSuccessfully = sentSuccessfully;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(DateTime sentAt) {
        this.sentAt = sentAt;
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

    public int getUnsuccessfull() {
        return unsuccessfull;
    }

    public void setUnsuccessfull(int unsuccessfull) {
        this.unsuccessfull = unsuccessfull;
    }


    @Override
    public Long getId() {
        return null;
    }

//    @JsonIgnore
//    @Column(unique = true, nullable = false)
//    private String idCode;
//
//    @Column(columnDefinition = "TEXT")
//    private String location;

//    @Enumerated(EnumType.STRING)
//    private Role role;
//
//    @ManyToOne
//    @JoinColumn(name = "publisher")
//    private Publisher publisher;
//
//    @OneToMany
//    @JoinTable(
//            name = "User_Taxon",
//            joinColumns = {@JoinColumn(name = "user")},
//            inverseJoinColumns = {@JoinColumn(name = "taxon")},
//            uniqueConstraints = @UniqueConstraint(columnNames = {"user", "taxon"}))
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    @JsonDeserialize(contentUsing = TaxonDeserializer.class)
//    @JsonSerialize(contentUsing = TaxonSerializer.class)
//    private List<Taxon> userTaxons;
//
//    @JsonIgnore
//    @OneToMany(mappedBy = "user", fetch = LAZY)
//    private List<User_Agreement> userAgreements;
//
//    @Transient
//    @JsonIgnore
//    private boolean newUser;


}
