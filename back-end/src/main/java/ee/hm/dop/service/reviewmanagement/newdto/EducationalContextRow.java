package ee.hm.dop.service.reviewmanagement.newdto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.rest.jackson.map.TaxonDeserializer;
import ee.hm.dop.rest.jackson.map.TaxonSerializer;

import java.util.List;

public class EducationalContextRow {

    @JsonSerialize(using = TaxonSerializer.class)
    @JsonDeserialize(using = TaxonDeserializer.class)
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
