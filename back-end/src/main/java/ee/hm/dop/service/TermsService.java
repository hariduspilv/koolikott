package ee.hm.dop.service;

import ee.hm.dop.dao.TermsDao;
import ee.hm.dop.model.Terms;
import ee.hm.dop.model.User;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static ee.hm.dop.utils.UserUtil.mustBeAdmin;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class TermsService {

    @Inject
    private TermsDao termsDao;

    public List<Terms> findAllTerms() {
        return termsDao.findAll();
    }


    public Terms save(Terms terms, User user) {
        mustBeAdmin(user);
        validateTerms(terms);
        terms.setCreatedAt(DateTime.now());
        return termsDao.createOrUpdate(terms);
    }

    public void delete(Terms terms, User loggedInUser) {
        mustBeAdmin(loggedInUser);
        Terms dbTerm = termsDao.findById(terms.getId());
        termsDao.remove(dbTerm);
    }

    private WebApplicationException badRequest(String s) {
        return new WebApplicationException(s, Response.Status.BAD_REQUEST);
    }

    private void validateTerms(Terms terms) {
        if (isBlank(terms.getTitleEst())) throw badRequest("Title Est is Empty");
        if (isBlank(terms.getTitleEng())) throw badRequest("Title Eng is Empty");
        if (isBlank(terms.getTitleRus())) throw badRequest("Title Rus is Empty");
        if (isBlank(terms.getContentEst())) throw badRequest("Content Est is Empty");
        if (isBlank(terms.getContentEng())) throw badRequest("Content Eng is Empty");
        if (isBlank(terms.getContentRus())) throw badRequest("Content Rus is Empty");
    }
}
