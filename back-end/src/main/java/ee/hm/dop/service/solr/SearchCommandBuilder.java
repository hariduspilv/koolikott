package ee.hm.dop.service.solr;

import ee.hm.dop.model.SearchFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SearchCommandBuilder {

    private static final List<String> PORTFOLIO_KEYS = Arrays.asList("title", "tag", "summary", "author", "publisher");
    private static final List<String> MATERIAL_KEYS = Arrays.asList("title", "tag", "description", "author", "publisher");
    private static final List<String> UNIQUE_KEYS = Arrays.asList("title", "tag", "description", "summary", "author", "publisher");
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

    static String getCountCommand(SolrSearchRequest searchRequest) {
        String query = searchRequest.getOriginalQuery();
        String path = format(SEARCH_PATH, encode(searchRequest.getSolrQuery()), "", 0, 1);
        return path + SEARCH_PATH_GROUPING + GROUP_QUERY + encode(parenthasize(query)) + GROUP_QUERY + encode(quotify(query));
    }

    static boolean isPhrase(String query) {
        return query != null && query.split("\\s+").length > 1;
    }

    static SearchGrouping pickGrouping(String query, SearchFilter searchFilter) {
        if (!searchFilter.isGrouped()) return SearchGrouping.GROUP_NONE;
        if (isPhrase(query)) return SearchGrouping.GROUP_PHRASE;
        return SearchGrouping.GROUP_WORD;
    }

    static String clearQuerySearch(String query) {
        if (query == null) return null;
        if (UNIQUE_KEYS.stream().noneMatch((group) -> query.startsWith(group + ":"))) {
            return query.replaceAll("\"", "").replaceAll(":", "\\\\:");
        }
        return query.replaceAll("\"", "");
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
            return getGroupsForQuery(groupingKeys, parenthasize(query) + type)
                    + getGroupsForQuery(groupingKeys, quotify(query) + type);
        } else {
            return getGroupsForQuery(groupingKeys, query + type);
        }
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
