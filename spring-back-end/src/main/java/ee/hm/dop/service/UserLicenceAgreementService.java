package ee.hm.dop.service;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.LicenceAgreementDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.dao.UserLicenceAgreementDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLicenceAgreement;
import ee.hm.dop.model.UserLicenceAgreementDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;

@Service
@Transactional
public class UserLicenceAgreementService {

    @Inject
    UserLicenceAgreementDao userLicenceAgreementDao;

    @Inject
    LicenceAgreementDao licenceAgreementDao;

    @Inject
    LearningObjectDao learningObjectDao;

    @Inject
    UserDao userDao;

    public UserLicenceAgreementDto getLatestUserLicenceAgreement(Long userid) {
        UserLicenceAgreement userLicenceAgreement = userLicenceAgreementDao.getLatestUserLicenceAgreement(userid);
        User user = userDao.findUserById(userid);
        boolean hasLearningObjects = learningObjectDao.countAllNotDeletedByCreator(user) > 0;
        UserLicenceAgreementDto userLicenceAgreementDto = new UserLicenceAgreementDto();
        userLicenceAgreementDto.setUserLicenceAgreement(userLicenceAgreement);
        userLicenceAgreementDto.setHasLearningObjects(hasLearningObjects);
        return userLicenceAgreementDto;
    }

    public UserLicenceAgreement setUserLicenceAgreement(User user, boolean agreed, boolean disagreed) {
        return userLicenceAgreementDao.createOrUpdate(createUserLicenceAgreement(user, agreed, disagreed));
    }

    private UserLicenceAgreement createUserLicenceAgreement(User user, boolean agreed, boolean disagreed) {
        UserLicenceAgreement userLicenceAgreement = new UserLicenceAgreement();
        userLicenceAgreement.setLicenceAgreement(licenceAgreementDao.findLatestAgreement());
        userLicenceAgreement.setUser(user);
        userLicenceAgreement.setAgreed(agreed);
        userLicenceAgreement.setDisagreed(disagreed);
        userLicenceAgreement.setClickedAt(LocalDateTime.now());
        return  userLicenceAgreement;
    }
}
