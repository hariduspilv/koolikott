package ee.hm.dop.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;
import ee.hm.dop.model.solr.SearchResult;

public class SearchService {

    @Inject
    private SearchEngineService searchEngineService;

    @Inject
    private MaterialDAO materialDAO;

    public SearchResult search(String query) {
        return search(query, 0);
    }

    public SearchResult search(String query, long start) {
        SearchResponse searchResponse = searchEngineService.search(query, start);

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

}
