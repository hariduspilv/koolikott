package ee.hm.dop.service.reviewmanagement.newdto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;

import java.util.ArrayList;
import java.util.List;

public class NewStatisticsResult {

    public NewStatisticsResult() {
    }

    public NewStatisticsResult(StatisticsFilterDto filter, List<EducationalContextRow> rows, NewStatisticsRow sum) {
        this.dbTaxons = new ArrayList<>();
        this.filter = filter;
        this.rows = rows;
        this.sum = sum;
    }

    public NewStatisticsResult(List<Taxon> dbTaxons, StatisticsFilterDto filter, List<EducationalContextRow> rows, NewStatisticsRow sum) {
        this.dbTaxons = dbTaxons;
        this.filter = filter;
        this.rows = rows;
        this.sum = sum;
    }

    @JsonIgnore
    private List<Taxon> dbTaxons;
    private StatisticsFilterDto filter;
    private List<EducationalContextRow> rows;
    private NewStatisticsRow sum;

    public StatisticsFilterDto getFilter() {
        return filter;
    }

    public void setFilter(StatisticsFilterDto filter) {
        this.filter = filter;
    }

    public List<EducationalContextRow> getRows() {
        return rows;
    }

    public void setRows(List<EducationalContextRow> rows) {
        this.rows = rows;
    }

    public NewStatisticsRow getSum() {
        return sum;
    }

    public void setSum(NewStatisticsRow sum) {
        this.sum = sum;
    }

    public List<Taxon> getDbTaxons() {
        return dbTaxons;
    }

    public void setDbTaxons(List<Taxon> dbTaxons) {
        this.dbTaxons = dbTaxons;
    }
}
