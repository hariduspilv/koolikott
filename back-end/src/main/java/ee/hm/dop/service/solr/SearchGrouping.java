package ee.hm.dop.service.solr;

public enum SearchGrouping {
    GROUP_ALL, GROUP_NONE;

    public boolean isGrouped() {
        return this == GROUP_ALL;
    }
}
