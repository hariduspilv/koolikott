package ee.hm.dop.service.reviewmanagement.newdto;

import ee.hm.dop.model.User;

import java.util.List;

/**
 * why for?
 */
@Deprecated
public class NewUserStatistics {

    private User user;
    private List<NewStatisticsRow> rows;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<NewStatisticsRow> getRows() {
        return rows;
    }

    public void setRows(List<NewStatisticsRow> rows) {
        this.rows = rows;
    }

}
