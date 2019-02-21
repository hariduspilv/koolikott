package ee.hm.dop.model.administration;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class PageableQuery {

    public static final String FR_CREATED_AT = "byCreatedAt";
    public static final String FR_CREATED_AT_DESC = "-byCreatedAt";
    public static final String LO_CREATED_BY = "byCreatedBy";
    public static final String LO_CREATED_BY_DESC = "-byCreatedBy";
    public static final String BY_SUBJECT_TRANS = "bySubject";
    public static final String BY_SUBJECT_TRANS_DESC = "-bySubject";
    public static final String BY_TYPE = "byType";
    public static final String BY_TYPE_DESC = "-byType";
    private static List<String> SORT_TYPES = Arrays.asList(BY_SUBJECT_TRANS, FR_CREATED_AT,
            LO_CREATED_BY, BY_TYPE,
            BY_SUBJECT_TRANS_DESC, FR_CREATED_AT_DESC, LO_CREATED_BY_DESC, BY_TYPE_DESC);

    private Sort sort;
    private int page;
    private int size;
    private String itemSortedBy;
    private boolean valid;
    private String query;
    private List<Long> taxons;
    private List<Long> users;
    private int lang;

    public PageableQuery() {
    }

    public PageableQuery(int page, String itemSortedBy, String query,
                         List<Long> taxons,
                         List<Long> users,
                         int lang) {
        if (itemSortedBy != null && SORT_TYPES.contains(itemSortedBy)) {
            valid = true;
            sort = itemSortedBy.startsWith("-") ? Sort.DESC : Sort.ASC;
            this.itemSortedBy = itemSortedBy;
            this.page = page;
            this.size = 20;
            this.query = query;
            this.taxons = taxons;
            this.users = users;
            this.lang = lang;
        }
    }

    public boolean hasSearch() {
        return !StringUtils.isBlank(query) && query.trim().length() >= 3;
    }

    public boolean hasSubjectOrder() {
        return orderBySubject() || orderBySubjectDesc();
    }

    public boolean orderBySubject() {
        return itemSortedBy.equals(BY_SUBJECT_TRANS);
    }

    public boolean orderBySubjectDesc() {
        return itemSortedBy.equals(BY_SUBJECT_TRANS_DESC);
    }

    public boolean hasCreatorOrder() {
        return orderByCreatorDesc() || orderByCreator();
    }

    private boolean orderByCreatorDesc() {
        return itemSortedBy.equals(LO_CREATED_BY_DESC);
    }

    private boolean orderByCreator() {
        return itemSortedBy.equals(LO_CREATED_BY);
    }

    public boolean hasCreatedAtOrder() {
        return orderByFrCreatedAtDesc() || orderByFrCreatedAt();
    }

    private boolean orderByFrCreatedAtDesc() {
        return itemSortedBy.equals(FR_CREATED_AT_DESC);
    }

    private boolean orderByFrCreatedAt() {
        return itemSortedBy.equals(FR_CREATED_AT);
    }

    public boolean hasOrderByType() {
        return orderByTypeDesc() || orderByType();
    }

    private boolean orderByTypeDesc() {
        return itemSortedBy.equals(BY_TYPE_DESC);
    }

    private boolean orderByType() {
        return itemSortedBy.equals(BY_TYPE);
    }

    public boolean hasTaxonsOrUsers() {
        return hasTaxons() || hasUsers();
    }

    public boolean hasTaxons() {
        return taxons.size() > 0;
    }

    public boolean hasUsers() {
        return users.size() > 0;
    }

    public String order() {
        if (orderByFrCreatedAt()) {
            return "ORDER BY min(r.createdAt)" + sort.name();
        } else if (orderByFrCreatedAtDesc()) {
            return "ORDER BY max(r.createdAt)" + sort.name();
        } else if (orderByCreator()) {
            return "ORDER BY min(u.surName)" + sort.name();
        } else if (orderByCreatorDesc()) {
            return "ORDER BY max(u.surName)" + sort.name();
        } else if (orderBySubject()) {
            return "ORDER BY min(tr.translation) " + sort.name();
        } else if (orderBySubjectDesc()) {
            return "ORDER BY max(tr.translation) " + sort.name();
        } else if (orderByType()) {
            return "ORDER BY m.id " + sort.name();
        } else if (orderByTypeDesc()) {
            return "ORDER BY m.id " + sort.name();
        } else {
            throw new UnsupportedOperationException("unknown sort");
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
}
