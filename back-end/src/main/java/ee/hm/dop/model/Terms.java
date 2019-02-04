package ee.hm.dop.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Terms implements AbstractEntity{

    @Id
    @GeneratedValue
    private Long id;

    private String titleEst;

    private String titleRus;

    private String titleEng;

    @Column(columnDefinition = "TEXT")
    private String contentEst;

    @Column(columnDefinition = "TEXT")
    private String contentRus;

    @Column(columnDefinition = "TEXT")
    private String contentEng;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleEst() {
        return titleEst;
    }

    public void setTitleEst(String titleEst) {
        this.titleEst = titleEst;
    }

    public String getTitleRus() {
        return titleRus;
    }

    public void setTitleRus(String titleRus) {
        this.titleRus = titleRus;
    }

    public String getTitleEng() {
        return titleEng;
    }

    public void setTitleEng(String titleEng) {
        this.titleEng = titleEng;
    }

    public String getContentEst() {
        return contentEst;
    }

    public void setContentEst(String contentEst) {
        this.contentEst = contentEst;
    }

    public String getContentRus() {
        return contentRus;
    }

    public void setContentRus(String contentRus) {
        this.contentRus = contentRus;
    }

    public String getContentEng() {
        return contentEng;
    }

    public void setContentEng(String contentEng) {
        this.contentEng = contentEng;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }
}
