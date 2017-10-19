package ee.hm.dop.common.test;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;

/**
 * Interface delegates TestConstants so you don't have to use static import all the time.
 * It is interface instead of abstract class, because it has no state.
 * It could be abstract, but why not. It's an experiment.
 */
public interface BaseClassForTests {
    Long NOT_EXISTS_ID = TestConstants.NOT_EXISTS_ID;

    TestUser USER_MATI = TestConstants.USER_MATI;
    TestUser USER_PEETER = TestConstants.USER_PEETER;
    TestUser USER_VOLDERMAR = TestConstants.USER_VOLDERMAR;
    TestUser USER_VOLDERMAR2 = TestConstants.USER_VOLDERMAR2;
    TestUser USER_MAASIKAS_VAARIKAS = TestConstants.USER_MAASIKAS_VAARIKAS;
    TestUser USER_MYTESTUSER = TestConstants.USER_MYTESTUSER;
    TestUser USER_ADMIN = TestConstants.USER_ADMIN;
    TestUser USER_SECOND = TestConstants.USER_SECOND;
    TestUser USER_RESTRICTED = TestConstants.USER_RESTRICTED;
    TestUser USER_MODERATOR = TestConstants.USER_MODERATOR;
    TestUser USER_TO_BE_BANNED1 = TestConstants.USER_TO_BE_BANNED1;
    TestUser USER_TO_BE_BANNED2 = TestConstants.USER_TO_BE_BANNED2;
    TestUser USER_RESTRICTED2 = TestConstants.USER_RESTRICTED2;

    Long MATERIAL_1 = TestConstants.MATERIAL_1;
    Long MATERIAL_2 = TestConstants.MATERIAL_2;
    Long MATERIAL_3 = TestConstants.MATERIAL_3;
    Long MATERIAL_4 = TestConstants.MATERIAL_4;
    Long MATERIAL_5 = TestConstants.MATERIAL_5;
    Long MATERIAL_6 = TestConstants.MATERIAL_6;
    Long MATERIAL_7 = TestConstants.MATERIAL_7;
    Long MATERIAL_8 = TestConstants.MATERIAL_8;
    Long MATERIAL_9 = TestConstants.MATERIAL_9;
    Long MATERIAL_10 = TestConstants.MATERIAL_10;
    Long MATERIAL_11 = TestConstants.MATERIAL_11;
    Long MATERIAL_12 = TestConstants.MATERIAL_12;
    Long MATERIAL_13 = TestConstants.MATERIAL_13;
    Long MATERIAL_14 = TestConstants.MATERIAL_14;
    Long MATERIAL_15 = TestConstants.MATERIAL_15;

    Long PORTFOLIO_1 = TestConstants.PORTFOLIO_1;
    Long PORTFOLIO_2 = TestConstants.PORTFOLIO_2;
    Long PORTFOLIO_3 = TestConstants.PORTFOLIO_3;
    Long PORTFOLIO_4 = TestConstants.PORTFOLIO_4;
    Long PORTFOLIO_5 = TestConstants.PORTFOLIO_5;
    Long PORTFOLIO_6 = TestConstants.PORTFOLIO_6;
    Long PORTFOLIO_7 = TestConstants.PORTFOLIO_7;
    Long PORTFOLIO_8 = TestConstants.PORTFOLIO_8;
    Long PORTFOLIO_9 = TestConstants.PORTFOLIO_9;
    Long PORTFOLIO_10 = TestConstants.PORTFOLIO_10;
    Long PORTFOLIO_11 = TestConstants.PORTFOLIO_11;
    Long PORTFOLIO_12 = TestConstants.PORTFOLIO_12;
    Long PORTFOLIO_13 = TestConstants.PORTFOLIO_13;
    Long PORTFOLIO_14 = TestConstants.PORTFOLIO_14;
    Long PORTFOLIO_15 = TestConstants.PORTFOLIO_15;

    TestTaxon TAXON_MATHEMATICS_DOMAIN = TestConstants.TAXON_MATHEMATICS_DOMAIN;

    default Material materialWithId(Long id) {
        return TestConstants.materialWithId(id);
    }

    default Portfolio portfolioWithId(Long id) {
        return TestConstants.portfolioWithId(id);
    }

    default User userWithId(Long id) {
        return TestConstants.userWithId(id);
    }

    default void assertMaterial1(Material material, TestLayer testLayer) {
        Material1Validation.assertMaterial1(material, testLayer);
    }

    default void assertPortfolio1(Portfolio portfolio, TestLayer testLayer) {
        Portfolio1Validator.assertPortfolio1(portfolio, testLayer);
    }

    default void validateUser(User user, TestUser testUser, TestLayer testLayer) {
        UserValidation.assertUser(user, testUser, testLayer);
    }
}
