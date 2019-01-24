package ee.hm.dop.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class IssueDate implements AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Short day;

    @Column
    private Short month;

    @Column
    private Integer year;

    public IssueDate() {
    }

    public IssueDate(Short day, Short month, Integer year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public IssueDate(LocalDateTime dateTime) {
        this.day = (short) dateTime.getDayOfMonth();
        this.month = (short) dateTime.getMonthValue();
        this.year = dateTime.getYear();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getDay() {
        return day;
    }

    public void setDay(Short day) {
        this.day = day;
    }

    public Short getMonth() {
        return month;
    }

    public void setMonth(Short month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31) //
                .append(day) //
                .append(month) //
                .append(year) //
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof IssueDate)) {
            return false;
        }

        IssueDate other = (IssueDate) obj;

        return new EqualsBuilder().append(day, other.day) //
                .append(month, other.month) //
                .append(year, other.year) //
                .isEquals();
    }
}
