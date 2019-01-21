package ee.hm.dop.rest.administration;

import ee.hm.dop.model.CustomerSupport;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.useractions.CustomerSupportService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RequestMapping("admin/customerSupport")
public class CustomerSupportResource extends BaseResource {

    @Inject
    private CustomerSupportService customerSupportService;

    @PostMapping
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public CustomerSupport saveCustomerSupportRequest(CustomerSupport customerSupport) {
        return customerSupportService.save(customerSupport, getLoggedInUser());
    }
}
