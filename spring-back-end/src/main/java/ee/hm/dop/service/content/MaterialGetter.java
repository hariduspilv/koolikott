package ee.hm.dop.service.content;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.model.*;
import ee.hm.dop.service.content.enums.GetMaterialStrategy;
import ee.hm.dop.service.permission.MaterialPermission;
import ee.hm.dop.utils.UrlUtil;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Service
public class MaterialGetter {

    @Inject
    private MaterialDao materialDao;
    @Inject
    private ReducedLearningObjectDao reducedLearningObjectDao;
    @Inject
    private MaterialPermission materialPermission;

    public Material get(Long materialId, User loggedInUser) {
        if (UserUtil.isAdminOrModerator(loggedInUser)) {
            return materialDao.findById(materialId);
        }
        Material material = materialDao.findByIdNotDeleted(materialId);
        if (!materialPermission.canView(loggedInUser, material)) {
            throw ValidatorUtil.permissionError();
        }
        return material;
    }

    public Material getWithoutValidation(Long materialId) {
        return materialDao.findById(materialId);
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
            throw new WebApplicationException("No material source link provided", Response.Status.BAD_REQUEST);
        }
    }

    public SearchResult getByCreatorResult(User creator, int start, int maxResults) {
        List<Searchable> userFavorites = new ArrayList<>(getByCreator(creator, start, maxResults));
        return new SearchResult(userFavorites, getByCreatorSize(creator), start);
    }

    private List<ReducedLearningObject> getByCreator(User creator, int start, int maxResults) {
        return reducedLearningObjectDao.findMaterialByCreator(creator, start, maxResults);
    }

    public long getByCreatorSize(User creator) {
        return materialDao.findByCreatorSize(creator);
    }
}
