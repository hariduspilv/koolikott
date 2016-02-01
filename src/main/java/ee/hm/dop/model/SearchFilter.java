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

    private String sort;

    private String sortDirection;

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

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

}
