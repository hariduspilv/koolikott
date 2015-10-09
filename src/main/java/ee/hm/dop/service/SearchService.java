package ee.hm.dop.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.solr.client.solrj.util.ClientUtils;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.dao.PortfolioDAO;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;
import ee.hm.dop.tokenizer.DOPSearchStringTokenizer;

public class SearchService {

    protected static final String MATERIAL_TYPE = "material";

    protected static final String PORTFOLIO_TYPE = "portfolio";

    @Inject
    private SearchEngineService searchEngineService;

    @Inject
    private MaterialDAO materialDAO;

    @Inject
    private PortfolioDAO portfolioDAO;

    public SearchResult search(String query, long start) {
        return search(query, start, null, null, null, null);
    }

    public SearchResult search(String query, String subject, String resourceType, String educationalContext,
            String licenseType) {
        return search(query, 0, subject, resourceType, educationalContext, licenseType);
    }

    public SearchResult search(String query, long start, String subject, String resourceType,
            String educationalContext, String licenseType) {
        SearchResult searchResult = new SearchResult();

        SearchResponse searchResponse = doSearch(query, start, subject, resourceType, educationalContext, licenseType);
        Response response = searchResponse.getResponse();

        if (response != null) {
            List<Document> documents = response.getDocuments();
            List<Searchable> unsortedSearchable = retrieveSearchedItems(documents);
            List<Searchable> sortedSearchable = sortSearchable(documents, unsortedSearchable);

            searchResult.setItems(sortedSearchable);
            searchResult.setStart(response.getStart());
            // "- documents.size() + sortedSearchable.size()" needed in case
            // SearchEngine and DB are not sync because of re-indexing time.
            searchResult.setTotalResults(response.getTotalResults() - documents.size() + sortedSearchable.size());
        }

        return searchResult;
    }

    private List<Searchable> retrieveSearchedItems(List<Document> documents) {
        List<Long> materialIds = new ArrayList<>();
        List<Long> portfolioIds = new ArrayList<>();
        for (Document document : documents) {
            switch (document.getType()) {
                case MATERIAL_TYPE:
                    materialIds.add(document.getId());
                    break;
                case PORTFOLIO_TYPE:
                    portfolioIds.add(document.getId());
                    break;
            }
        }

        List<Searchable> unsortedSearchable = new ArrayList<>();

        if (!materialIds.isEmpty()) {
            unsortedSearchable.addAll(materialDAO.findAllById(materialIds));
        }

        if (!portfolioIds.isEmpty()) {
            unsortedSearchable.addAll(portfolioDAO.findAllById(portfolioIds));
        }

        return unsortedSearchable;
    }

    private SearchResponse doSearch(String query, long start, String subject, String resourceType,
            String educationalContext, String licenseType) {
        String queryString = getTokenizedQueryString(query);

        String filtersAsQuery = getFiltersAsQuery(subject, resourceType, educationalContext, licenseType);
        if (!filtersAsQuery.isEmpty()) {
            queryString = "(" + queryString + ")" + filtersAsQuery;
        }

        return searchEngineService.search(queryString, start);
    }

    private List<Searchable> sortSearchable(List<Document> indexList, List<Searchable> unsortedSearchable) {
        List<Searchable> sortedSearchable = new ArrayList<>();

        for (Document document : indexList) {
            for (int i = 0; i < unsortedSearchable.size(); i++) {
                Searchable searchable = unsortedSearchable.get(i);

                if (document.getId() == searchable.getId() && document.getType().equals(searchable.getType())) {
                    sortedSearchable.add(searchable);
                    unsortedSearchable.remove(i);
                    break;
                }
            }
        }

        return sortedSearchable;
    }

    private String getTokenizedQueryString(String query) {
        StringBuilder sb = new StringBuilder();
        if (!isBlank(query)) {
            DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer(query);
            while (tokenizer.hasMoreTokens()) {
                sb.append(tokenizer.nextToken());
                if (tokenizer.hasMoreTokens()) {
                    sb.append(" ");
                }
            }
        } else {
            throw new RuntimeException("Empty search query!");
        }
        return sb.toString();
    }

    private String getFiltersAsQuery(String subject, String resourceType, String educationalContext, String licenseType) {
        Map<String, String> filters = new LinkedHashMap<>();
        filters.put("subject", subject);
        filters.put("resource_type", resourceType);
        filters.put("educational_context", educationalContext);
        filters.put("license_type", licenseType);

        // Convert filters to Solr syntax query
        String filtersAsQuery = "";
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            if (filter.getValue() != null) {
                String value = ClientUtils.escapeQueryChars(filter.getValue()).toLowerCase();
                filtersAsQuery += " AND " + filter.getKey() + ":\"" + value + "\"";
            }
        }
        return filtersAsQuery;
    }

}
