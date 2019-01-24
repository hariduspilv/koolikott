package ee.hm.dop.rest.administration;

import com.google.common.io.Files;
import ee.hm.dop.model.CustomerSupport;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.useractions.CustomerSupportService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Path("admin/customerSupport")
public class CustomerSupportResource extends BaseResource {

    @Inject
    private CustomerSupportService customerSupportService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public CustomerSupport saveCustomerSupportRequest(CustomerSupport customerSupport) {

        final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|jpeg|gif|bmp|JPG|PNG|GIF|JPEG))$)";

        boolean dataIsValidated = false;

        List<String> imageFiles =customerSupport.getFiles().stream().map(f -> f.getName()).collect(Collectors.toList());

        List<String> extensions = imageFiles.stream().map(f -> Files.getFileExtension(f)).collect(Collectors.toList());

        for(String ext : extensions){

            Pattern pattern = Pattern.compile(IMAGE_PATTERN);
            Matcher matcher = pattern.matcher(ext);

//            dataIsValidated = matcher.matches() ? true : false;

            if (matcher.matches()){
                dataIsValidated = true;

            }
            else{
                dataIsValidated = false;
                break;
            }
        }
        if (dataIsValidated) {

            return customerSupportService.save(customerSupport, getLoggedInUser());

        }
        else {
            return null;
        }    }
}
