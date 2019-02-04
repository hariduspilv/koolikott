package ee.hm.dop.rest;

import ee.hm.dop.model.Terms;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.TermsService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("terms")
public class TermsResource extends BaseResource {

    @Inject
    private TermsService termsService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Terms> getTerms() {
        return termsService.findAllTerms();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(RoleString.ADMIN)
    public Terms save(Terms terms) {
        return termsService.save(terms, getLoggedInUser());
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(RoleString.ADMIN)
    public void delete(Terms terms) {
         termsService.delete(terms, getLoggedInUser());
    }

}
