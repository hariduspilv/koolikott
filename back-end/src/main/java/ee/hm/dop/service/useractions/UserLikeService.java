package ee.hm.dop.service.useractions;

import static org.joda.time.DateTime.now;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.UserLikeDao;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLike;
import ee.hm.dop.service.content.MaterialService;
import org.joda.time.DateTime;

public class UserLikeService {

    @Inject
    private UserLikeDao userLikeDao;
    @Inject
    private MaterialService materialService;

    public List<Searchable> getMostLiked(int maxResults) {
        // TODO: return only objects that user is allowed to see ex if private portfolio then, don't return
        return userLikeDao.findMostLikedSince(now().minusYears(1), maxResults);
    }

    public UserLike addUserLike(Material material, User loggedInUser, boolean isLiked) {
        Material originalMaterial = materialService.validateAndFind(material);

        userLikeDao.deleteMaterialLike(originalMaterial, loggedInUser);

        UserLike like = new UserLike();
        like.setLearningObject(originalMaterial);
        like.setCreator(loggedInUser);
        like.setLiked(isLiked);
        like.setAdded(DateTime.now());

        return userLikeDao.update(like);
    }
}
