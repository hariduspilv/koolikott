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
        // TODO: return only objects that user is allowed to see ex if private portfolio then, don't return
        return userLikeDAO.findMostLikedSince(now().minusYears(1), maxResults);
    }
}
