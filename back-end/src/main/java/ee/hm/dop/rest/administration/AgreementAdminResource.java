package ee.hm.dop.rest.administration;

import ee.hm.dop.model.Agreement;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.login.AgreementService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("admin/agreement")
public class AgreementAdminResource extends BaseResource {

    @Inject
    private AgreementService agreementService;

    @GET
    @RolesAllowed({RoleString.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Agreement> getAgreements() {
        return agreementService.findAllValid(getLoggedInUser());
    }

    @POST
    @RolesAllowed({RoleString.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Agreement> saveAgreement(Agreement agreement) {
        User user = getLoggedInUser();
        agreementService.save(user, agreement);
        return agreementService.findAllValid(user);
    }

    @DELETE
    @Path("favorite")
    @RolesAllowed({RoleString.USER})
    public void deleteAgreement(@QueryParam("id") long id) {
        agreementService.deleteAgreement(id, getLoggedInUser());
    }
}
