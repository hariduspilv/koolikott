package ee.hm.dop.service.useractions;


import com.google.inject.Inject;
import ee.hm.dop.dao.UserTourDataDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserTourData;

public class UserTourDataService {

    @Inject
    private UserTourDataDao userTourDataDao;

    public UserTourData getUserTourData(User user) {
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }

        UserTourData userTourData = userTourDataDao.getUserTourData(user);

        if (userTourData == null) {
            UserTourData newUserTourData = new UserTourData();
            newUserTourData.setUser(user);

            userTourData = userTourDataDao.createOrUpdate(newUserTourData);
        }

        return userTourData;
    }

    public UserTourData addUserTourData(UserTourData userTourData, User loggedInUser) {
        if (userTourData.getUser() == null || loggedInUser == null) {
            throw new IllegalArgumentException();
        }

        if (!userTourData.getUser().getId().equals(loggedInUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        return userTourDataDao.createOrUpdate(userTourData);
    }
}
