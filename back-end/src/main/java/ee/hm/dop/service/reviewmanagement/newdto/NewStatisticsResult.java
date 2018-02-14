package ee.hm.dop.service.reviewmanagement.newdto;

import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;

import java.util.List;

public class NewStatisticsResult {

    public NewStatisticsResult() {
    }

    public NewStatisticsResult(StatisticsFilterDto filter, List<EducationalContextRow> rows, NewStatisticsRow sum) {
        this.filter = filter;
        this.rows = rows;
        this.sum = sum;
    }

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
}
