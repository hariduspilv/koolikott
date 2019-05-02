package ee.hm.dop.rest.administration;

import com.google.common.io.Files;
import ee.hm.dop.model.AttachedFile;
import ee.hm.dop.model.CustomerSupport;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.useractions.CustomerSupportService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Path("admin/customerSupport")
public class CustomerSupportResource extends BaseResource {

    public static final List<String> PICTURE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "bmp", "gif");
    @Inject
    private CustomerSupportService customerSupportService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR, RoleString.USER, RoleString.RESTRICTED})
    public CustomerSupport saveCustomerSupportRequest(CustomerSupport customerSupport) {
        if (invalidForm(customerSupport)) {
            throw new WebApplicationException("contains invalid files", Response.Status.BAD_REQUEST);
        }
        return customerSupportService.save(customerSupport, getLoggedInUser());
    }

    private boolean invalidForm(CustomerSupport customerSupport) {
        if (isEmpty(customerSupport.getFiles())){
            return false;
        }
        return customerSupport.getFiles().stream()
                .map(AttachedFile::getName)
                .map(Files::getFileExtension)
                .map(String::toLowerCase)
                .noneMatch(PICTURE_EXTENSIONS::contains);
    }
}
