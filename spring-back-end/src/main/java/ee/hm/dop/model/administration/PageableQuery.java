package ee.hm.dop.model.administration;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PageableQuery {

    protected Sort sort;
    protected int page;
    protected int size;
    protected String itemSortedBy;
    protected boolean valid;
    protected String query;
    protected List<Long> taxons;
    protected List<Long> users;
    protected int lang;
    protected String materialType;

    PageableQuery() {
    }

    public boolean hasSearch() {
        return !StringUtils.isBlank(query) && query.trim().length() >= 3;
    }

    public boolean hasTaxonsOrUsers() {
        return hasTaxons() || hasUsers();
    }

    public boolean hasTaxons() {
        if (taxons == null){
            return false;
        }
        return taxons.size() > 0;
    }

    public boolean hasUsers() {
        if (users == null){
            return false;
        }
        return users.size() > 0;
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

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public List<Long> getTaxons() {
        return taxons;
    }

    public void setTaxons(List<Long> taxons) {
        this.taxons = taxons;
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getMaterialType() {
        return materialType;
    }
}
