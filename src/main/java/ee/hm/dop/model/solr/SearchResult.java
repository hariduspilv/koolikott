package ee.hm.dop.model.solr;

import java.util.List;

import ee.hm.dop.model.Material;

public class SearchResult {

    private List<Material> materials;

    private long totalResults;

    private long start;

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

}
