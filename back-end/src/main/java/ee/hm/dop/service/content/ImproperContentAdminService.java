package ee.hm.dop.service.content;

import ee.hm.dop.dao.ImproperContentDao;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.utils.UserUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class ImproperContentAdminService {

    @Inject
    private ImproperContentDao improperContentDao;
    @Inject
    private ImproperContentService improperContentService;

    public List<ImproperContent> getImproperMaterials(User user) {
        return improperContentService.getAll(user).stream()
                .filter(imp -> imp.getLearningObject() instanceof Material)
                .collect(Collectors.toList());
    }

    public List<ImproperContent> getImproperPortfolios(User user) {
        return improperContentService.getAll(user).stream()
                .filter(imp -> imp.getLearningObject() instanceof Portfolio && !imp.getLearningObject().isDeleted())
                .collect(Collectors.toList());
    }

    public long getImproperMaterialSize(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        return improperContentDao.getImproperMaterialCount();
    }

    public long getImproperPortfolioSize(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        return improperContentDao.getImproperPortfolioCount();
    }
}
