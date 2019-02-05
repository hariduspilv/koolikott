package ee.hm.dop.service.solr;

import ee.hm.dop.model.SearchFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ee.hm.dop.service.solr.SearchService.PORTFOLIO_TYPE;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.*;

public class SearchCommandBuilder {

    public static final String PORTFOLIO_TITLE = "portfolioTitle";
    public static final String PORTFOLIO_CREATOR = "portfolio_creator";
    public static final String MATERIAL_AUTHOR = "author";
    public static final String MATERIAL_TITLE = "title";
    public static final String PORTFOLIO_SUMMARY = "summary";
    public static final String MATERIAL_DESCRIPTION = "description";
    public static final String DESCRIPTION = MATERIAL_DESCRIPTION;
    public static final String COMMON_PUBLISHER = "publisher";
    public static final String COMMON_TAG = "tag";
    public static final String RECOMMENDED = "recommended";
    public static final List<String> UNIQUE_KEYS =
            asList(MATERIAL_TITLE, PORTFOLIO_TITLE, COMMON_TAG, DESCRIPTION, PORTFOLIO_SUMMARY,
                    MATERIAL_AUTHOR, PORTFOLIO_CREATOR, COMMON_PUBLISHER, RECOMMENDED);
    private static final List<String> PORTFOLIO_KEYS =
            asList(PORTFOLIO_TITLE, COMMON_TAG, PORTFOLIO_SUMMARY, PORTFOLIO_CREATOR, COMMON_PUBLISHER);
    private static final List<String> MATERIAL_KEYS =
            asList(MATERIAL_TITLE, COMMON_TAG, MATERIAL_DESCRIPTION, MATERIAL_AUTHOR, COMMON_PUBLISHER);

    private static final String SEARCH_PATH = "select?q=%1$s" +
            "&sort=%2$s" +
            "&wt=json" +
            "&start=%3$d" +
            "&rows=%4$d";
    private static final String SEARCH_PATH_GROUPING = "&group=true" +
            "&group.format=simple" +
            "&stats=true" +
            "&stats.field=id" +
            "&stats.calcdistinct=true";

    private static final String GROUP_QUERY = "&group.query=";
    private static final String TYPE_MATERIAL = " AND type:\"material\"";
    private static final String TYPE_PORTFOLIO = " AND type:\"portfolio\"";

    static String getSearchCommand(SolrSearchRequest searchRequest, Long itemLimit) {
        if (searchRequest.getGrouping().isNoGrouping()) {
            return searchCommand(searchRequest, itemLimit, SEARCH_PATH);
        }
        return searchCommand(searchRequest, itemLimit, SEARCH_PATH + SEARCH_PATH_GROUPING) + getGroupingCommand(searchRequest);
    }

    static String keyMapper(String groupType, String key) {
        if (!groupType.equals(PORTFOLIO_TYPE)) {
            return key;
        } else if (key.equals(PORTFOLIO_TITLE)) {
            return MATERIAL_TITLE;
        } else if (key.equals(PORTFOLIO_SUMMARY)) {
            return DESCRIPTION;
        } else if (key.equals(PORTFOLIO_CREATOR)) {
            return MATERIAL_AUTHOR;
        }
        return key;
    }

    static String getCountCommand(SolrSearchRequest searchRequest) {
        String query = searchRequest.getOriginalQuery();
        if (searchRequest.getGrouping().isSingleGrouping()) query = getCleanQuery(query);
        String path = format(SEARCH_PATH, encode(searchRequest.getSolrQuery()), "", 0, 1);
        return path + SEARCH_PATH_GROUPING + GROUP_QUERY + encode(parenthasize(query)) + GROUP_QUERY + encode(quotify(query));
    }

    private static boolean isPhrase(String query) {
        return query != null && query.split("\\s+").length > 1;
    }

    static SearchGrouping pickGrouping(String query, SearchFilter searchFilter) {
        if (!searchFilter.isGrouped()) return SearchGrouping.GROUP_NONE;
        if (isPhrase(query)) return SearchGrouping.GROUP_PHRASE;
        return SearchGrouping.GROUP_WORD;
    }

    static String sanitizeQuery(String query, SearchFilter searchFilter) {
        if (query == null) return null;
        if (searchFilter.isFieldSpecificSearch()) return convertFieldSpecificQuery(query);
        else return getCleanQuery(query);
    }

    private static String getCleanQuery(String query) {
        return query.replaceAll("\"", "").replaceAll(":", "\\\\:");
    }

    private static String convertFieldSpecificQuery(String query) {
        String clearQuery = query.replaceAll("\"", "");
        String field = getSearchField(clearQuery);
        String search = getSearchQuery(clearQuery, field);
        return field + ":" + quotify(search);
    }

    private static String getSearchField(String query) {
        return UNIQUE_KEYS.stream().filter(query::startsWith).findAny().orElse("");
    }

    private static String getSearchQuery(String query, String field) {
        return query.substring(field.length()+1);
    }

    static String quotify(String query) {
        return "\"" + query + "\"";
    }

    private static String searchCommand(SolrSearchRequest searchRequest, Long itemLimit, String searchPath) {
        return format(searchPath,
                encode(searchRequest.getSolrQuery()),
                searchRequest.getSort() != null ? encode(searchRequest.getSort()) : "",
                searchRequest.getFirstItem(),
                itemLimit);
    }

    private static String getGroupingCommand(SolrSearchRequest searchRequest) {
        String query = StringUtils.isBlank(searchRequest.getOriginalQuery()) ? "\"\"" : searchRequest.getOriginalQuery();
        String groupSearchPathMaterial = buildPath(searchRequest, query, MATERIAL_KEYS, TYPE_MATERIAL);
        String groupSearchPathPortfolio = buildPath(searchRequest, query, PORTFOLIO_KEYS, TYPE_PORTFOLIO);
        return groupSearchPathMaterial + groupSearchPathPortfolio;
    }

    private static String buildPath(SolrSearchRequest searchRequest, String query, List<String> groupingKeys, String type) {
        if (searchRequest.getGrouping().isPhraseGrouping()) {
            return getGroupsForQuery(groupingKeys, parenthasize(query) + type) + getGroupsForQuery(groupingKeys, quotify(query) + type);
        } else return getGroupsForQuery(groupingKeys, query + type);
    }

    private static String parenthasize(String query) {
        return "(" + query + ")";
    }

    private static String getGroupsForQuery(List<String> groupingKeys, String query) {
        return groupingKeys.stream()
                .map(groupName -> GROUP_QUERY + groupName + ":" + encode(query))
                .collect(Collectors.joining());
    }

    private static String encode(String query) {
        try {
            return URLEncoder.encode(query, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
