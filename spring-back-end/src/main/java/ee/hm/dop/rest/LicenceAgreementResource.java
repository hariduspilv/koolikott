package ee.hm.dop.rest;

import ee.hm.dop.model.LicenceAgreement;
import ee.hm.dop.service.LicenceAgreementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
