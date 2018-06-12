package ee.hm.dop.service.solr;

public class SearchRequest {
    private String solrQuery;
    private String originalQuery;
    private long firstItem;
    private Long itemLimit;
    private String sort;
    private SearchGrouping grouping = SearchGrouping.GROUP_NONE;

    public String getSolrQuery() {
        return solrQuery;
    }

    public void setSolrQuery(String solrQuery) {
        this.solrQuery = solrQuery;
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public void setOriginalQuery(String originalQuery) {
        this.originalQuery = originalQuery;
    }

    public long getFirstItem() {
        return firstItem;
    }

    public void setFirstItem(long firstItem) {
        this.firstItem = firstItem;
    }

    public Long getItemLimit() {
        return itemLimit != null ? itemLimit : 0;
    }

    public void setItemLimit(Long itemLimit) {
        this.itemLimit = itemLimit;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public SearchGrouping getGrouping() {
        return grouping;
    }

    public void setGrouping(SearchGrouping grouping) {
        this.grouping = grouping;
    }
}
