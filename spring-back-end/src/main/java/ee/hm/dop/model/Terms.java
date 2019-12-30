package ee.hm.dop.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.enums.TermType;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Terms implements AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "createdBy", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "agreement", nullable = false)
    private Agreement agreement;

    @Enumerated(EnumType.STRING)
    private TermType type;
}
