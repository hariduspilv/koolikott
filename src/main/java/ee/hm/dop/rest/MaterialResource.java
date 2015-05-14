package ee.hm.dop.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.Material;
import ee.hm.dop.service.MaterialService;

@Path("material")
public class MaterialResource {

    @Inject
    private MaterialService materialService;

    @GET
    @Path("getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Material> getAllMaterials() {
        return materialService.getAllMaterials();
    }
}
