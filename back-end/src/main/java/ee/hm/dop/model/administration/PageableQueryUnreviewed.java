package ee.hm.dop.model.administration;

import java.util.Arrays;
import java.util.List;

public class PageableQueryUnreviewed extends PageableQuery {

    private static final String FR_CREATED_AT = "byCreatedAt";
    private static final String FR_CREATED_AT_DESC = "-byCreatedAt";
    private static final String LO_CREATED_BY = "byCreatedBy";
    private static final String LO_CREATED_BY_DESC = "-byCreatedBy";
    private static final String BY_SUBJECT_TRANS = "bySubject";
    private static final String BY_SUBJECT_TRANS_DESC = "-bySubject";
    private static final String BY_TYPE = "byType";
    private static final String BY_TYPE_DESC = "-byType";
    private static final String BY_MATERIAL_TYPE_MATERIAL = "Material";
    private static final String BY_MATERIAL_TYPE_PORTFOLIO = "Portfolio";
    private static final String BY_MATERIAL_TYPE_ALL = "All";
    private static List<String> SORT_TYPES = Arrays.asList(BY_SUBJECT_TRANS, FR_CREATED_AT,
            LO_CREATED_BY, BY_TYPE,
            BY_SUBJECT_TRANS_DESC, FR_CREATED_AT_DESC, LO_CREATED_BY_DESC, BY_TYPE_DESC);
    private static List<String> MATERIAL_TYPES = Arrays.asList(BY_MATERIAL_TYPE_MATERIAL, BY_MATERIAL_TYPE_PORTFOLIO, BY_MATERIAL_TYPE_ALL);

    public PageableQueryUnreviewed() {
        super();
    }

    public PageableQueryUnreviewed(int page, String itemSortedBy, String query,
                                   List<Long> taxons,
                                   List<Long> users,
                                   int lang,
                                   String materialType) {

        if (materialType != null && MATERIAL_TYPES.contains(materialType)) {
            this.materialType = materialType.equalsIgnoreCase("All") ? "" : materialType;

        } else {
            valid = false;
        }
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

    public boolean hasFilterByTypeMaterial() {
        return materialType.equals(BY_MATERIAL_TYPE_MATERIAL);
    }

    public boolean hasFilterByTypePortfolio() {
        return materialType.equals(BY_MATERIAL_TYPE_PORTFOLIO);
    }

    private boolean orderByTypeDesc() {
        return itemSortedBy.equals(BY_TYPE_DESC);
    }

    private boolean orderByType() {
        return itemSortedBy.equals(BY_TYPE);
    }

    public String order() {
        if (orderByFrCreatedAt()) {
            return "ORDER BY min(r.createdAt) " + sort.name();
        } else if (orderByFrCreatedAtDesc()) {
            return "ORDER BY max(r.createdAt) " + sort.name();
        } else if (orderByCreator()) {
            return "ORDER BY min(u.surName) " + sort.name();
        } else if (orderByCreatorDesc()) {
            return "ORDER BY max(u.surName) " + sort.name();
        } else if (orderBySubject()) {
            return "ORDER BY min(tr.translation) " + sort.name() + ", min(tr2.translation) " + sort.name();
        } else if (orderBySubjectDesc()) {
            return "ORDER BY max(tr.translation) " + sort.name() + ", max(tr2.translation) " + sort.name();
        } else if (orderByType()) {
            return "ORDER BY m.id " + sort.name();
        } else if (orderByTypeDesc()) {
            return "ORDER BY m.id " + sort.name();
        } else {
            throw new UnsupportedOperationException("unknown sort");
        }
    }
}
