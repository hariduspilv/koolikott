package ee.hm.dop.rest;

import ee.hm.dop.model.ehis.InstitutionEhis;
import ee.hm.dop.service.ehis.EhisInstitutionService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("ehisInstitution")
public class EhisInstitutionResource extends BaseResource {

    @Inject
    private EhisInstitutionService ehisInstitutionService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<InstitutionEhis> getEhisInstitutions(){
        return ehisInstitutionService.findAll();
    }

    @GET
    @Path("areas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<String> getEhisInstitutionAreas(){
        return ehisInstitutionService.getInstitutionAreas();
    }

    @GET
    @Path("institutions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<InstitutionEhis> getInstitutionsPerArea(@QueryParam("area") String area)
    {
        return ehisInstitutionService.getInstitutionPerArea(area);
    }


}
