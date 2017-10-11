package ee.hm.dop.service.content;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.model.*;
import ee.hm.dop.service.author.AuthorService;
import ee.hm.dop.service.author.PublisherService;
import ee.hm.dop.service.content.enums.GetMaterialStrategy;
import ee.hm.dop.service.metadata.CrossCurricularThemeService;
import ee.hm.dop.service.metadata.KeyCompetenceService;
import ee.hm.dop.service.reviewmanagement.ChangedLearningObjectService;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.useractions.PeerReviewService;
import ee.hm.dop.utils.UrlUtil;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.apache.commons.configuration.Configuration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MaterialGetter {

    @Inject
    private MaterialDao materialDao;
    @Inject
    private ReducedLearningObjectDao reducedLearningObjectDao;

    public Material get(Long materialId, User loggedInUser) {
        if (UserUtil.isAdminOrModerator(loggedInUser)) {
            return materialDao.findById(materialId);
        }
        return materialDao.findByIdNotDeleted(materialId);
    }

    public Material validateAndFindNotDeleted(Material material) {
        return ValidatorUtil.findValid(material, (Function<Long, Material>) materialDao::findByIdNotDeleted);
    }

    public List<Material> getBySource(String materialSource, GetMaterialStrategy getMaterialStrategy) {
        materialSource = UrlUtil.getURLWithoutProtocolAndWWW(UrlUtil.processURL(materialSource));
        checkLink(materialSource);
        return materialDao.findBySource(materialSource, getMaterialStrategy);
    }

    public Material getOneBySource(String materialSource, GetMaterialStrategy getMaterialStrategy) {
        materialSource = UrlUtil.getURLWithoutProtocolAndWWW(UrlUtil.processURL(materialSource));
        checkLink(materialSource);
        return materialDao.findOneBySource(materialSource, getMaterialStrategy);
    }

    private void checkLink(String materialSource) {
        if (materialSource == null) {
            throw new RuntimeException("No material source link provided");
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
