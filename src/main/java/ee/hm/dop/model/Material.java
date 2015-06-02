package ee.hm.dop.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ee.hm.dop.rest.jackson.map.LocalDateDeserializer;
import ee.hm.dop.rest.jackson.map.LocalDateSerializer;

@Entity
public class Material {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String title;

    @ManyToMany
    @JoinTable(
            name = "Material_Author",
            joinColumns = { @JoinColumn(name = "material") },
            inverseJoinColumns = { @JoinColumn(name = "author") })
    private List<Author> authors;

    @Column
    private Short day;

    @Column
    private Short month;

    @Column
    private Integer year;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    @JsonDeserialize(using = LocalDateDeserializer.class)
    public void setIssueDate(LocalDate date) {
        if (date != null) {
            year = date.getYear();
            month = (short) date.getMonthOfYear();
            day = (short) date.getDayOfMonth();
        }
    }

    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate getIssueDate() {
        if (year == null || month == null || day == null) {
            return null;
        }

        return new LocalDate(year, month, day);
    }
}
