package ee.hm.dop.model.sisuloome;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.AbstractEntity;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "SisuloomeMaterialAuthor")
public class SisuloomeMaterialAuthorEntity implements AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JsonIgnore
  @JoinColumn(name="sisuloomeMaterial")
  private SisuloomeMaterialEntity sisuloomeMaterial;
  @Column(nullable = false)
  @JsonSerialize(using = DateTimeSerializer.class)
  @JsonDeserialize(using = DateTimeDeserializer.class)
  private Timestamp createdAt;
  @Column
  private String name;
  @Column
  private String surname;
}
