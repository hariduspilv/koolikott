package ee.hm.dop.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.EducationalContext;
import ee.hm.dop.service.EducationalContextService;

@Path("learningMaterialMetadata")
public class LearningMaterialMetadataResource {

    @Inject
    private EducationalContextService educationalContextService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("educationalContext")
    public List<EducationalContext> getEducationalContext() {
        return educationalContextService.getAll();
    }
}
