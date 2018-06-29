package ee.hm.dop.service.solr;

public enum SearchGrouping {
    GROUP_SIMILAR, GROUP_NONE, GROUP_PHRASE;

    public boolean isSimilarGrouping() {
        return this == GROUP_SIMILAR;
    }

    public boolean isPhraseGrouping() {
        return this == GROUP_PHRASE;
    }

    public boolean isAnyGrouping() {
        return this != GROUP_NONE;
    }

    public boolean isNoGrouping() {
        return this == GROUP_NONE;
    }
}
