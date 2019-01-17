package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import ee.hm.dop.model.enums.ReportingReasonEnum;

import javax.persistence.*;

@Entity
public class ReportingReason implements AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "improperContent", nullable = false)
    @JsonBackReference("improperContent")
    private ImproperContent improperContent;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportingReasonEnum reason;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ImproperContent getImproperContent() {
        return improperContent;
    }

    public void setImproperContent(ImproperContent improperContent) {
        this.improperContent = improperContent;
    }

    public ReportingReasonEnum getReason() {
        return reason;
    }

    public void setReason(ReportingReasonEnum reason) {
        this.reason = reason;
    }
}
