package ee.hm.dop.service;

import ee.hm.dop.dao.LicenceAgreementDao;
import ee.hm.dop.model.LicenceAgreement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
@Transactional
public class LicenceAgreementService {

    @Inject
    LicenceAgreementDao licenceAgreementDao;

    public LicenceAgreement getLatestLicenceAgreement() {
        return licenceAgreementDao.findLatestAgreement();
    }

}
