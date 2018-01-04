package ee.hm.dop.service.reviewmanagement.dto;

import java.util.List;

public class StatisticsResult {

    public StatisticsResult() {
    }

    public StatisticsResult(StatisticsFilterDto filter, List<StatisticsRow> rows, StatisticsRow sum) {
        this.filter = filter;
        this.rows = rows;
        this.sum = sum;
    }

    private StatisticsFilterDto filter;
    private List<StatisticsRow> rows;
    private StatisticsRow sum;

    public StatisticsFilterDto getFilter() {
        return filter;
    }

    public void setFilter(StatisticsFilterDto filter) {
        this.filter = filter;
    }

    public List<StatisticsRow> getRows() {
        return rows;
    }

    public void setRows(List<StatisticsRow> rows) {
        this.rows = rows;
    }

    public StatisticsRow getSum() {
        return sum;
    }

    public void setSum(StatisticsRow sum) {
        this.sum = sum;
    }
}
