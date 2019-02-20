package ee.hm.dop.model.administration;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class PageableQuery {

    private static List<String> SORT_TYPES = Arrays.asList("bySubject", "byCreatedAt", "byCreatedBy", "byTitle",
            "-bySubject", "-byCreatedAt", "-byCreatedBy", "-byTitle");

    private Sort sort;
    private int page;
    private int size;
    private String itemSortedBy;
    private boolean valid;
    private String query;
    private List<String> taxons;
    private boolean isUserTaxon;
    private int lang;

    public PageableQuery() {
    }

    public PageableQuery(List<String> taxons, boolean isUserTaxon) {
        this.taxons = taxons;
        this.isUserTaxon = isUserTaxon;
    }

    public PageableQuery(String query, List<String> taxons) {
        this.query = query;
        this.taxons = taxons;
    }

    public PageableQuery(int page, String itemSortedBy, String query, List<String> taxons,boolean isUserTaxon, int lang) {
        if (itemSortedBy != null && SORT_TYPES.contains(itemSortedBy)) {
            valid = true;
            sort = itemSortedBy.startsWith("-") ? Sort.DESC : Sort.ASC;
            this.itemSortedBy = itemSortedBy;
            this.page = page;
            this.size = 200;
            this.query = query;
            this.taxons = taxons;
            this.isUserTaxon = isUserTaxon;
            this.lang = lang;
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

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean existsQuery() {
        return !StringUtils.isBlank(query);
    }

    public List<String> getTaxons() { return taxons; }

    public void setTaxons(List<String> taxons) {
        this.taxons = taxons;
    }

    public boolean existsTaxons() {
        return taxons.size() > 0;
    }

    public boolean isUserTaxon() {
        return isUserTaxon;
    }

    public void setUserTaxon(boolean userTaxon) {
        isUserTaxon = userTaxon;
    }

    public int getLang() { return lang; }

    public void setLang(int lang) { this.lang = lang; }
}
