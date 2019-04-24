package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.TaxonPositionDao;
import ee.hm.dop.dao.firstreview.FirstReviewDao;
import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.administration.DopPage;
import ee.hm.dop.model.administration.PageableQuery;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.taxon.FirstReviewTaxon;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.TaxonDTO;
import ee.hm.dop.model.taxon.TaxonPosition;
import ee.hm.dop.utils.UserUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.joda.time.DateTime.now;

public class FirstReviewAdminService {

    @Inject
    private FirstReviewDao firstReviewDao;
    @Inject
    private TaxonPositionDao taxonPositionDao;

    public DopPage getUnReviewed(User user, PageableQuery pageableQuery) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isModerator(user)) {
            pageableQuery.setUsers(Arrays.asList(user.getId()));
        }
        List<AdminLearningObject> unreviewed = firstReviewDao.findAllUnreviewed(pageableQuery);
        Long unreviewedCount = firstReviewDao.findCoundOfAllUnreviewed(pageableQuery);
        DopPage dp = getSearchResult(unreviewed, unreviewedCount);
        dp.setPage(pageableQuery.getPage());
        dp.setSize(pageableQuery.getSize());
        dp.setTotalPages((int) (unreviewedCount / pageableQuery.getSize()));
        return dp;
    }

    private DopPage getSearchResult(List<AdminLearningObject> allUnreviewed, Long unreviewedCount) {
        for (AdminLearningObject learningObject : allUnreviewed) {
            List<FirstReviewTaxon> firstReviewTaxons = learningObject.getTaxons().stream()
                    .map(this::convert)
                    .distinct()
                    .collect(Collectors.toList());
            learningObject.setFirstReviewTaxons(firstReviewTaxons);
        }
        DopPage dopPage = new DopPage();
        dopPage.setContent(allUnreviewed);
        dopPage.setTotalElements(unreviewedCount);
        return dopPage;
    }

    private FirstReviewTaxon convert(Taxon taxon) {
        TaxonPosition tp = taxonPositionDao.findByTaxon(taxon);
        if (tp == null){
            return null;
        }
        return new FirstReviewTaxon(toDto(tp.getEducationalContext()), toDto(tp.getDomain()), toDto(tp.getSubject()));
    }

    private TaxonDTO toDto(Taxon taxon) {
        if (taxon == null) return null;
        return new TaxonDTO(taxon.getId(), taxon.getName(), taxon.getTranslationKey());
    }

    public Long getUnReviewedCount(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return firstReviewDao.findCountOfUnreviewed();
        } else {
            return firstReviewDao.findCountOfUnreviewed(user);
        }
    }

    public FirstReview save(LearningObject learningObject) {
        learningObject.setUnReviewed(learningObject.getUnReviewed() + 1);
        FirstReview firstReview = new FirstReview();
        firstReview.setLearningObject(learningObject);
        firstReview.setReviewed(false);
        firstReview.setCreatedAt(now());
        return firstReviewDao.createOrUpdate(firstReview);
    }

    public void setReviewed(LearningObject learningObject, User loggedInUser, ReviewStatus reviewStatus) {
        for (FirstReview firstReview : learningObject.getFirstReviews()) {
            if (!firstReview.isReviewed()) {
                firstReview.setReviewedAt(DateTime.now());
                firstReview.setReviewedBy(loggedInUser);
                firstReview.setReviewed(true);
                firstReview.setStatus(reviewStatus);
                firstReviewDao.createOrUpdate(firstReview);
                learningObject.setUnReviewed(learningObject.getUnReviewed() - 1);
            }
        }
    }
}
