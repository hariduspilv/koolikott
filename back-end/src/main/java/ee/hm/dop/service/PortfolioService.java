package ee.hm.dop.service;

import ee.hm.dop.dao.ChapterObjectDAO;
import ee.hm.dop.dao.PortfolioDAO;
import ee.hm.dop.dao.ReducedLearningObjectDAO;
import ee.hm.dop.dao.UserLikeDAO;
import ee.hm.dop.model.ChangedLearningObject;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.ChapterObject;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.ReducedPortfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLike;
import ee.hm.dop.model.Visibility;
import ee.hm.dop.service.learningObject.LearningObjectHandler;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joda.time.DateTime.now;

public class PortfolioService extends BaseService implements LearningObjectHandler {

    @Inject
    private PortfolioDAO portfolioDAO;

    @Inject
    private UserLikeDAO userLikeDAO;

    @Inject
    private ChapterObjectDAO chapterObjectDAO;

    @Inject
    private SolrEngineService solrEngineService;

    @Inject
    private ChangedLearningObjectService changedLearningObjectService;

    @Inject
    private ReducedLearningObjectDAO reducedLearningObjectDAO;

    private static final int MAX_DESCRIPTION_LENGTH = 850;

    public Portfolio get(long portfolioId, User loggedInUser) {
        Portfolio portfolio;
        if (isUserAdmin(loggedInUser) || isUserModerator(loggedInUser)) {
            portfolio = portfolioDAO.findByIdFromAll(portfolioId);
        } else {
            portfolio = portfolioDAO.findByIdNotDeleted(portfolioId);

            if (!hasPermissionsToView(loggedInUser, portfolio)) {
                throw new RuntimeException("Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.");
            }
        }

        return portfolio;
    }

    public List<ReducedLearningObject> getByCreator(User creator, User loggedInUser, int start, int maxResults) {
        return reducedLearningObjectDAO.findPortfolioByCreator(creator, start, maxResults)
                .stream()
                .filter(p -> hasPermissionsToAccess(loggedInUser, p))
                .collect(Collectors.toList());
    }

    public Long getCountByCreator(User creator) {
        return portfolioDAO.findCountByCreator(creator);
    }

    public void incrementViewCount(Portfolio portfolio) {
        Portfolio originalPortfolio = portfolioDAO.findByIdFromAll(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }

        portfolioDAO.incrementViewCount(originalPortfolio);
        solrEngineService.updateIndex();
    }

    public void addComment(Comment comment, Portfolio portfolio, User loggedInUser) {
        if (isEmpty(comment.getText()) || comment.getId() != null)
            throw new RuntimeException("Comment is missing text or already exists.");

        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());

        if (!hasPermissionsToView(loggedInUser, originalPortfolio)) {
            throw new RuntimeException("Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.");
        }

        comment.setAdded(DateTime.now());
        originalPortfolio.getComments().add(comment);
        portfolioDAO.update(originalPortfolio);
    }

    public UserLike addUserLike(Portfolio portfolio, User loggedInUser, boolean isLiked) {
        if (portfolio == null || portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }
        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());

        if (!hasPermissionsToView(loggedInUser, originalPortfolio)) {
            throw new RuntimeException("Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.");
        }

        userLikeDAO.deletePortfolioLike(originalPortfolio, loggedInUser);

        UserLike like = new UserLike();
        like.setLearningObject(originalPortfolio);
        like.setCreator(loggedInUser);
        like.setLiked(isLiked);
        like.setAdded(DateTime.now());

        return userLikeDAO.update(like);
    }

    public void removeUserLike(Portfolio portfolio, User loggedInUser) {
        if (portfolio == null || portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }
        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());

        if (!hasPermissionsToView(loggedInUser, originalPortfolio)) {
            throw new RuntimeException("Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.");
        }

        userLikeDAO.deletePortfolioLike(originalPortfolio, loggedInUser);
    }

    public UserLike getUserLike(Portfolio portfolio, User loggedInUser) {

        if (portfolio == null || portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }
        Portfolio originalPortfolio = portfolioDAO.findByIdFromAll(portfolio.getId());

        if (!hasPermissionsToView(loggedInUser, originalPortfolio)) {
            throw new RuntimeException("Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.");
        }

        return userLikeDAO.findPortfolioUserLike(originalPortfolio, loggedInUser);
    }

    public Recommendation addRecommendation(Portfolio portfolio, User loggedInUser) {
        if (portfolio == null || portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }

        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());
        if (originalPortfolio == null || !isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Portfolio not found or user is not admin");
        }

        Recommendation recommendation = new Recommendation();
        recommendation.setCreator(loggedInUser);
        recommendation.setAdded(DateTime.now());

        originalPortfolio.setRecommendation(recommendation);

        originalPortfolio = (Portfolio) portfolioDAO.update(originalPortfolio);
        solrEngineService.updateIndex();

        return originalPortfolio.getRecommendation();
    }

    public void removeRecommendation(Portfolio portfolio, User loggedInUser) {
        if (portfolio == null || portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }

        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());
        if (originalPortfolio == null || !isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Portfolio not found or user is not admin");
        }

        originalPortfolio.setRecommendation(null);

        portfolioDAO.update(originalPortfolio);
        solrEngineService.updateIndex();
    }

    public Portfolio create(Portfolio portfolio, User creator) {
        if (portfolio.getId() != null) {
            throw new RuntimeException("Portfolio already exists.");
        }

        validateDescriptions(portfolio);
        cleanTextFields(portfolio);

        Portfolio safePortfolio = getPortfolioWithAllowedFieldsOnCreate(portfolio);
        saveNewObjectsInChapters(safePortfolio);

        return doCreate(safePortfolio, creator, creator);
    }

    public Portfolio update(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = validateUpdate(portfolio, loggedInUser);

        validateDescriptions(portfolio);
        cleanTextFields(portfolio);

        originalPortfolio = setPortfolioUpdatableFields(originalPortfolio, portfolio);
        saveNewObjectsInChapters(originalPortfolio);
        originalPortfolio.setUpdated(now());

        Portfolio updatedPortfolio = (Portfolio) portfolioDAO.update(originalPortfolio);
        solrEngineService.updateIndex();

        processChanges(portfolio);

        return updatedPortfolio;
    }

    private void validateDescriptions(Portfolio portfolio) {
        if (portfolio.getSummary() != null) {
            if (portfolio.getSummary().length() > MAX_DESCRIPTION_LENGTH) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void cleanTextFields(Portfolio portfolio) {
        String regex = "[^\\u0000-\\uFFFF]";
        String replacement = "\uFFFD";

        if (portfolio.getTitle() != null)
            portfolio.setTitle(portfolio.getTitle().replaceAll(regex, replacement));

        if (portfolio.getSummary() != null)
            portfolio.setSummary(portfolio.getSummary().replaceAll(regex, replacement));
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
        chapter.getContentRows().forEach(chapterRow -> chapterRow.getLearningObjects().replaceAll(learningObject -> {
            if (learningObject instanceof ChapterObject) {
                return chapterObjectDAO.update((ChapterObject) learningObject);
            } else return learningObject;
        }));
    }

    private void processChanges(Portfolio portfolio) {
        List<ChangedLearningObject> changes = changedLearningObjectService.getAllByLearningObject(portfolio.getId());
        if (changes == null || changes.isEmpty()) return;

        for (ChangedLearningObject change : changes) {
            if (!changedLearningObjectService.learningObjectHasThis(portfolio, change)) {
                changedLearningObjectService.removeChangeById(change.getId());
            }
        }
    }

    public Portfolio copy(Portfolio portfolio, User loggedInUser) {
        if (portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }

        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());

        if (!hasPermissionsToView(loggedInUser, originalPortfolio)) {
            throw new RuntimeException("Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.");
        }

        Portfolio copy = getPortfolioWithAllowedFieldsOnCreate(originalPortfolio);
        copy.setChapters(copyChapters(originalPortfolio.getChapters()));

        return doCreate(copy, loggedInUser, originalPortfolio.getCreator());
    }

    public void delete(Portfolio portfolio, User loggedInUser) {
        if (portfolio.getId() == null) {
            throw new RuntimeException("Portfolio must already exist.");
        }

        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());

        if (!hasPermissionsToUpdate(loggedInUser, originalPortfolio)) {
            throw new RuntimeException("Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.");
        }

        portfolioDAO.delete(originalPortfolio);
        solrEngineService.updateIndex();
    }

    public void restore(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = portfolioDAO.findDeletedById(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }

        if (!isUserAdminOrModerator(loggedInUser)) {
            throw new RuntimeException("Logged in user must be an administrator.");
        }

        portfolioDAO.restore(originalPortfolio);
        solrEngineService.updateIndex();
    }

    public List<Portfolio> getDeletedPortfolios() {
        return portfolioDAO.findDeletedPortfolios();
    }

    public Long getDeletedPortfoliosCount() {
        return portfolioDAO.findDeletedPortfoliosCount();
    }

    private Portfolio validateUpdate(Portfolio portfolio, User loggedInUser) {
        if (portfolio.getId() == null) {
            throw new RuntimeException("Portfolio must already exist.");
        }

        if (isEmpty(portfolio.getTitle())) {
            throw new RuntimeException("Required field title must be filled.");
        }

        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());

        if (!hasPermissionsToUpdate(loggedInUser, originalPortfolio)) {
            throw new RuntimeException("Object does not exist or the user that is updating must be logged in user must be the creator, administrator or moderator.");
        }

        return originalPortfolio;
    }

    private Portfolio doCreate(Portfolio portfolio, User creator, User originalCreator) {
        portfolio.setViews(0L);
        portfolio.setCreator(creator);
        portfolio.setOriginalCreator(originalCreator);
        portfolio.setVisibility(Visibility.PRIVATE);
        portfolio.setAdded(now());

        Portfolio createdPortfolio = (Portfolio) portfolioDAO.update(portfolio);
        solrEngineService.updateIndex();

        return createdPortfolio;
    }

    private List<Chapter> copyChapters(List<Chapter> chapters) {
        List<Chapter> copyChapters = new ArrayList<>();

        if (chapters != null) {
            for (Chapter chapter : chapters) {
                Chapter copy = new Chapter();
                copy.setTitle(chapter.getTitle());
                copy.setText(chapter.getText());
                copy.setContentRows(chapter.getContentRows());
                copy.setSubchapters(copyChapters(chapter.getSubchapters()));

                copyChapters.add(copy);
            }
        }

        return copyChapters;
    }

    private Portfolio getPortfolioWithAllowedFieldsOnCreate(Portfolio portfolio) {
        Portfolio safePortfolio = new Portfolio();
        safePortfolio.setTitle(portfolio.getTitle());
        safePortfolio.setSummary(portfolio.getSummary());
        safePortfolio.setTags(portfolio.getTags());
        safePortfolio.setTargetGroups(portfolio.getTargetGroups());
        safePortfolio.setTaxons(portfolio.getTaxons());
        safePortfolio.setChapters(portfolio.getChapters());
        safePortfolio.setPicture(portfolio.getPicture());

        return safePortfolio;
    }

    private Portfolio setPortfolioUpdatableFields(Portfolio originalPortfolio, Portfolio portfolio) {
        originalPortfolio.setTitle(portfolio.getTitle());
        originalPortfolio.setSummary(portfolio.getSummary());
        originalPortfolio.setTags(portfolio.getTags());
        originalPortfolio.setTargetGroups(portfolio.getTargetGroups());
        originalPortfolio.setTaxons(portfolio.getTaxons());
        originalPortfolio.setChapters(portfolio.getChapters());
        originalPortfolio.setVisibility(portfolio.getVisibility());
        originalPortfolio.setPicture(portfolio.getPicture());
        return originalPortfolio;
    }

    private boolean hasPermissionsToView(User loggedInUser, Portfolio portfolio) {
        return isPublic(portfolio) || isNotListed(portfolio) || isUserAdminOrModerator(loggedInUser) || isUserCreator(portfolio, loggedInUser);
    }

    private boolean hasPermissionsToAccess(User user, ReducedLearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof ReducedPortfolio)) return false;
        ReducedPortfolio portfolio = (ReducedPortfolio) learningObject;

        return isPublic(portfolio) || isUserAdminOrModerator(user) || isUserCreator(portfolio, user);
    }

    @Override
    public boolean hasPermissionsToAccess(User user, LearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof Portfolio)) return false;
        Portfolio portfolio = (Portfolio) learningObject;

        return isPublic(learningObject) || isUserAdminOrModerator(user) || isUserCreator(portfolio, user);
    }

    @Override
    public boolean hasPermissionsToUpdate(User user, LearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof Portfolio)) return false;
        Portfolio portfolio = (Portfolio) learningObject;

        return isUserAdminOrModerator(user) || isUserCreator(portfolio, user);
    }

    private boolean isUserCreator(ReducedPortfolio reducedPortfolio, User user) {
        return user != null && reducedPortfolio.getCreator().getId().equals(user.getId());
    }

    private boolean isPublic(ReducedPortfolio reducedPortfolio) {
        return reducedPortfolio.getVisibility() == Visibility.PUBLIC && !reducedPortfolio.isDeleted();
    }

    @Override
    public boolean isPublic(LearningObject learningObject) {
        return ((Portfolio) learningObject).getVisibility() == Visibility.PUBLIC && !learningObject.isDeleted();
    }

    private boolean isNotListed(LearningObject learningObject) {
        return ((Portfolio) learningObject).getVisibility() == Visibility.NOT_LISTED && !learningObject.isDeleted();
    }
}
