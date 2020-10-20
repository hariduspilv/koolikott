package ee.hm.dop.model.sisuloome;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.AbstractEntity;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SisuloomeMaterial")
public class SisuloomeMaterialEntity implements AbstractEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  @Column(nullable = false)
  @JsonSerialize(using = DateTimeSerializer.class)
  @JsonDeserialize(using = DateTimeDeserializer.class)
  private Timestamp createdAt;
  @Column
  private String personalCode;
  @Column
  private BigInteger externalMaterialId;
  @Column
  private String addedAt;
  @Column
  private Visibility visibility;
  @Column
  private String title;
  @Column
  private String materialUrl;
  @Column
  private String embedCode;
  @OneToMany(cascade = ALL, mappedBy = "sisuloomeMaterial", fetch = EAGER)
  private List<SisuloomeMaterialAuthorEntity> authors;
}
