package ee.hm.dop.service.useractions;

import ee.hm.dop.dao.UserDao;
import ee.hm.dop.dao.UserEmailDao;
import ee.hm.dop.dao.UserProfileDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserEmail;
import ee.hm.dop.model.UserProfile;
import ee.hm.dop.model.ehis.InstitutionEhis;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.MailBuilder;
import ee.hm.dop.service.PinGeneratorService;
import ee.hm.dop.service.MailSender;
import ee.hm.dop.service.ehis.EhisInstitutionService;
import ee.hm.dop.service.metadata.TaxonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ee.hm.dop.utils.UserDataValidationUtil.validateEmail;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Service
@Transactional
public class UserProfileService {

    @Inject
    private UserProfileDao userProfileDao;
    @Inject
    private UserDao userDao;
    @Inject
    private UserEmailDao userEmailDao;
    @Inject
    private MailSender mailSender;
    @Inject
    private MailBuilder mailBuilder;
    @Inject
    private EhisInstitutionService ehisInstitutionService;
    @Inject
    private TaxonService taxonService;

    public ResponseEntity<?> update(UserProfile userProfile, User user) {
        ResponseEntity<?> response = ResponseEntity.status(HttpStatus.OK).build();

        updateUser(userProfile, user);
        UserEmail dbUserEmail = userEmailDao.findByUser(user);
        if (dbUserEmail != null && dbUserEmail.getEmail() != null && !dbUserEmail.getEmail().equals(validateEmail(userProfile.getEmail()))) {
            dbUserEmail.setPin(PinGeneratorService.generatePin());
            mailSender.sendEmail(mailBuilder.sendPinToUser(dbUserEmail, userProfile.getEmail()));
            userEmailDao.createOrUpdate(dbUserEmail);
            response = ResponseEntity.status(HttpStatus.CREATED).build();
        } else if (dbUserEmail == null ){
            createUserEmail(user, userProfile);
            response = ResponseEntity.status(HttpStatus.CREATED).build();
        }

        UserProfile dbUserProfile = userProfileDao.findByUser(user);
        if (dbUserProfile != null) {
            dbUserProfile.setLastUpdate(LocalDateTime.now());
            dbUserProfile.setRole(userProfile.getRole());
            dbUserProfile.setCustomRole(userProfile.getCustomRole());
            userProfileDao.createOrUpdate(dbUserProfile);
            return response;
        }
        userProfile.setLastUpdate(LocalDateTime.now());
        userProfileDao.createOrUpdate(userProfile);
        return response;

    }

    public UserProfile getUserProfile(User loggedInUser) {
        User user = userDao.findUserById(loggedInUser.getId());
        UserProfile userProfile = userProfileDao.findByUser(user);
        UserEmail userEmail = userEmailDao.findByUser(user);
        if (userProfile == null && userEmail == null) {
            throw badRequest("No profile found");
        }

        if (userProfile == null) {
            userProfile = new UserProfile();
            userProfile.setUser(user);
        }

        if (isNotEmpty(user.getInstitutions())) {
            userProfile.setInstitutions(user.getInstitutions());
        }
        if (isNotEmpty(user.getTaxons())) {
            userProfile.setTaxons(user.getTaxons());
        }
        if (userEmail.getEmail() != null) {
            userProfile.setEmail(userEmail.getEmail());
        }
        return userProfile;
    }

    private void createUserEmail(User user, UserProfile userProfile) {
        UserEmail userEmail = new UserEmail();
        userEmail.setPin(PinGeneratorService.generatePin());
        userEmail.setUser(user);
        userEmail.setActivated(false);
        userEmail.setActivatedAt(null);
        userEmail.setCreatedAt(LocalDateTime.now());
        userEmail.setEmail(null);
        if (mailSender.sendEmail(mailBuilder.sendPinToUser(userEmail, userProfile.getEmail()))){
            userEmailDao.createOrUpdate(userEmail);
        }
    }

    private void updateUser(UserProfile userProfile, User user) {
        User dbUser = userDao.findUserById(user.getId());
        if (dbUser != null) {
            if (userProfile.getRole() != null) {
                if (userProfile.getRole().needsInstitutions()) {
                    if (userProfile.getInstitutions() != null) {
                        dbUser.setInstitutions(getInstitutionEhis(userProfile.getInstitutions()));
                    }
                } else {
                    dbUser.setInstitutions(null);
                }
            } else {
                dbUser.setInstitutions(null);
            }
            if (userProfile.getTaxons() != null)
                dbUser.setTaxons(getTaxons(userProfile.getTaxons()));
            userDao.createOrUpdate(dbUser);
        } else {
            throw  badRequest("User not found");
        }
    }

    private List<Taxon> getTaxons(List<Taxon> taxons) {
        if (taxons.stream().noneMatch(Objects::nonNull)) {
            return null;
        }
        List<Long> ids = taxons.stream().filter(Objects::nonNull).map(Taxon::getId).filter(Objects::nonNull).collect(Collectors.toList());
        return taxonService.getTaxonById(ids);
    }

    private List<InstitutionEhis> getInstitutionEhis(List<InstitutionEhis> institutions) {
        if (institutions.stream().noneMatch(Objects::nonNull)) {
            return null;
        }
        List<Long> ids = institutions.stream().filter(Objects::nonNull).map(InstitutionEhis::getId).filter(Objects::nonNull).collect(Collectors.toList());
        return ehisInstitutionService.getInstitutionEhisById(ids);
    }

    private ResponseStatusException badRequest(String s) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, s);
    }
}
