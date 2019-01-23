package ee.hm.dop.service.reviewmanagement.dto;

public class StatisticsQuery {
    private Long userId;
    private Long count;

    public StatisticsQuery() {
    }

    public StatisticsQuery(Long userId, Long count) {
        this.userId = userId;
        this.count = count;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
