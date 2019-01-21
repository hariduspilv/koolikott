package ee.hm.dop.rest.administration;

import ee.hm.dop.model.CustomerSupport;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.useractions.CustomerSupportService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("admin/customerSupport")
public class CustomerSupportResource extends BaseResource {

    @Inject
    private CustomerSupportService customerSupportService;

    @PostMapping
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public CustomerSupport saveCustomerSupportRequest(@RequestBody CustomerSupport customerSupport) {
        return customerSupportService.save(customerSupport, getLoggedInUser());
    }
}
