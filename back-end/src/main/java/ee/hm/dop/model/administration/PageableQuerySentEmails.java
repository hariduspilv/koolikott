package ee.hm.dop.model.administration;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class PageableQuerySentEmails {

    private static final String BY_EMAIL_REC = "byEmailReceiver";
    private static final String BY_EMAIL_REC_DESC = "-byEmailReceiver";
    private static final String BY_LO_TITLE = "byLoTitle";
    private static final String BY_LO_TITLE_DESC = "-byLoTitle";
    private static final String BY_EMAIL_SENT_AT = "byEmailSentAt";
    private static final String BY_EMAIL_SENT_AT_DESC = "-byEmailSentAt";
    private static List<String> SORT_TYPES = Arrays.asList(BY_EMAIL_SENT_AT_DESC, BY_EMAIL_SENT_AT, BY_LO_TITLE_DESC, BY_LO_TITLE, BY_EMAIL_REC_DESC, BY_EMAIL_REC);
    private Sort sort;
    private int page;
    private int size;
    private String itemSortedBy;
    private boolean valid;
    private String query;
    private int lang;

    public PageableQuerySentEmails() {
    }

    public PageableQuerySentEmails(int page, String itemSortedBy, String query, int lang) {
        if (itemSortedBy != null && SORT_TYPES.contains(itemSortedBy)) {
            valid = true;
            sort = itemSortedBy.startsWith("-") ? Sort.DESC : Sort.ASC;
            this.page = page;
            this.size = 20;
            this.itemSortedBy = itemSortedBy;
            this.query = query;
            this.lang = lang;
        }
    }

    public boolean hasSearch() {
        return !StringUtils.isBlank(query) && query.trim().length() >= 3;
    }

    public boolean hasEmailReceiverOrder() {
        return orderByEmailReceiverDesc() || orderByEmailReceiver();
    }

    private boolean orderByEmailReceiver() {
        return itemSortedBy.equals(BY_EMAIL_REC);
    }

    private boolean orderByEmailReceiverDesc() {
        return itemSortedBy.equals(BY_EMAIL_REC_DESC);
    }

    private boolean orderByLoTitle() {
        return itemSortedBy.equals(BY_LO_TITLE);
    }

    private boolean orderByLoTitleDesc() {
        return itemSortedBy.equals(BY_LO_TITLE_DESC);
    }

    private boolean orderByEmailSentAt() {
        return itemSortedBy.equals(BY_EMAIL_SENT_AT);
    }

    private boolean orderByEmailSentAtDesc() {
        return itemSortedBy.equals(BY_EMAIL_SENT_AT_DESC);
    }

    public boolean hasOrderByTitle() {
        return orderByLoTitleDesc() || orderByLoTitle();
    }

    public String order() {

        if (orderByEmailReceiver()) {
            return "ORDER BY min(U.surName) " + sort.name();
        } else if (orderByEmailReceiverDesc()) {
            return "ORDER BY max(U.surName)" + sort.name();
        } else if (orderByLoTitle()) {
            return "ORDER BY e.id " + sort.name();
        } else if (orderByLoTitleDesc()) {
            return "ORDER BY e.id " + sort.name();
        } else if (orderByEmailSentAt()) {
            return "ORDER BY e.sentAt " + sort.name();
        } else if (orderByEmailSentAtDesc()) {
            return "ORDER BY e.sentAt " + sort.name();
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

}
