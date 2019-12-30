package ee.hm.dop.service;

import ee.hm.dop.dao.AgreementDao;
import ee.hm.dop.dao.TermsDao;
import ee.hm.dop.model.Terms;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.TermType;
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
public class TermsService {

    @Inject
    private TermsDao termsDao;
    @Inject
    private AgreementDao agreementDao;

    public Terms save(Terms terms, User user) {
        mustBeAdmin(user);
        validateTerms(terms);
        Terms termToSave = createNewTerms(terms, user);

        if (terms.getType().equals(TermType.GDPR)) {
            termToSave.setAgreement(agreementDao.findLatestGdprTermsAgreement());
        } else if (terms.getType().equals(TermType.USAGE)) {
            termToSave.setAgreement(agreementDao.findLatestUserTermsAgreement());
        }
        return termsDao.createOrUpdate(termToSave);
    }

    private Terms createNewTerms(Terms terms, User user) {
        Terms newTerms = new Terms();
        newTerms.setCreatedBy(user);
        newTerms.setCreatedAt(LocalDateTime.now());
        newTerms.setContentEng(terms.getContentEng());
        newTerms.setContentEst(terms.getContentEst());
        newTerms.setContentRus(terms.getContentRus());
        newTerms.setTitleEng(terms.getTitleEng());
        newTerms.setTitleEst(terms.getTitleEst());
        newTerms.setTitleRus(terms.getTitleRus());
        newTerms.setType(terms.getType());
        return newTerms;
    }

    public void delete(Terms terms, User loggedInUser) {
        mustBeAdmin(loggedInUser);
        Terms dbTerms = termsDao.findById(terms.getId());
        termsDao.remove(dbTerms);
    }

    private ResponseStatusException badRequest(String s) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, s);
    }

    private void validateTerms(Terms terms) {
        if (isBlank(terms.getTitleEst())) throw badRequest("Title Est is Empty");
        if (isBlank(terms.getTitleEng())) throw badRequest("Title Eng is Empty");
        if (isBlank(terms.getTitleRus())) throw badRequest("Title Rus is Empty");
        if (isBlank(terms.getContentEst())) throw badRequest("Content Est is Empty");
        if (isBlank(terms.getContentEng())) throw badRequest("Content Eng is Empty");
        if (isBlank(terms.getContentRus())) throw badRequest("Content Rus is Empty");
    }

    public List<Terms> getGdprTerms() {
        return termsDao.getGdprTerms();
    }

    public List<Terms> getUserTerms() {
        return termsDao.getUserTerms();
    }

    public List<Terms> getTerms(String termType) {
        if (termType.equals(TermType.USAGE.name())) {
            return getUserTerms();
        } else if (termType.equals(TermType.GDPR.name())) {
            return getGdprTerms();
        }
        throw badRequest("Term type does not exist");
    }
}
