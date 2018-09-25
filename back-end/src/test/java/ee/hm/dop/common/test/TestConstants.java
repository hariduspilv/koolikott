package ee.hm.dop.common.test;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;

public class TestConstants {
    public static final long NOT_EXISTS_ID = -1L;
    public static final String NOT_EXISTS_STRING = "-1L";

    public static final Long MATERIAL_1 = 1L;
    public static final Long MATERIAL_2 = 2L;
    public static final Long MATERIAL_3 = 3L;
    public static final Long MATERIAL_4 = 4L;
    public static final Long MATERIAL_5 = 5L;
    public static final Long MATERIAL_6 = 6L;
    public static final Long MATERIAL_7 = 7L;
    public static final Long MATERIAL_8 = 8L;
    public static final Long MATERIAL_9 = 9L;
    public static final Long MATERIAL_10 = 10L;
    public static final Long MATERIAL_11 = 11L;
    public static final Long MATERIAL_12 = 12L;
    public static final Long MATERIAL_13 = 13L;
    public static final Long MATERIAL_14 = 14L;
    public static final Long MATERIAL_15 = 15L;
    public static final Long MATERIAL_16 = 16L;
    public static final Long MATERIAL_17 = 17L;
    public static final Long MATERIAL_18 = 18L;
    public static final Long MATERIAL_19 = 19L;
    public static final Long MATERIAL_20 = 20L;
    public static final Long MATERIAL_21 = 21L;
    public static final Long MATERIAL_22 = 22L;
    public static final Long MATERIAL_23 = 23L;
    public static final Long MATERIAL_24 = 24L;
    public static final Long MATERIAL_25 = 25L;
    public static final Long MATERIAL_26 = 26L;
    public static final Long MATERIAL_27 = 27L;
    public static final Long MATERIAL_28 = 28L;
    public static final Long MATERIAL_29 = 29L;
    public static final Long MATERIAL_30 = 30L;
    public static final Long MATERIAL_31 = 31L;
    public static final Long MATERIAL_32 = 32L;
    public static final Long MATERIAL_33 = 33L;
    public static final Long MATERIAL_34 = 34L;
    public static final Long MATERIAL_35 = 35L;
    public static final Long MATERIAL_36 = 36L;
    public static final Long MATERIAL_37 = 37L;
    public static final Long MATERIAL_38 = 38L;
    public static final Long MATERIAL_39 = 39L;

    public static final Long PORTFOLIO_1 = 101L;
    public static final Long PORTFOLIO_2 = 102L;
    public static final Long PORTFOLIO_3 = 103L;
    public static final Long PORTFOLIO_4 = 104L;
    public static final Long PORTFOLIO_5 = 105L;
    public static final Long PORTFOLIO_6 = 106L;
    public static final Long PORTFOLIO_7 = 107L;
    public static final Long PORTFOLIO_8 = 108L;
    public static final Long PORTFOLIO_9 = 109L;
    public static final Long PORTFOLIO_10 = 110L;
    public static final Long PORTFOLIO_11 = 111L;
    public static final Long PORTFOLIO_12 = 112L;
    public static final Long PORTFOLIO_13 = 113L;
    public static final Long PORTFOLIO_14 = 114L;
    public static final Long PORTFOLIO_15 = 115L;
    public static final Long PORTFOLIO_16 = 116L;
    public static final Long PORTFOLIO_17 = 117L;

    public static final TestUser USER_MATI = new TestUser(1L, "39011220011", "mati.maasikas", "Mati", "Maasikas");
    public static final TestUser USER_PEETER = new TestUser(2L, "38011550077", "peeter.paan", "Peeter", "Paan");
    public static final TestUser USER_VOLDERMAR = new TestUser(3L, "37066990099", "voldemar.vapustav", "Voldemar", "Vapustav");
    public static final TestUser USER_VOLDERMAR2 = new TestUser(4L, "15066990099", "voldemar.vapustav2");
    public static final TestUser USER_MAASIKAS_VAARIKAS = new TestUser(6L, "39011220013", "mati.maasikas-vaarikas");
    public static final TestUser USER_MYTESTUSER = new TestUser(7L, "78912378912", "my.testuser");
    public static final TestUser USER_ADMIN = new TestUser(8L, "89898989898", "admin.admin");
    public static final TestUser USER_SECOND = new TestUser(9L, "89012378912", "second.testuser");
    public static final TestUser USER_RESTRICTED = new TestUser(11L, "89898989890", "restricted.user");
    public static final TestUser USER_MODERATOR = new TestUser(12L, "38211120031", "biffy.clyro");
    public static final TestUser USER_TO_BE_BANNED1 = new TestUser(13L, "38256133107", "user.to.be.banned1");
    public static final TestUser USER_TO_BE_BANNED2 = new TestUser(14L, "38256133108", "user.to.be.banned2");
    public static final TestUser USER_RESTRICTED2 = new TestUser(15L, "89898989892", "restricted.user2");

    public static final TestTaxon TAXON_TEACHER_EDUCATION = new TestTaxon(7L, "TEACHEREDUCATION");
    public static final TestTaxon TAXON_MATHEMATICS_DOMAIN = new TestTaxon(10L, "Mathematics");
    public static final TestTaxon TAXON_FOREIGNLANGUAGE_DOMAIN = new TestTaxon(11L, "ForeignLanguage");
    public static final TestTaxon TAXON_UNUSED_DOMAIN = new TestTaxon(15L, "Unused_Taxon");
    public static final TestTaxon TAXON_USED_DOMAIN = new TestTaxon(16L, "Used_Taxon");

    public static Material materialWithId(Long id) {
        Material material = new Material();
        material.setId(id);
        return material;
    }

    public static Portfolio portfolioWithId(Long id) {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(id);
        return portfolio;
    }

    public static User userWithId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
