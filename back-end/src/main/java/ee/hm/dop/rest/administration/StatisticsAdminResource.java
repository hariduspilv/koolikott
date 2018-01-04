package ee.hm.dop.rest.administration;

import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.reviewmanagement.StatisticsService;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsResult;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("admin/statistics/")
public class StatisticsAdminResource extends BaseResource{

    @Inject
    private StatisticsService statisticsService;

    @POST
    @RolesAllowed({RoleString.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public StatisticsResult search(StatisticsFilterDto searchFilter) {
        StatisticsFilterDto filter = searchFilter != null ? searchFilter : new StatisticsFilterDto();
        return statisticsService.statistics(filter, getLoggedInUser());
    }
}
