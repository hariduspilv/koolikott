package ee.hm.dop.service.useractions;

import ee.hm.dop.dao.UserDao;
import ee.hm.dop.dao.UserEmailDao;
import ee.hm.dop.dao.UserProfileDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserEmail;
import ee.hm.dop.model.UserProfile;
import ee.hm.dop.model.ehis.InstitutionEhis;
import ee.hm.dop.model.enums.UserRole;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.PinGeneratorService;
import ee.hm.dop.service.SendMailService;
import ee.hm.dop.service.ehis.EhisInstitutionService;
import ee.hm.dop.service.metadata.TaxonService;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ee.hm.dop.utils.UserDataValidationUtil.validateEmail;

public class UserProfileService {


    @Inject
    private UserProfileDao userProfileDao;
    @Inject
    private UserDao userDao;
    @Inject
    private UserEmailDao userEmailDao;
    @Inject
    private SendMailService sendMailService;
    @Inject
    private EhisInstitutionService ehisInstitutionService;
    @Inject
    private TaxonService taxonService;

    public Response update(UserProfile userProfile, User user) {
        Response response = Response.status(HttpURLConnection.HTTP_OK).build();

        updateUser(userProfile, user);
        UserEmail dbUserEmail = userEmailDao.findByUser(user);
        if (dbUserEmail != null && !dbUserEmail.getEmail().equals(validateEmail(userProfile.getEmail()))) {
            dbUserEmail.setPin(PinGeneratorService.generatePin());
            sendMailService.sendEmail(sendMailService.sendPinToUser(dbUserEmail, userProfile.getEmail()));
            userEmailDao.createOrUpdate(dbUserEmail);
            response = Response.status(HttpURLConnection.HTTP_CREATED).build();
        } else if (dbUserEmail == null ){
            createUserEmail(user, userProfile);
            response = Response.status(HttpURLConnection.HTTP_CREATED).build();
        }

        UserProfile dbUserProfile = userProfileDao.findByUser(user);
        if (dbUserProfile != null) {
            dbUserProfile.setLastUpdate(DateTime.now());
            dbUserProfile.setRole(userProfile.getRole());
            dbUserProfile.setCustomRole(userProfile.getCustomRole());
            userProfileDao.createOrUpdate(dbUserProfile);
            return response;
        }
        userProfile.setLastUpdate(DateTime.now());
        userProfileDao.createOrUpdate(userProfile);
        return response;

    }

    public UserProfile getUserProfile(User loggedInUser) {
        User user = userDao.findUserById(loggedInUser.getId());
        UserProfile userProfile = userProfileDao.findByUser(user);
        if (userProfile == null)
            return null;

        userProfile.setInstitutions(user.getInstitutions());
        userProfile.setTaxons(user.getTaxons());
        return userProfile;
    }

    private void createUserEmail(User user, UserProfile userProfile) {
        UserEmail userEmail = new UserEmail();
        userEmail.setPin(PinGeneratorService.generatePin());
        userEmail.setUser(user);
        userEmail.setActivated(false);
        userEmail.setActivatedAt(null);
        userEmail.setCreatedAt(DateTime.now());
        userEmail.setEmail("");
        if (sendMailService.sendEmail(sendMailService.sendPinToUser(userEmail, userProfile.getEmail())));
            userEmailDao.createOrUpdate(userEmail);
    }

    private void updateUser(UserProfile userProfile, User user) {
        User dbUser = userDao.findUserById(user.getId());
        if (dbUser != null) {
            if (userProfile.getRole() != null) {
                if (userProfile.getRole() == UserRole.PARENT || userProfile.getRole() == UserRole.OTHER) {
                    dbUser.setInstitutions(null);
                } else {
                    setInstitutions(userProfile, dbUser);
                    setTaxons(userProfile, dbUser);
                }
            } else {
                dbUser.setInstitutions(null);
            }
            userDao.createOrUpdate(dbUser);
        } else {
            throw badRequest("User not found");
        }
    }

    private void setTaxons(UserProfile userProfile, User dbUser) {
        List<Taxon> taxons;
        if (userProfile.getTaxons().stream().noneMatch(Objects::nonNull))
            taxons = null;
        else {
            List<Long> ids = userProfile.getTaxons().stream().map(Taxon::getId).collect(Collectors.toList());
            taxons = taxonService.getTaxonById(ids);
        }
        dbUser.setTaxons(taxons);
    }

    private void setInstitutions(UserProfile userProfile, User dbUser) {
        List<InstitutionEhis> institutions;
        if (userProfile.getInstitutions().stream().noneMatch(Objects::nonNull))
            institutions = null;
        else {
            List<Long> ids = userProfile.getInstitutions().stream().map(InstitutionEhis::getId).collect(Collectors.toList());
            institutions = ehisInstitutionService.getInstitutionEhisById(ids);
        }
        dbUser.setInstitutions(institutions);
    }

    private WebApplicationException badRequest(String s) {
        return new WebApplicationException(s, Response.Status.BAD_REQUEST);
    }
}
