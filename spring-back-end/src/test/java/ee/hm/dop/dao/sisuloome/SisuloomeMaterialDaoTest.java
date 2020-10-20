package ee.hm.dop.dao.sisuloome;

import static ee.hm.dop.util.Sisuloome.SISULOOME_MATERIAL_ENTITY_1;
import static ee.hm.dop.util.Sisuloome.SISULOOME_MATERIAL_ENTITY_1_DB;
import static org.assertj.core.api.Assertions.assertThat;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.sisuloome.SisuloomeMaterialEntity;
import javax.inject.Inject;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;

public class SisuloomeMaterialDaoTest extends DatabaseTestBase {

  @Inject
  SisuloomeMaterialDao sisuloomeMaterialDao;

  @Test
  @Sql("/sisuloome/sql/resetSisuloomeTables.sql")
  public void createOrUpdateShouldSuccessfullyCreateValidRecord() {
    // TODO: Validate that created_at fields are correct
    SisuloomeMaterialEntity result = sisuloomeMaterialDao.createOrUpdate(SISULOOME_MATERIAL_ENTITY_1);
    assertThat(result).isEqualToComparingFieldByFieldRecursively(SISULOOME_MATERIAL_ENTITY_1_DB);
  }
}
