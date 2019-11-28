package ee.hm.dop.rest;

import ee.hm.dop.model.Licenses;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.LicenceService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("licenses")
public class LicensesResource extends BaseResource {

    @Inject
    private LicenceService licenceService;

    @GetMapping
    public List<Licenses> getLicenses() {
        return licenceService.findAllLicenses();
    }

    @PostMapping
    @Secured(RoleString.ADMIN)
    public Licenses save(@RequestBody Licenses licenses) {
        return licenceService.save(licenses, getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("delete")
    @Secured(RoleString.ADMIN)
    public void delete(@RequestBody Licenses licenses) {
        licenceService.delete(licenses, getLoggedInUser());
    }

}
