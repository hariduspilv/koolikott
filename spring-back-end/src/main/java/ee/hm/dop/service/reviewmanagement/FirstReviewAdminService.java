package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.dao.TaxonPositionDao;
import ee.hm.dop.dao.firstreview.FirstReviewDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.administration.PageableQueryUnreviewed;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.taxon.FirstReviewTaxon;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.TaxonDTO;
import ee.hm.dop.model.taxon.TaxonPosition;
import ee.hm.dop.utils.UserUtil;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FirstReviewAdminService {

    @Inject
    private FirstReviewDao firstReviewDao;
    @Inject
    private TaxonPositionDao taxonPositionDao;
    @Inject
    private TaxonDao taxonDao;

    public SearchResult getUnReviewed(User user, PageableQueryUnreviewed pageableQuery) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isModerator(user)) {
            pageableQuery.setUsers(Arrays.asList(user.getId()));
        }
        List<AdminLearningObject> unreviewed = firstReviewDao.findAllUnreviewed(pageableQuery);
        Long unreviewedCount = firstReviewDao.findCoundOfAllUnreviewed(pageableQuery);
        return getSearchResult(unreviewed, unreviewedCount);
    }

    private SearchResult getSearchResult(List<AdminLearningObject> allUnreviewed, Long unreviewedCount) {
        for (AdminLearningObject learningObject : allUnreviewed) {
            List<FirstReviewTaxon> firstReviewTaxons = learningObject.getTaxons().stream()
                    .map(this::convert)
                    .distinct()
                    .collect(Collectors.toList());
            learningObject.setFirstReviewTaxons(firstReviewTaxons);
        }

        SearchResult searchResult = new SearchResult();
        searchResult.setItems(allUnreviewed);
        searchResult.setTotalResults(unreviewedCount);
        return searchResult;
    }

    private FirstReviewTaxon convert(Taxon taxon) {
        TaxonPosition tp = taxonPositionDao.findByTaxon(taxon);
        if (tp == null) return null;
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
        }
        if (taxonDao.getUserTaxons(user).isEmpty()) {
            return 0L;
        }
        return firstReviewDao.findCountOfUnreviewed(user);
    }

    public FirstReview save(LearningObject learningObject) {
        learningObject.setUnReviewed(learningObject.getUnReviewed() + 1);
        FirstReview firstReview = new FirstReview();
        firstReview.setLearningObject(learningObject);
        firstReview.setReviewed(false);
        firstReview.setCreatedAt(LocalDateTime.now());
        return firstReviewDao.createOrUpdate(firstReview);
    }

    public void setReviewed(LearningObject learningObject, User loggedInUser, ReviewStatus reviewStatus) {
        for (FirstReview firstReview : learningObject.getFirstReviews()) {
            if (!firstReview.isReviewed()) {
                firstReview.setReviewedAt(LocalDateTime.now());
                firstReview.setReviewedBy(loggedInUser);
                firstReview.setReviewed(true);
                firstReview.setStatus(reviewStatus);
                firstReviewDao.createOrUpdate(firstReview);
                learningObject.setUnReviewed(learningObject.getUnReviewed() - 1);
            }
        }
    }
}
