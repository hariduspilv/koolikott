package ee.hm.dop.service.content;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.model.*;
import ee.hm.dop.service.content.enums.GetMaterialStrategy;
import ee.hm.dop.service.permission.MaterialPermission;
import ee.hm.dop.utils.UrlUtil;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MaterialGetter {

    @Inject
    private MaterialDao materialDao;
    @Inject
    private ReducedLearningObjectDao reducedLearningObjectDao;
    @Inject
    private MaterialPermission materialPermission;
    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private ReducedUserService reducedUserService;

    public Material get(Long materialId, User loggedInUser) {
        if (UserUtil.isAdminOrModerator(loggedInUser)) {
            Material material = materialDao.findById(materialId);
            if (material == null) return null;
            learningObjectService.setTaxonPosition(material);
            return material;
        }

        Material material = materialDao.findByIdNotDeleted(materialId);
        if (!materialPermission.canView(loggedInUser, material)) {
            throw ValidatorUtil.permissionError();
        }
        if (material.getCreator() != null) {
            material.setCreator(reducedUserService.getMapper().convertValue(material.getCreator(), User.class));
        }
        learningObjectService.setTaxonPosition(material);
        return material;
    }

    public Material getWithoutValidation(Long materialId) {
        Material material = materialDao.findById(materialId);
        if (material.getCreator() != null) {
            material.setCreator(reducedUserService.getMapper().convertValue(material.getCreator(), User.class));
        }
        return material;
    }

    public List<Material> getBySource(String initialMaterialSource, GetMaterialStrategy getMaterialStrategy) {
        String materialSource = UrlUtil.getURLWithoutProtocolAndWWW(UrlUtil.processURL(initialMaterialSource));
        checkLink(materialSource);
        return materialDao.findBySource(materialSource, getMaterialStrategy);
    }

    public Material getAnyBySource(String initialMaterialSource, GetMaterialStrategy getMaterialStrategy) {
        String materialSource = UrlUtil.getURLWithoutProtocolAndWWW(UrlUtil.processURL(initialMaterialSource));
        checkLink(materialSource);
        return materialDao.findAnyBySource(materialSource, getMaterialStrategy);
    }

    private void checkLink(String materialSource) {
        if (materialSource == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No material source link provided");
        }
    }

    public SearchResult getByCreatorResult(User creator, User loggedInUser, int start, int maxResults) {
        List<Searchable> userFavorites = new ArrayList<>(getByCreator(creator, start, maxResults));
        return new SearchResult(userFavorites, getByCreatorSize(creator, loggedInUser), start);
    }

    private List<ReducedLearningObject> getByCreator(User creator, int start, int maxResults) {
        List<ReducedLearningObject> reducedLearningObjects = reducedLearningObjectDao.findMaterialByCreator(creator, start, maxResults);
        reducedLearningObjects
                .forEach(lo -> {
                    if (lo.getCreator() != null) {
                        lo.setCreator(reducedUserService.getMapper().convertValue(lo.getCreator(), User.class));
                    }
                });
        return reducedLearningObjects;
    }

    public long getByCreatorSize(User creator, User loggedInUser) {
        if (loggedInUser == null) {
            return materialDao.findByCreatorNotDeletedOrNotPrivateSize(creator);
        } else if (UserUtil.isAdmin(loggedInUser) || creator.getId().equals(loggedInUser.getId())) {
            return materialDao.findByCreatorSize(creator);
        } else {
            return materialDao.findByCreatorNotDeletedOrNotPrivateSize(creator);
        }
    }

}
