package ee.hm.dop.mapper;

import static ee.hm.dop.util.Sisuloome.SISULOOME_MATERIAL_AUTHOR_1;
import static ee.hm.dop.util.Sisuloome.SISULOOME_MATERIAL_AUTHOR_ENTITY_1;
import static org.assertj.core.api.Assertions.assertThat;

import ee.hm.dop.model.sisuloome.SisuloomeMaterialAuthorEntity;
import org.junit.Test;

public class SisuloomeMaterialAuthorMapperTest {

  private final SisuloomeMaterialAuthorMapper mapper = new SisuloomeMaterialAuthorMapperImpl();

  @Test
  public void toEntityShouldSuccessfullyMapResourceToEntity() {
    SisuloomeMaterialAuthorEntity result = mapper.toEntity(SISULOOME_MATERIAL_AUTHOR_1);
    assertThat(result).isEqualToComparingFieldByField(SISULOOME_MATERIAL_AUTHOR_ENTITY_1);
  }

  @Test
  public void toEntityShouldReturnNullWhenInputIsNull() {
    SisuloomeMaterialAuthorEntity result = mapper.toEntity(null);
    assertThat(result).isNull();
  }
}
