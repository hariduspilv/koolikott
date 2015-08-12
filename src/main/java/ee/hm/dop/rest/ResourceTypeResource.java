package ee.hm.dop.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.ResourceType;
import ee.hm.dop.service.ResourceTypeService;

@Path("resourceType")
public class ResourceTypeResource {

    @Inject
    private ResourceTypeService resourceTypeService;

    @GET
    @Path("getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ResourceType> getAllResourceType() {
        return resourceTypeService.getAllResourceTypes();
    }

}
