package ee.hm.dop.service;

import static org.joda.time.DateTime.now;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.UserLikeDAO;
import ee.hm.dop.model.Searchable;

public class UserLikeService {

    @Inject
    private UserLikeDAO userLikeDAO;

    public List<Searchable> getMostLiked(int maxResults) {
        return userLikeDAO.findMostLikedSince(now().minusYears(1), maxResults);
    }
}
