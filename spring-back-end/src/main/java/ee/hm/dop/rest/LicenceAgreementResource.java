package ee.hm.dop.rest;

import ee.hm.dop.model.LicenceAgreement;
import ee.hm.dop.service.LicenceAgreementService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("licenceAgreement")
public class LicenceAgreementResource extends BaseResource {

    @Inject
    LicenceAgreementService licenceAgreementService;

    @GetMapping("latest")
    public LicenceAgreement getLatest() {
        return licenceAgreementService.getLatestLicenceAgreement();
    }

    @PostMapping
    public LicenceAgreement save(@RequestBody LicenceAgreement licenceAgreement) {
        licenceAgreement.setCreatedBy(getLoggedInUser());
        return licenceAgreementService.save(licenceAgreement);
    }

}
