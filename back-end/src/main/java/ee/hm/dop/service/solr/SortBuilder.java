package ee.hm.dop.service.solr;

import ee.hm.dop.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

class SortBuilder {

    private static final String PORTFOLIO_FIRST = "type desc";
    private static final String MATERIAL_FIRST = "type asc";
    private static final String RECENT_FIRST = "added desc";
    private static final String EARLIEST_FIRST = "added asc";
    private static final String VISIBILITY = "visibility asc";
    private static final String ICON = "icon asc";
    private static final String ID_DESC = "id desc";
    private static final String ID_ASC = "id asc";

    static String getSort(SearchFilter searchFilter) {
        SortType sort = searchFilter.getSort();
        SortDirection sortDirection = searchFilter.getSortDirection();
        if (sort == null || sortDirection == null) {
            sort = SortType.DEFAULT;
            sortDirection = SortDirection.DESCENDING;
        }

        if (sort == SortType.DEFAULT) {
            return join(defaultSort(sortDirection));
        } else if (sort == SortType.TYPE) {
            return join(typeSort(sortDirection));
        } else if (sort == SortType.ADDED) {
            return join(addedSort(sortDirection));
        } else {
            return String.join(" ", sort.getValue(), sortDirection.getValue());
        }
    }

    private static ArrayList<String> addedSort(SortDirection dir) {
        if (dir.recentFirst()) {
            return newArrayList(RECENT_FIRST, VISIBILITY, ID_DESC);
        }
        return newArrayList(EARLIEST_FIRST, VISIBILITY, ID_ASC);
    }

    private static ArrayList<String> typeSort(SortDirection dir) {
        if (dir.portfoliosFirst()) {
            return newArrayList(PORTFOLIO_FIRST, ICON, RECENT_FIRST, VISIBILITY, ID_DESC);
        }
        return newArrayList(MATERIAL_FIRST, ICON, RECENT_FIRST, VISIBILITY, ID_ASC);
    }

    private static List<String> defaultSort(SortDirection dir) {
        if (dir.isDesc()) {
            return newArrayList(PORTFOLIO_FIRST, RECENT_FIRST, VISIBILITY, ID_DESC);
        }
        return newArrayList(PORTFOLIO_FIRST, EARLIEST_FIRST, VISIBILITY, ID_ASC);
    }

    private static String join(List<String> sortConditions) {
        return sortConditions.stream().collect(Collectors.joining(", "));
    }
}
