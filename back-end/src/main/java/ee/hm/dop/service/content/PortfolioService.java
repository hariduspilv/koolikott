package ee.hm.dop.service.content;

import ee.hm.dop.dao.ChapterObjectDao;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.model.ChangedLearningObject;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.ChapterObject;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.model.interfaces.ILearningObject;
import ee.hm.dop.model.interfaces.IPortfolio;
import ee.hm.dop.service.learningObject.PermissionItem;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.TextFieldUtil;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joda.time.DateTime.now;

public class PortfolioService implements PermissionItem {

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
    private FirstReviewService firstReviewService;

    public Portfolio get(long portfolioId, User loggedInUser) {
        if (UserUtil.isUserAdminOrModerator(loggedInUser)) {
            return portfolioDao.findById(portfolioId);
        }
        Portfolio portfolio = portfolioDao.findByIdNotDeleted(portfolioId);
        if (!canView(loggedInUser, portfolio)) {
            throw ValidatorUtil.permissionError();
        }
        return portfolio;
    }

    public List<ReducedLearningObject> getByCreator(User creator, User loggedInUser, int start, int maxResults) {
        return reducedLearningObjectDao.findPortfolioByCreator(creator, start, maxResults).stream()
                .filter(p -> canAccess(loggedInUser, p))
                .collect(Collectors.toList());
    }

    public Long getCountByCreator(User creator) {
        return portfolioDao.findCountByCreator(creator);
    }

    public void incrementViewCount(Portfolio portfolio) {
        Portfolio originalPortfolio = portfolioDao.findById(portfolio.getId());
        ValidatorUtil.mustHaveEntity(originalPortfolio);

        portfolioDao.incrementViewCount(originalPortfolio);
        solrEngineService.updateIndex();
    }

    public void addComment(Comment comment, Portfolio portfolio, User loggedInUser) {
        if (isEmpty(comment.getText()) || comment.getId() != null)
            throw new RuntimeException("Comment is missing text or already exists.");

        Portfolio originalPortfolio = portfolioDao.findByIdNotDeleted(portfolio.getId());

        if (!canView(loggedInUser, originalPortfolio)) {
            throw ValidatorUtil.permissionError();
        }

        comment.setAdded(DateTime.now());
        originalPortfolio.getComments().add(comment);
        portfolioDao.createOrUpdate(originalPortfolio);
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

    public void delete(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = findValid(portfolio);

        if (!canUpdate(loggedInUser, originalPortfolio)) {
            throw ValidatorUtil.permissionError();
        }

        portfolioDao.delete(originalPortfolio);
        solrEngineService.updateIndex();
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
        firstReviewService.save(createdPortfolio);
        solrEngineService.updateIndex();

        return createdPortfolio;
    }

    private Portfolio validateUpdate(Portfolio portfolio, User loggedInUser) {
        ValidatorUtil.mustHaveId(portfolio);
        if (isEmpty(portfolio.getTitle())) {
            throw new RuntimeException("Required field title must be filled.");
        }
        Portfolio originalPortfolio = portfolioDao.findByIdNotDeleted(portfolio.getId());
        if (canUpdate(loggedInUser, originalPortfolio)) {
            return originalPortfolio;
        }
        throw ValidatorUtil.permissionError();
    }

    @Override
    public boolean canView(User loggedInUser, ILearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof IPortfolio)) return false;
        return isNotPrivate(learningObject) || UserUtil.isUserAdminOrModerator(loggedInUser) || UserUtil.isUserCreator(learningObject, loggedInUser);
    }

    @Override
    public boolean canAccess(User user, ILearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof IPortfolio)) return false;
        return isPublic(learningObject) || UserUtil.isUserAdminOrModerator(user) || UserUtil.isUserCreator(learningObject, user);
    }

    @Override
    public boolean canUpdate(User user, ILearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof IPortfolio)) return false;
        return UserUtil.isUserAdminOrModerator(user) || UserUtil.isUserCreator(learningObject, user);
    }

    @Override
    public boolean isPublic(ILearningObject learningObject) {
        return ((IPortfolio) learningObject).getVisibility() == Visibility.PUBLIC && !learningObject.isDeleted();
    }

    public Portfolio findValid(Portfolio portfolio) {
        return ValidatorUtil.findValid(portfolio, (Function<Long, Portfolio>) portfolioDao::findByIdNotDeleted);
    }

    private boolean isNotPrivate(ILearningObject learningObject) {
        return ((IPortfolio) learningObject).getVisibility().isNotPrivate() && !learningObject.isDeleted();
    }

}
