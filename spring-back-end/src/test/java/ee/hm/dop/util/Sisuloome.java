package ee.hm.dop.util;

import static ee.hm.dop.model.enums.Visibility.PUBLIC;
import static java.math.BigInteger.valueOf;
import static java.util.Arrays.asList;

import ee.hm.dop.model.sisuloome.SisuloomeMaterialAuthorEntity;
import ee.hm.dop.model.sisuloome.SisuloomeMaterialEntity;
import ee.hm.dop.rest.sisuloome.SisuloomeMaterial;
import ee.hm.dop.rest.sisuloome.SisuloomeMaterialAuthor;
import ee.hm.dop.rest.sisuloome.SisuloomeMaterialResponse;

/**
 * Test data for sisuloome related objects.
 */
public final class Sisuloome {

  public static final SisuloomeMaterialAuthorEntity SISULOOME_MATERIAL_AUTHOR_ENTITY_1 = createSisuloomeMaterialAuthorEntity1();
  public static final SisuloomeMaterialAuthorEntity SISULOOME_MATERIAL_AUTHOR_ENTITY_1_DB = createSisuloomeMaterialAuthorEntity1Db();
  public static final SisuloomeMaterialAuthorEntity SISULOOME_MATERIAL_AUTHOR_ENTITY_2 = createSisuloomeMaterialAuthorEntity2();
  public static final SisuloomeMaterialAuthorEntity SISULOOME_MATERIAL_AUTHOR_ENTITY_2_DB = createSisuloomeMaterialAuthorEntity2Db();
  public static final SisuloomeMaterialAuthorEntity SISULOOME_MATERIAL_AUTHOR_ENTITY_3 = createSisuloomeMaterialAuthorEntity3();
  public static final SisuloomeMaterialAuthorEntity SISULOOME_MATERIAL_AUTHOR_ENTITY_4 = createSisuloomeMaterialAuthorEntity4();
  public static final SisuloomeMaterialEntity SISULOOME_MATERIAL_ENTITY_1 = createSisuloomeMaterialEntity1();
  public static final SisuloomeMaterialEntity SISULOOME_MATERIAL_ENTITY_1_DB = createSisuloomeMaterialEntity1Db();
  public static final SisuloomeMaterialEntity SISULOOME_MATERIAL_ENTITY_2 = createSisuloomeMaterialEntity2();
  public static final SisuloomeMaterialAuthor SISULOOME_MATERIAL_AUTHOR_1 = createSisuloomeMaterialAuthor1();
  public static final SisuloomeMaterialAuthor SISULOOME_MATERIAL_AUTHOR_2 = createSisuloomeMaterialAuthor2();
  public static final SisuloomeMaterial SISULOOME_MATERIAL_1 = createSisuloomeMaterial1();
  public static final SisuloomeMaterialResponse SISULOOME_MATERIAL_RESPONSE_1 = createSisuloomeMaterialResponse1();

  private static SisuloomeMaterialEntity createSisuloomeMaterialEntity1() {
    return SisuloomeMaterialEntity.builder()
        .personalCode("36301014983")
        .externalMaterialId(valueOf(696L))
        .addedAt("11/03/2020 - 14:41")
        .visibility(PUBLIC)
        .title("The material of materials")
        .materialUrl("https://material.url.com/material")
        .embedCode("code code code")
        .authors(asList(SISULOOME_MATERIAL_AUTHOR_ENTITY_1, SISULOOME_MATERIAL_AUTHOR_ENTITY_2))
        .build();
  }

  private static SisuloomeMaterialEntity createSisuloomeMaterialEntity1Db() {
    return SISULOOME_MATERIAL_ENTITY_1.toBuilder()
        .id(1L)
        .authors(asList(SISULOOME_MATERIAL_AUTHOR_ENTITY_1_DB, SISULOOME_MATERIAL_AUTHOR_ENTITY_2_DB))
        .build();
  }

  private static SisuloomeMaterialEntity createSisuloomeMaterialEntity2() {
    return SisuloomeMaterialEntity.builder()
        .personalCode("36301014983")
        .externalMaterialId(valueOf(696L))
        .addedAt("11/03/2020 - 14:41")
        .visibility(PUBLIC)
        .title("The material of materials")
        .materialUrl("https://material.url.com/material")
        .embedCode("code code code")
        .authors(asList(SISULOOME_MATERIAL_AUTHOR_ENTITY_3, SISULOOME_MATERIAL_AUTHOR_ENTITY_4))
        .build();
  }

  private static SisuloomeMaterialAuthorEntity createSisuloomeMaterialAuthorEntity1() {
    return SisuloomeMaterialAuthorEntity.builder()
        .name("Leeroy")
        .surname("Jenkins")
        .build();
  }

  private static SisuloomeMaterialAuthorEntity createSisuloomeMaterialAuthorEntity1Db() {
    return SISULOOME_MATERIAL_AUTHOR_ENTITY_1.toBuilder()
        .id(1L)
        .build();
  }

  private static SisuloomeMaterialAuthorEntity createSisuloomeMaterialAuthorEntity2() {
    return SisuloomeMaterialAuthorEntity.builder()
        .name("Frodo")
        .surname("Leggings")
        .build();
  }

  private static SisuloomeMaterialAuthorEntity createSisuloomeMaterialAuthorEntity2Db() {
    return SISULOOME_MATERIAL_AUTHOR_ENTITY_2.toBuilder()
        .id(2L)
        .build();
  }

  private static SisuloomeMaterialAuthorEntity createSisuloomeMaterialAuthorEntity3() {
    return SisuloomeMaterialAuthorEntity.builder()
        .name("Pipi")
        .surname("Pikksokk")
        .build();
  }

  private static SisuloomeMaterialAuthorEntity createSisuloomeMaterialAuthorEntity4() {
    return SisuloomeMaterialAuthorEntity.builder()
        .name("I'm Mary")
        .surname("Poppins y'all")
        .build();
  }

  private static SisuloomeMaterial createSisuloomeMaterial1() {
    return SisuloomeMaterial.builder()
        .personalCode("36301014983")
        .externalMaterialId(valueOf(696L))
        .addedAt("11/03/2020 - 14:41")
        .visibility(PUBLIC)
        .title("The material of materials")
        .materialUrl("https://material.url.com/material")
        .embedCode("code code code")
        .authors(asList(SISULOOME_MATERIAL_AUTHOR_1, SISULOOME_MATERIAL_AUTHOR_2))
        .build();
  }

  private static SisuloomeMaterialAuthor createSisuloomeMaterialAuthor1() {
    return new SisuloomeMaterialAuthor("Leeroy", "Jenkins");
  }

  private static SisuloomeMaterialAuthor createSisuloomeMaterialAuthor2() {
    return new SisuloomeMaterialAuthor("Frodo", "Leggings");
  }

  private static SisuloomeMaterialResponse createSisuloomeMaterialResponse1() {
    return new SisuloomeMaterialResponse(valueOf(1L), valueOf(696L));
  }
}
