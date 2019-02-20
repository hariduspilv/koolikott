package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.firstreview.FirstReviewDao;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.dao.TaxonPositionDao;
import ee.hm.dop.dao.firstreview.FirstReviewOldDao;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.joda.time.DateTime.now;

public class FirstReviewAdminService {

    @Inject
    private FirstReviewDao firstReviewDao;
    @Inject
    private FirstReviewOldDao firstReviewOldDao;
    @Inject
    private TaxonPositionDao taxonPositionDao;

    @Deprecated
    public List<AdminLearningObject> getUnReviewed(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return firstReviewOldDao.findAllUnreviewed();
        } else {
            return firstReviewOldDao.findAllUnreviewed(user);
        }
    }

    public SearchResult getUnReviewed(User user, PageableQuery pageableQuery) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return getSearchResult(firstReviewDao.findAllUnreviewed(pageableQuery));
        } else {
            return getSearchResult(firstReviewDao.findAllUnreviewed(user, pageableQuery));
        }
    }

    private SearchResult getSearchResult(List<AdminLearningObject> allUnreviewed) {
        for (AdminLearningObject learningObject : allUnreviewed) {
            List<FirstReviewTaxon> firstReviewTaxons = learningObject.getTaxons().stream()
                    .map(this::convert)
                    .distinct()
                    .collect(Collectors.toList());
            learningObject.setFirstReviewTaxons(firstReviewTaxons);
        }

        SearchResult searchResult = new SearchResult();
        searchResult.setItems(allUnreviewed);
        //todo totalresults is wrong
        searchResult.setTotalResults(allUnreviewed.size());
        return searchResult;
    }

    private FirstReviewTaxon convert(Taxon taxon) {
        TaxonPosition tp = taxonPositionDao.findByTaxon(taxon);
        return new FirstReviewTaxon(toDto(tp.getEducationalContext()), toDto(tp.getDomain()), toDto(tp.getSubject()));
    }

    private TaxonDTO toDto(Taxon taxon) {
        if (taxon == null) return null;
        return new TaxonDTO(taxon.getId(), taxon.getName(), taxon.getTranslationKey());
    }

    public long getUnReviewedCount(User user,PageableQuery query) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return firstReviewDao.findCountOfUnreviewed(query);
        } else {
            return firstReviewDao.findCountOfUnreviewed(Arrays.asList(user.getId().toString()));
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
