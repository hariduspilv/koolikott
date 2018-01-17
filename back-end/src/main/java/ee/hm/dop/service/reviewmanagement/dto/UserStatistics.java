package ee.hm.dop.service.reviewmanagement.dto;

import ee.hm.dop.model.User;

import java.util.List;

public class UserStatistics {

    private User user;
    private List<StatisticsRow> rows;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<StatisticsRow> getRows() {
        return rows;
    }

    public void setRows(List<StatisticsRow> rows) {
        this.rows = rows;
    }
}
