package ee.hm.dop.rest.administration;

import ee.hm.dop.model.Agreement;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.login.AgreementService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RestController
@RequestMapping("admin/agreement")
public class AgreementAdminResource extends BaseResource {

    @Inject
    private AgreementService agreementService;

    @GetMapping
    @Secured({RoleString.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Agreement> getAgreements() {
        return agreementService.findAllValid(getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("validate")
    @Secured({RoleString.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public boolean validate(Agreement agreement) {
        return agreementService.isValid(agreement, getLoggedInUser());
    }

    @PostMapping
    @Secured({RoleString.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Agreement saveAgreement(Agreement agreement) {
        return agreementService.save(agreement, getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("delete")
    @Secured({RoleString.ADMIN})
    public void deleteAgreement(Agreement agreement) {
        agreementService.deleteAgreement(agreement, getLoggedInUser());
    }
}
