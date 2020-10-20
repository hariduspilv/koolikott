package ee.hm.dop.mapper;

import static ee.hm.dop.util.Sisuloome.SISULOOME_MATERIAL_1;
import static ee.hm.dop.util.Sisuloome.SISULOOME_MATERIAL_ENTITY_1;
import static org.assertj.core.api.Assertions.assertThat;

import ee.hm.dop.model.sisuloome.SisuloomeMaterialEntity;
import org.junit.Test;

public class SisuloomeMaterialMapperTest {

  private final SisuloomeMaterialMapper mapper = new SisuloomeMaterialMapperImpl(new SisuloomeMaterialAuthorMapperImpl());

  @Test
  public void toEntityShouldSuccessfullyMapResourceToEntity() {
    SisuloomeMaterialEntity result = mapper.toEntity(SISULOOME_MATERIAL_1);
    assertThat(result).isEqualToComparingFieldByFieldRecursively(SISULOOME_MATERIAL_ENTITY_1);
  }

  @Test
  public void toEntityShouldReturnNullWhenInputIsNull() {
    SisuloomeMaterialEntity result = mapper.toEntity(null);
    assertThat(result).isNull();
  }
}
