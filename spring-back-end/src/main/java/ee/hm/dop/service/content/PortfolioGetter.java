package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.PortfolioLogDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.CopiedLOStatus;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.service.permission.PortfolioPermission;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class PortfolioGetter {

    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private ReducedLearningObjectDao reducedLearningObjectDao;
    @Inject
    private PortfolioPermission portfolioPermission;
    @Inject
    private PortfolioLogDao portfolioLogDao;
    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private ReducedUserService reducedUserService;

    public Portfolio get(Long portfolioId, User loggedInUser) {
        if (UserUtil.isAdminOrModerator(loggedInUser)) {
            Portfolio portfolio = portfolioDao.findById(portfolioId);
            if (portfolio == null) return null;
            learningObjectService.setTaxonPosition(portfolio);
            if (portfolio.isCopy()) {
                return findCopiedRelated(portfolio);
            }
            return portfolio;
        }
        Portfolio portfolio = portfolioDao.findByIdNotDeleted(portfolioId);
        if (!portfolioPermission.canView(loggedInUser, portfolio)) {
            throw ValidatorUtil.permissionError();
        }
        learningObjectService.setTaxonPosition(portfolio);
        if (portfolio.isCopy()) {
            return findCopiedRelated(portfolio);
        }
        hideSensitiveInfoPortfolio(portfolio);
        return portfolio;
    }

    public SearchResult getByCreatorResult(User creator, User loggedInUser, int start, int maxResults) {
        List<Searchable> searchables = new ArrayList<>(getByCreator(creator, loggedInUser, start, maxResults));
        Long size = getCountByCreator(creator);
        return new SearchResult(searchables, size, start);
    }

    public List<PortfolioLog> getPortfolioHistoryAll(Long portfolioId) {
        return portfolioLogDao.findAllPortfolioLogsByLoId(portfolioId);
    }

    public List<PortfolioLog> findByIdAndCreatorAllPortfolioLogs(Long portfolioId, User user) {
        return portfolioLogDao.findPortfolioLogsByLoIdAndUserId(portfolioId, user);
    }

    public PortfolioLog getPortfolioHistory(long portfolioHistoryId) {
        return portfolioLogDao.findById(portfolioHistoryId);
    }

    public List<ReducedLearningObject> getByCreator(User creator, User loggedInUser, int start, int maxResults) {
        return reducedLearningObjectDao.findPortfolioByCreator(creator, start, maxResults).stream()
                .filter(p -> portfolioPermission.canInteract(loggedInUser, p))
                .collect(toList());
    }

    public Long getCountByCreator(User creator) {
        return portfolioDao.findCountByCreator(creator);
    }

    public Portfolio findValid(Portfolio portfolio) {
        return ValidatorUtil.findValid(portfolio, (Function<Long, Portfolio>) portfolioDao::findByIdNotDeleted);
    }

    private Boolean isDeletedOrNotPublic(LearningObject learningObject) {
        return learningObject.isDeleted() || learningObject.getVisibility() != Visibility.PUBLIC;
    }

    private CopiedLOStatus getDeletedOrNotPublic(LearningObject learningObject) {
        if (learningObject.isDeleted()) return CopiedLOStatus.DELETED;
        else if (learningObject.getVisibility() != Visibility.PUBLIC) return CopiedLOStatus.PRIVATE;
        return null;
    }

    public Portfolio findCopiedRelated(Portfolio portfolio) {
        if (portfolio.getCopiedFromDirect() != null) {
            LearningObject loCopiedFromDirectly = learningObjectDao.findById(portfolio.getCopiedFromDirect());
            if (loCopiedFromDirectly != null) {
                portfolio.setCopiedFromDirectName(loCopiedFromDirectly.getCreator().getFullName());
                portfolio.setDeletedOrNotPublic(isDeletedOrNotPublic(loCopiedFromDirectly));
                portfolio.setCopiedLOStatus(getDeletedOrNotPublic(loCopiedFromDirectly));
            }
        }
        return portfolio;
    }

    private void hideSensitiveInfoPortfolio(Portfolio portfolio) {
        if (portfolio.getCreator() != null) {
            portfolio.setCreator(reducedUserService.getMapper().convertValue(portfolio.getCreator(), User.class));
        }
        if (portfolio.getChapters() != null) {
            portfolio.getChapters()
                    .forEach(chapter ->  {
                        if (chapter.getContentRows() != null) {
                            chapter.getContentRows()
                                    .forEach(cr -> {
                                        if (cr.getLearningObjects() != null) {
                                            cr.getLearningObjects()
                                                    .forEach(lo -> {
                                                        if (lo.getCreator() != null) {
                                                            lo.setCreator(reducedUserService.getMapper().convertValue(lo.getCreator(), User.class));
                                                        }
                                                    });
                                        }
                                    });
                        }

                    });
        }
        if (portfolio.getOriginalCreator() != null) {
            portfolio.setOriginalCreator(reducedUserService.getMapper().convertValue(portfolio.getOriginalCreator(), User.class));
        }
    }
}
