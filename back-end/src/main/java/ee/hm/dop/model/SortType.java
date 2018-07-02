package ee.hm.dop.model;

public enum SortType {

    DEFAULT("default"), ADDED("added"), TYPE("type");

    private String type;

    SortType(String type) {
        this.type = type;
    }

    public static SortType getByValue(String value) {
        for (SortType sortType : SortType.values()) {
            if (sortType.getValue().equalsIgnoreCase(value)) return sortType;
        }
        return null;
    }

    public String getValue() {
        return type;
    }

}
