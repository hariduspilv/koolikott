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
    public Agreement saveAgreement(Agreement agreement) {
        return agreementService.save(agreement, getLoggedInUser());
    }

    @POST
    @Path("delete")
    @RolesAllowed({RoleString.ADMIN})
    public void deleteAgreement(Agreement agreement) {
        agreementService.deleteAgreement(agreement, getLoggedInUser());
    }
}
