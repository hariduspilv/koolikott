package ee.hm.dop.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.StudyPlan;
import ee.hm.dop.service.StudyPlanService;

@Path("studyPlan")
public class StudyPlanResource {

    @Inject
    private StudyPlanService studyPlanService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StudyPlan get(@QueryParam("id") long materialId) {
        return studyPlanService.get(materialId);
    }
}
