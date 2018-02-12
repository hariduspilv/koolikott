package ee.hm.dop.service.reviewmanagement.newdto;

import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsRow;

import java.util.List;
import java.util.Map;

public class NewStatisticsResult {

    public NewStatisticsResult() {
    }

    public NewStatisticsResult(StatisticsFilterDto filter, Map<EducationalContext, NewStatisticsRow> rows, NewStatisticsRow sum) {
        this.filter = filter;
        this.rows = rows;
        this.sum = sum;
    }

    private StatisticsFilterDto filter;
    private Map<EducationalContext, NewStatisticsRow> rows;
    private NewStatisticsRow sum;

    public StatisticsFilterDto getFilter() {
        return filter;
    }

    public void setFilter(StatisticsFilterDto filter) {
        this.filter = filter;
    }

    public Map<EducationalContext, NewStatisticsRow> getRows() {
        return rows;
    }

    public void setRows(Map<EducationalContext, NewStatisticsRow> rows) {
        this.rows = rows;
    }

    public NewStatisticsRow getSum() {
        return sum;
    }

    public void setSum(NewStatisticsRow sum) {
        this.sum = sum;
    }
}
