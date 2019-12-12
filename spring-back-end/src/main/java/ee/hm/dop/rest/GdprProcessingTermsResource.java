package ee.hm.dop.rest;

import ee.hm.dop.model.GdprProcessTerms;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.GdprProcessTermsService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("gdprProcessing")
public class GdprProcessingTermsResource extends BaseResource {

    @Inject
    private GdprProcessTermsService gdprProcessTermsService;

    @GetMapping
    public List<GdprProcessTerms> getLicenses() {
        return gdprProcessTermsService.findAllLicenses();
    }

    @PostMapping
    @Secured(RoleString.ADMIN)
    public GdprProcessTerms save(@RequestBody GdprProcessTerms gdprProcessTerms) {
        return gdprProcessTermsService.save(gdprProcessTerms, getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("delete")
    @Secured(RoleString.ADMIN)
    public void delete(@RequestBody GdprProcessTerms gdprProcessTerms) {
        gdprProcessTermsService.delete(gdprProcessTerms, getLoggedInUser());
    }

}
