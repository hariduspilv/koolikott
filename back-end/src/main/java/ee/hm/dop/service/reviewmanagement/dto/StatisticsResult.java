package ee.hm.dop.service.reviewmanagement.dto;

import java.util.List;

public class StatisticsResult {

    public StatisticsResult() {
    }

    public StatisticsResult(StatisticsFilterDto filter, List<UserStatistics> rows, StatisticsRow sum) {
        this.filter = filter;
        this.rows = rows;
        this.sum = sum;
    }

    private StatisticsFilterDto filter;
    private List<UserStatistics> rows;
    private StatisticsRow sum;

    public StatisticsFilterDto getFilter() {
        return filter;
    }

    public void setFilter(StatisticsFilterDto filter) {
        this.filter = filter;
    }

    public List<UserStatistics> getRows() {
        return rows;
    }

    public void setRows(List<UserStatistics> rows) {
        this.rows = rows;
    }

    public StatisticsRow getSum() {
        return sum;
    }

    public void setSum(StatisticsRow sum) {
        this.sum = sum;
    }
}
