package ee.hm.dop.service.useractions;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.UserFavoriteDao;
import ee.hm.dop.model.*;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.ValidatorUtil;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserFavoriteService {

    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private UserFavoriteDao userFavoriteDao;
    @Inject
    private SolrEngineService solrEngineService;

    public UserFavorite addUserFavorite(LearningObject learningObjectIn, User loggedInUser) {
        LearningObject learningObject = learningObjectService.validateAndFind(learningObjectIn);

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setAdded(LocalDateTime.now());
        userFavorite.setCreator(loggedInUser);
        userFavorite.setLearningObject(learningObject);

        UserFavorite created = userFavoriteDao.createOrUpdate(userFavorite);
        learningObjectDao.createOrUpdate(learningObject);
        solrEngineService.updateIndex();
        return created;
    }

    public void removeUserFavorite(Long id, User loggedInUser) {
        LearningObject learningObject = learningObjectDao.findById(id);
        ValidatorUtil.mustHaveId(learningObject);
        userFavoriteDao.deleteByLearningObjectAndUser(learningObject, loggedInUser);
        solrEngineService.updateIndex();
    }

    public UserFavorite hasFavorited(Long id, User loggedInUser) {
        if (id == null || loggedInUser == null) return null;
        return userFavoriteDao.findFavoriteByUserAndLearningObject(id, loggedInUser);
    }

    public SearchResult getUserFavoritesSearchResult(User loggedInUser, int start, int maxResults) {
        List<Searchable> userFavorites = new ArrayList<>(getUserFavorites(loggedInUser, start, maxResults));
        return new SearchResult(userFavorites, getUserFavoritesSize(loggedInUser), start);
    }

    private List<ReducedLearningObject> getUserFavorites(User loggedInUser, int start, int maxResult) {
        return userFavoriteDao.findUsersFavoritedLearningObjects(loggedInUser, start, maxResult);
    }

    public long getUserFavoritesSize(User loggedInUser) {
        return userFavoriteDao.findUsersFavoritedLearningObjectsCount(loggedInUser);
    }
}
