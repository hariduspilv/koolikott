package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.ehis.InstitutionEhis;
import ee.hm.dop.model.enums.UserRole;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import ee.hm.dop.rest.jackson.map.TaxonDeserializer;
import ee.hm.dop.rest.jackson.map.TaxonSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class UserProfile implements AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;


    @Column
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column
    private String customRole;

    @Column
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime lastUpdate;


    @Transient
    @JsonDeserialize(contentUsing = TaxonDeserializer.class)
    @JsonSerialize(contentUsing = TaxonSerializer.class)
    private List<Taxon> taxons;

    @Transient
    private List<InstitutionEhis> institutions;

    @Transient
    private String email;

}
