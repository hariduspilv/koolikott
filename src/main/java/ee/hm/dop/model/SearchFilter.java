package ee.hm.dop.model;

import java.util.List;

import ee.hm.dop.model.taxon.Taxon;

public class SearchFilter {

    private Taxon taxon;

    private boolean paid = true;

    private String type;

    private Language language;

    private List<TargetGroup> targetGroups;

    private ResourceType resourceType;

    private boolean isSpecialEducation = false;

    private Integer issuedFrom;

    private CrossCurricularTheme crossCurricularTheme;

    private KeyCompetence keyCompetence;

    private Visibility visibility;

    private Boolean isCurriculumLiterature;

    private String sort;

    private SortDirection sortDirection;

    public Taxon getTaxon() {
        return taxon;
    }

    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public List<TargetGroup> getTargetGroups() {
        return targetGroups;
    }

    public void setTargetGroups(List<TargetGroup> targetGroups) {
        this.targetGroups = targetGroups;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public boolean isSpecialEducation() {
        return isSpecialEducation;
    }

    public void setSpecialEducation(boolean isSpecialEducation) {
        this.isSpecialEducation = isSpecialEducation;
    }

    public Integer getIssuedFrom() {
        return issuedFrom;
    }

    public void setIssuedFrom(Integer issuedFrom) {
        this.issuedFrom = issuedFrom;
    }

    public CrossCurricularTheme getCrossCurricularTheme() {
        return crossCurricularTheme;
    }

    public void setCrossCurricularTheme(CrossCurricularTheme crossCurricularTheme) {
        this.crossCurricularTheme = crossCurricularTheme;
    }

    public KeyCompetence getKeyCompetence() {
        return keyCompetence;
    }

    public void setKeyCompetence(KeyCompetence keyCompetence) {
        this.keyCompetence = keyCompetence;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Boolean isCurriculumLiterature() {
        return isCurriculumLiterature;
    }

    public void setCurriculumLiterature(Boolean isCurriculumLiterature) {
        this.isCurriculumLiterature = isCurriculumLiterature;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

    public enum SortDirection {
        ASCENDING("asc"), DESCENDING("desc");

        private String direction;

        private SortDirection(String direction) {
            this.direction = direction;
        }

        public String getValue() {
            return direction;
        }

        public static SortDirection getByValue(String value) {
            for (SortDirection sortDirection : SortDirection.values()) {
                if (sortDirection.getValue().equalsIgnoreCase(value)) {
                    return sortDirection;
                }
            }
            return null;
        }
    }

}
