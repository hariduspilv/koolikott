package ee.hm.dop.rest;

import ee.hm.dop.model.Faq;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.FaqService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("faq")
public class FaqResource extends BaseResource {

    @Inject
    private FaqService faqService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Faq> getFaqs() {
        return faqService.findAllFaq();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(RoleString.ADMIN)
    public Faq save(Faq faq) {
        return faqService.save(faq, getLoggedInUser());
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(RoleString.ADMIN)
    public void delete(Faq faq) {
         faqService.delete(faq, getLoggedInUser());
    }

}
