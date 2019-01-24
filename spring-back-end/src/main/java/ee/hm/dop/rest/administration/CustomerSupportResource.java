package ee.hm.dop.rest.administration;

import com.google.common.io.Files;
import ee.hm.dop.model.AttachedFile;
import ee.hm.dop.model.CustomerSupport;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.useractions.CustomerSupportService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("admin/customerSupport")
public class CustomerSupportResource extends BaseResource {

    @Inject
    private CustomerSupportService customerSupportService;

    @PostMapping
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public CustomerSupport saveCustomerSupportRequest(@RequestBody CustomerSupport customerSupport) {

        final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|jpeg|gif|bmp))$)";

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
        }
    }
}
