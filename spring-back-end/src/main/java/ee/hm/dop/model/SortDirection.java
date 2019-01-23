package ee.hm.dop.model;

public enum SortDirection {
    ASCENDING("asc"), DESCENDING("desc");

    private String direction;

    SortDirection(String direction) {
        this.direction = direction;
    }

    public static SortDirection getByValue(String value) {
        for (SortDirection sortDirection : SortDirection.values()) {
            if (sortDirection.getValue().equalsIgnoreCase(value)) return sortDirection;
        }
        return null;
    }

    public String getValue() {
        return direction;
    }

    public boolean isDesc(){
        return this == DESCENDING;
    }

    public boolean recentFirst(){
        return isDesc();
    }

    public boolean portfoliosFirst(){
        return isDesc();
    }
}
