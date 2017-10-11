package ee.hm.dop.service.content;

import ee.hm.dop.dao.ChapterObjectDao;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.service.permission.PortfolioPermission;
import ee.hm.dop.service.reviewmanagement.ChangedLearningObjectService;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.TextFieldUtil;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joda.time.DateTime.now;

public class PortfolioService {

    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private ChapterObjectDao chapterObjectDao;
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private ChangedLearningObjectService changedLearningObjectService;
    @Inject
    private ReducedLearningObjectDao reducedLearningObjectDao;
    @Inject
    private PortfolioConverter portfolioConverter;
    @Inject
    private FirstReviewAdminService firstReviewAdminService;
    @Inject
    private PortfolioPermission portfolioPermission;

    public Portfolio get(long portfolioId, User loggedInUser) {
        if (UserUtil.isAdminOrModerator(loggedInUser)) {
            return portfolioDao.findById(portfolioId);
        }
        Portfolio portfolio = portfolioDao.findByIdNotDeleted(portfolioId);
        if (!portfolioPermission.canView(loggedInUser, portfolio)) {
            throw ValidatorUtil.permissionError();
        }
        return portfolio;
    }

    public SearchResult getByCreatorResult(User creator, User loggedInUser, int start, int maxResults) {
        List<Searchable> searchables = new ArrayList<>(getByCreator(creator, loggedInUser, start, maxResults));
        Long size = getCountByCreator(creator);
        return new SearchResult(searchables, size, start);
    }

    public List<ReducedLearningObject> getByCreator(User creator, User loggedInUser, int start, int maxResults) {
        return reducedLearningObjectDao.findPortfolioByCreator(creator, start, maxResults).stream()
                .filter(p -> portfolioPermission.canInteract(loggedInUser, p))
                .collect(Collectors.toList());
    }

    public Long getCountByCreator(User creator) {
        return portfolioDao.findCountByCreator(creator);
    }

    public Portfolio update(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = validateUpdate(portfolio, loggedInUser);

        TextFieldUtil.cleanTextFields(portfolio);

        originalPortfolio = portfolioConverter.setPortfolioUpdatableFields(originalPortfolio, portfolio);
        saveNewObjectsInChapters(originalPortfolio);
        originalPortfolio.setUpdated(now());

        Portfolio updatedPortfolio = portfolioDao.createOrUpdate(originalPortfolio);
        solrEngineService.updateIndex();

        processChanges(portfolio);

        return updatedPortfolio;
    }

    private void saveNewObjectsInChapters(Portfolio originalPortfolio) {
        if (originalPortfolio.getChapters() == null) return;
        originalPortfolio.getChapters().forEach(chapter -> {
            saveAndUpdateChapterObjects(chapter);
            if (chapter.getSubchapters() != null) {
                chapter.getSubchapters().forEach(this::saveAndUpdateChapterObjects);
            }
        });
    }

    private void saveAndUpdateChapterObjects(Chapter chapter) {
        if (chapter.getContentRows() == null) return;
        chapter.getContentRows().forEach(chapterRow ->
                chapterRow.getLearningObjects().replaceAll(learningObject -> {
                    if (learningObject instanceof ChapterObject) {
                        return chapterObjectDao.update((ChapterObject) learningObject);
                    } else return learningObject;
                }));
    }

    private void processChanges(Portfolio portfolio) {
        List<ChangedLearningObject> changes = changedLearningObjectService.getAllByLearningObject(portfolio.getId());
        if (CollectionUtils.isNotEmpty(changes)) {
            for (ChangedLearningObject change : changes) {
                if (!changedLearningObjectService.learningObjectHasThis(portfolio, change)) {
                    changedLearningObjectService.removeChangeById(change.getId());
                }
            }
        }
    }

    public Portfolio create(Portfolio portfolio, User creator) {
        ValidatorUtil.mustNotHaveId(portfolio);
        TextFieldUtil.cleanTextFields(portfolio);

        Portfolio safePortfolio = portfolioConverter.getPortfolioWithAllowedFieldsOnCreate(portfolio);
        saveNewObjectsInChapters(safePortfolio);

        return doCreate(safePortfolio, creator, creator);
    }

    Portfolio doCreate(Portfolio portfolio, User creator, User originalCreator) {
        portfolio.setViews(0L);
        portfolio.setCreator(creator);
        portfolio.setOriginalCreator(originalCreator);
        portfolio.setVisibility(Visibility.PRIVATE);
        portfolio.setAdded(now());

        Portfolio createdPortfolio = portfolioDao.createOrUpdate(portfolio);
        firstReviewAdminService.save(createdPortfolio);
        solrEngineService.updateIndex();

        return createdPortfolio;
    }

    private Portfolio validateUpdate(Portfolio portfolio, User loggedInUser) {
        ValidatorUtil.mustHaveId(portfolio);
        if (isEmpty(portfolio.getTitle())) {
            throw new RuntimeException("Required field title must be filled.");
        }
        Portfolio originalPortfolio = portfolioDao.findByIdNotDeleted(portfolio.getId());
        if (portfolioPermission.canUpdate(loggedInUser, originalPortfolio)) {
            return originalPortfolio;
        }
        throw ValidatorUtil.permissionError();
    }

    public Portfolio findValid(Portfolio portfolio) {
        return ValidatorUtil.findValid(portfolio, (Function<Long, Portfolio>) portfolioDao::findByIdNotDeleted);
    }

    public Portfolio findValidIncludeDeleted(Portfolio portfolio) {
        return ValidatorUtil.findValid(portfolio, (Function<Long, Portfolio>) portfolioDao::findById);
    }
}
