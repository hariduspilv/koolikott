package ee.hm.dop.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        return search(query, start, null);
    }

    public SearchResult search(String query, String subject) {
        return search(query, 0, subject);
    }

    public SearchResult search(String query, long start, String subject) {
        String queryString = getFiltersAsQuery(subject) + getTokenizedQueryString(query);
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

    private String getFiltersAsQuery(String subject) {
        if (subject == null) {
            return "";
        }

        return "+subject:\"" + ClientUtils.escapeQueryChars(subject).toLowerCase() + "\" ";
    }

}
