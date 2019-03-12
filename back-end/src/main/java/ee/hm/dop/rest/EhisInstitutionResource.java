package ee.hm.dop.rest;

import ee.hm.dop.model.ehis.InstitutionEhis;
import ee.hm.dop.service.ehis.EhisInstitutionService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
}
