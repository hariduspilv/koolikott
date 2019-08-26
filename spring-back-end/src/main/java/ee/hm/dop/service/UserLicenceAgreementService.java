package ee.hm.dop.service;

import ee.hm.dop.dao.UserLicenceAgreementDao;
import ee.hm.dop.model.UserLicenceAgreement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
@Transactional
public class UserLicenceAgreementService {

    @Inject
    UserLicenceAgreementDao userLicenceAgreementDao;

    public UserLicenceAgreement getLatestUserLicenceAgreement(Long userid) {
        return userLicenceAgreementDao.getLatestUserLicenceAgreement(userid);
    }
}
