package ee.hm.dop.model.enums;

public enum LicenseType {

    /**
     * ALLRIGHTSRESERVED, CCBY, CCBYSA, CCBYND, CCBYNC, CCBYNCSA, CCBYNCND, YOUTUBE
     * are licenses which can be found on educational content which is created before the release of version 1.50.0
     * These licenses will now be replaced to CCBYSA30 with scripts or during user logins
     */
    ALLRIGHTSRESERVED, CCBY, CCBYSA, CCBYND, CCBYNC, CCBYNCSA, CCBYNCND, YOUTUBE,

    /**
     * CCBYSA30 is only acceptable licenseType for new educational content to be public.
     */
    CCBYSA30

}
