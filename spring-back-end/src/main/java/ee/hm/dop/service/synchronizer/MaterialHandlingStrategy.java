package ee.hm.dop.service.synchronizer;

public enum MaterialHandlingStrategy {
    MATERIAL_IS_FROM_SAME_REPO,
    MATERIAL_IS_FROM_OTHER_REPO;

    public static MaterialHandlingStrategy of(boolean bool) {
        return bool ? MATERIAL_IS_FROM_SAME_REPO : MATERIAL_IS_FROM_OTHER_REPO;
    }

    public boolean isOtherRepo() {
        return this == MATERIAL_IS_FROM_OTHER_REPO;
    }

    public boolean isSameRepo() {
        return this == MATERIAL_IS_FROM_SAME_REPO;
    }
}
