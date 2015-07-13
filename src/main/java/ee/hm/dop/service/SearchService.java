package ee.hm.dop.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.model.Material;

public class SearchService {

    @Inject
    private SearchEngineService searchEngineService;

    @Inject
    private MaterialDAO materialDAO;

    public List<Material> search(String query) {
        List<Long> searchResult = searchEngineService.search(query);

        List<Material> materials = Collections.emptyList();
        if (!searchResult.isEmpty()) {
            List<Material> unsortedMaterials = materialDAO.findAllById(searchResult);
            materials = sortMaterials(searchResult, unsortedMaterials);
        }

        return materials;
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
