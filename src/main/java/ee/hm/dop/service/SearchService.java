package ee.hm.dop.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.solr.client.solrj.util.ClientUtils;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;
import ee.hm.dop.tokenizer.DOPSearchStringTokenizer;

public class SearchService {

    @Inject
    private SearchEngineService searchEngineService;

    @Inject
    private MaterialDAO materialDAO;

    public SearchResult search(String query, long start) {
        return search(query, start, null, null, null, null);
    }

    public SearchResult search(String query, String subject, String resourceType, String educationalContext,
            String licenseType) {
        return search(query, 0, subject, resourceType, educationalContext, licenseType);
    }

    public SearchResult search(String query, long start, String subject, String resourceType, String educationalContext,
            String licenseType) {

        String filtersAsQuery = getFiltersAsQuery(subject, resourceType, educationalContext, licenseType);
        String tokenizedQueryString = getTokenizedQueryString(query);

        String queryString = tokenizedQueryString;
        if (!filtersAsQuery.isEmpty()) {
            queryString = "(" + tokenizedQueryString + ")" + filtersAsQuery;
        }
        SearchResponse searchResponse = searchEngineService.search(queryString, start);

        List<Long> materialIds = new ArrayList<>();

        Response response = searchResponse.getResponse();
        if (response != null) {
            for (Document document : response.getDocuments()) {
                materialIds.add(document.getId());
            }
        }

        List<Material> materials = Collections.emptyList();
        if (!materialIds.isEmpty()) {
            List<Material> unsortedMaterials = materialDAO.findAllById(materialIds);
            materials = sortMaterials(materialIds, unsortedMaterials);
        }

        SearchResult searchResult = new SearchResult();
        searchResult.setMaterials(materials);
        if (response != null) {
            searchResult.setTotalResults(response.getTotalResults());
            searchResult.setStart(response.getStart());
        } else {
            searchResult.setTotalResults(0);
            searchResult.setStart(0);
        }

        return searchResult;
    }

    private List<Material> sortMaterials(List<Long> indexList, List<Material> unsortedMaterials) {
        Material[] sortedMaterials = new Material[indexList.size()];

        for (Material material : unsortedMaterials) {
            sortedMaterials[indexList.indexOf(material.getId())] = material;
        }

        List<Material> sortedList = new ArrayList<>(Arrays.asList(sortedMaterials));

        // Removes null elements in case unsortedMaterials is smaller than
        // indexList
        sortedList.removeIf(material -> material == null);
        return sortedList;
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

    private String getFiltersAsQuery(String subject, String resourceType, String educationalContext,
            String licenseType) {
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
