package ee.hm.dop.rest.administration;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.LearningObjectMiniDto;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.administration.PageableQuery;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
import ee.hm.dop.service.reviewmanagement.ReviewManager;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.List;

@RestController
@RequestMapping("admin/firstReview")
public class FirstReviewAdminResource extends BaseResource {

    @Inject
    private FirstReviewAdminService firstReviewAdminService;
    @Inject
    private ReviewManager reviewManager;

    @GetMapping
    @RequestMapping("unReviewed")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public SearchResult getUnReviewed(@RequestParam(value = "page", defaultValue = "1") int page,
                                      @RequestParam(value = "itemSortedBy", required = false) String itemSortedBy,
                                      @RequestParam(value = "query", required = false) String query,
                                      @RequestParam(value = "taxon", required = false) List<Long> taxons,
                                      @RequestParam(value = "user", required = false) List<Long> user,
                                      @RequestParam(value = "lang", required = false) Integer lang,
                                      @RequestParam(value = "materialtype", required = false) String materialType)

    {
        PageableQuery pageableQuery = new PageableQuery(page, itemSortedBy, query, taxons, user, lang,materialType);
        if (!pageableQuery.isValid()) {
            throw badRequest("Query parameters invalid");
        }
        return firstReviewAdminService.getUnReviewed(getLoggedInUser(), pageableQuery);
    }

    @GetMapping
    @RequestMapping("unReviewed/count")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getUnReviewedCount() {
        return firstReviewAdminService.getUnReviewedCount(getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("setReviewed")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject setReviewed(@RequestBody LearningObjectMiniDto learningObject) {
        return reviewManager.setEverythingReviewedRefreshLO(getLoggedInUser(), learningObject.convert(), ReviewStatus.ACCEPTED, ReviewType.FIRST);
    }
}
