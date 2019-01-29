package ee.hm.dop.rest.administration;

import com.google.common.io.Files;
import ee.hm.dop.model.AttachedFile;
import ee.hm.dop.model.CustomerSupport;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.useractions.CustomerSupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@RestController
@RequestMapping("admin/customerSupport")
public class CustomerSupportResource extends BaseResource {

    public static final List<String> PICTURE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "bmp", "gif");

    @Autowired
    private CustomerSupportService customerSupportService;

    @PostMapping
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public CustomerSupport saveCustomerSupportRequest(@RequestBody CustomerSupport customerSupport) {
        if (invalidForm(customerSupport)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "contains invalid files");
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
