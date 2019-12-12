package ee.hm.dop.service;

import ee.hm.dop.dao.GdprProcessTermsDao;
import ee.hm.dop.model.GdprProcessTerms;
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
public class GdprProcessTermsService {

    @Inject
    private GdprProcessTermsDao gdprProcessTermsDao;

    public List<GdprProcessTerms> findAllLicenses() {
        return gdprProcessTermsDao.findAll();
    }

    public GdprProcessTerms save(GdprProcessTerms gdprProcessTerms, User user) {
        mustBeAdmin(user);
        validateLicenses(gdprProcessTerms);
        gdprProcessTerms.setCreatedAt(LocalDateTime.now());
        return gdprProcessTermsDao.createOrUpdate(gdprProcessTerms);
    }

    public void delete(GdprProcessTerms gdprProcessTerms, User loggedInUser) {
        mustBeAdmin(loggedInUser);
        GdprProcessTerms dbGdprProcessTerms = gdprProcessTermsDao.findById(gdprProcessTerms.getId());
        gdprProcessTermsDao.remove(dbGdprProcessTerms);
    }

    private ResponseStatusException badRequest(String s) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, s);
    }

    private void validateLicenses(GdprProcessTerms gdprProcessTerms) {
        if (isBlank(gdprProcessTerms.getTitleEst())) throw badRequest("Title Est is Empty");
        if (isBlank(gdprProcessTerms.getTitleEng())) throw badRequest("Title Eng is Empty");
        if (isBlank(gdprProcessTerms.getTitleRus())) throw badRequest("Title Rus is Empty");
        if (isBlank(gdprProcessTerms.getContentEst())) throw badRequest("Content Est is Empty");
        if (isBlank(gdprProcessTerms.getContentEng())) throw badRequest("Content Eng is Empty");
        if (isBlank(gdprProcessTerms.getContentRus())) throw badRequest("Content Rus is Empty");
    }

}
