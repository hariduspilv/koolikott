package ee.hm.dop.service.content.enums;

public enum SearchIndexStrategy {
    UPDATE_INDEX, SKIP_UPDATE;

    public boolean updateIndex(){
        return this == UPDATE_INDEX;
    }

    public boolean skipIndex(){
        return this == SKIP_UPDATE;
    }
}
