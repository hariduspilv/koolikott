package ee.hm.dop.rest.administration;

import ee.hm.dop.model.Agreement;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.login.AgreementService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("admin/agreement")
public class AgreementAdminResource extends BaseResource {

    @Inject
    private AgreementService agreementService;

    @PostMapping
    @Secured({RoleString.ADMIN})
    public Agreement saveAgreement(@RequestBody Agreement agreement) {
        return agreementService.save(agreement, getLoggedInUser());
    }

}
