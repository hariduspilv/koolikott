package ee.hm.dop.rest.sisuloome;

import static ee.hm.dop.util.Sisuloome.SISULOOME_MATERIAL_1;
import static org.assertj.core.api.Assertions.assertThat;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.util.Sisuloome;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SisuloomeMaterialResourceIntegrationTest extends ResourceIntegrationTestBase {

  @Test
  public void postSisuloomeMaterialShouldReturnMaterialResponseOnSuccess() {
    SisuloomeMaterialResponse response = doPost("sisuloome", toJson(SISULOOME_MATERIAL_1), SisuloomeMaterialResponse.class);
    assertThat(response).isEqualToComparingFieldByField(Sisuloome.SISULOOME_MATERIAL_RESPONSE_1);
  }

}
