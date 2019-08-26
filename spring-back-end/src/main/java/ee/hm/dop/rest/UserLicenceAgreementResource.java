package ee.hm.dop.rest;

import ee.hm.dop.model.UserLicenceAgreement;
import ee.hm.dop.service.UserLicenceAgreementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("userLicenceAgreement")
public class UserLicenceAgreementResource {

    @Inject
    UserLicenceAgreementService userLicenceAgreementService;

    @GetMapping
    public UserLicenceAgreement get(@RequestParam(value = "id") Long id) {
        return userLicenceAgreementService.getLatestUserLicenceAgreement(id);
    }

}
