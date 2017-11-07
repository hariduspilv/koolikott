package ee.hm.dop.model;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;

import java.util.Arrays;
import java.util.List;

public class LearningObjectMiniDto {

    public static final String PORTFOLIO = ".Portfolio";
    public static final String REDUCED_PORTFOLIO = ".ReducedPortfolio";
    public static final String ADMIN_PORTFOLIO = ".AdminPortfolio";
    public static final String MATERIAL = ".Material";
    public static final String REDUCED_MATERIAL = ".ReducedMaterial";
    public static final String ADMIN_MATERIAL = ".AdminMaterial";
    public static final List<String> PORTFOLIOS = Arrays.asList(PORTFOLIO, REDUCED_PORTFOLIO, ADMIN_PORTFOLIO);
    public static final List<String> MATERIALS = Arrays.asList(MATERIAL, REDUCED_MATERIAL, ADMIN_MATERIAL);
    private Long id;
    private String type;

    public LearningObject convert() {
        if (PORTFOLIOS.contains(type)){
            Portfolio portfolio = new Portfolio();
            portfolio.setId(id);
            return portfolio;
        }
        if (MATERIALS.contains(type)){
            Material material = new Material();
            material.setId(id);
            return material;
        }
        throw new UnsupportedOperationException("missing or unknown type: " + type);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
