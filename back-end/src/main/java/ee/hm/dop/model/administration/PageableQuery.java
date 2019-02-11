package ee.hm.dop.model.administration;

import java.util.Arrays;
import java.util.List;

public class PageableQuery {

    private static List<String> SORT_TYPES = Arrays.asList("bySubject", "byCreatedAt", "byCreatedBy", "byCreatedBy", "byTitle",
            "-bySubject", "-byCreatedAt", "-byCreatedBy", "-byCreatedBy", "-byTitle");

    private Sort sort;
    private int page;
    private int size;
    private String itemSortedBy;
    private boolean valid;

    public PageableQuery() {
    }

    public PageableQuery(int page, String itemSortedBy) {
        if (itemSortedBy != null && SORT_TYPES.contains(itemSortedBy)) {
            valid = true;
            sort = itemSortedBy.startsWith("-") ? Sort.DESC : Sort.ASC;
            this.itemSortedBy = itemSortedBy;
            this.page = page;
            this.size = 200;
        }
    }

    public Sort getSort() {
        return sort;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getItemSortedBy() {
        return itemSortedBy;
    }

    public void setItemSortedBy(String itemSortedBy) {
        this.itemSortedBy = itemSortedBy;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getOffset() {
        return (page - 1) * size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
