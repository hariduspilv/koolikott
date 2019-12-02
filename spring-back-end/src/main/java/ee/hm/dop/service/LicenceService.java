package ee.hm.dop.service;

import ee.hm.dop.dao.LicensesDao;
import ee.hm.dop.model.Licenses;
import ee.hm.dop.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

import static ee.hm.dop.utils.UserUtil.mustBeAdmin;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Transactional
public class LicenceService {

    @Inject
    private LicensesDao licensesDao;

    public List<Licenses> findAllLicenses() {
        return licensesDao.findAll();
    }

    public Licenses save(Licenses licenses, User user) {
        mustBeAdmin(user);
        validateLicenses(licenses);
        licenses.setCreatedAt(LocalDateTime.now());
        return licensesDao.createOrUpdate(licenses);
    }

    public void delete(Licenses licenses, User loggedInUser) {
        mustBeAdmin(loggedInUser);
        Licenses dbLicenses = licensesDao.findById(licenses.getId());
        licensesDao.remove(dbLicenses);
    }

    private ResponseStatusException badRequest(String s) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, s);
    }

    private void validateLicenses(Licenses licenses) {
        if (isBlank(licenses.getTitleEst())) throw badRequest("Title Est is Empty");
        if (isBlank(licenses.getTitleEng())) throw badRequest("Title Eng is Empty");
        if (isBlank(licenses.getTitleRus())) throw badRequest("Title Rus is Empty");
        if (isBlank(licenses.getContentEst())) throw badRequest("Content Est is Empty");
        if (isBlank(licenses.getContentEng())) throw badRequest("Content Eng is Empty");
        if (isBlank(licenses.getContentRus())) throw badRequest("Content Rus is Empty");
    }

}
