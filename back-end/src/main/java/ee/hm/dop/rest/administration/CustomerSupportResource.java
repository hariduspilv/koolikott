package ee.hm.dop.rest.administration;

import ee.hm.dop.model.CustomerSupport;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.useractions.CustomerSupportService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("admin/customerSupport")
public class CustomerSupportResource extends BaseResource {

    @Inject
    private CustomerSupportService customerSupportService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public CustomerSupport saveCustomerSupportRequest(CustomerSupport customerSupport) {
        return customerSupportService.save(customerSupport, getLoggedInUser());
    }
}
