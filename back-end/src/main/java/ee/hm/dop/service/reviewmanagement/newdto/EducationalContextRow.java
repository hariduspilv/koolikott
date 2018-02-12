package ee.hm.dop.service.reviewmanagement.newdto;

import ee.hm.dop.model.taxon.EducationalContext;

import java.util.List;

public class EducationalContextRow {

    private EducationalContext educationalContext;
    private List<NewStatisticsRow> rows;

    public EducationalContextRow() {
    }

    public EducationalContextRow(EducationalContext educationalContext, List<NewStatisticsRow> rows) {
        this.educationalContext = educationalContext;
        this.rows = rows;
    }

    public EducationalContext getEducationalContext() {
        return educationalContext;
    }

    public void setEducationalContext(EducationalContext educationalContext) {
        this.educationalContext = educationalContext;
    }

    public List<NewStatisticsRow> getRows() {
        return rows;
    }

    public void setRows(List<NewStatisticsRow> rows) {
        this.rows = rows;
    }
}
