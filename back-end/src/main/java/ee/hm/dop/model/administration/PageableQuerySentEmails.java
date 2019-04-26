package ee.hm.dop.model.administration;

import java.util.Arrays;
import java.util.List;

public class PageableQuerySentEmails extends PageableQuery {

    private static final String BY_EMAIL_REC = "byEmailReceiver";
    private static final String BY_EMAIL_REC_DESC = "-byEmailReceiver";
    private static final String BY_LO_TITLE = "byLoTitle";
    private static final String BY_LO_TITLE_DESC = "-byLoTitle";
    private static final String BY_EMAIL_SENT_AT = "byEmailSentAt";
    private static final String BY_EMAIL_SENT_AT_DESC = "-byEmailSentAt";
    private static List<String> SORT_TYPES = Arrays.asList(BY_EMAIL_SENT_AT_DESC, BY_EMAIL_SENT_AT, BY_LO_TITLE_DESC, BY_LO_TITLE, BY_EMAIL_REC_DESC, BY_EMAIL_REC);

    public PageableQuerySentEmails() {
        super();
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
}
