package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.FirstReviewDao;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.dao.TaxonPositionDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.administration.PageableQuery;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.taxon.FirstReviewTaxon;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.TaxonDTO;
import ee.hm.dop.model.taxon.TaxonPosition;
import ee.hm.dop.utils.UserUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.joda.time.DateTime.now;

public class FirstReviewAdminService {

    @Inject
    private FirstReviewDao firstReviewDao;
    @Inject
    private TaxonDao taxonDao;
    @Inject
    private TaxonPositionDao taxonPositionDao;

    public List<AdminLearningObject> getUnReviewed(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return firstReviewDao.findAllUnreviewed();
        } else {
            return firstReviewDao.findAllUnreviewed(user);
        }
    }

    public SearchResult getUnReviewed(User user, PageableQuery pageableQuery) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {

            List<AdminLearningObject> allUnreviewed = firstReviewDao.findAllUnreviewed(pageableQuery);

            for (AdminLearningObject learningObject : allUnreviewed) {
                for (Taxon taxon : learningObject.getTaxons()) {
                    TaxonPosition dao = taxonPositionDao.findByTaxon(taxon);
                    FirstReviewTaxon firstReviewTaxon = new FirstReviewTaxon(toDto(dao.getEducationalContext()), toDto(dao.getDomain()), toDto(dao.getSubject()));
                    learningObject.getFirstReviewTaxons().add(firstReviewTaxon);
                }
                List<FirstReviewTaxon> collect = learningObject.getFirstReviewTaxons().stream().distinct().collect(Collectors.toList());
                learningObject.setFirstReviewTaxons(collect);

            }

            SearchResult searchResult = new SearchResult();
            searchResult.setItems(allUnreviewed);
            searchResult.setTotalResults(allUnreviewed.size());

            return searchResult;
        }

        else {

            SearchResult result = new SearchResult();
            result.setItems(firstReviewDao.findAllUnreviewed(user, pageableQuery));
            result.setTotalResults((firstReviewDao.findAllUnreviewed(user, pageableQuery)).size());
            return result;
        }
    }

    private TaxonDTO toDto(Taxon taxon) {
        if (taxon == null) return null;
        return new TaxonDTO(taxon.getId(), taxon.getName(), taxon.getTranslationKey());
    }

    public long getUnReviewedCount(User user) {
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
