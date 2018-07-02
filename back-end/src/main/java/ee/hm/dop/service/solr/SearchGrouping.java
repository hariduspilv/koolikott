package ee.hm.dop.service.solr;

public enum SearchGrouping {
    GROUP_WORD, GROUP_NONE, GROUP_PHRASE;

    public boolean isSingleGrouping() {
        return this == GROUP_WORD;
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
