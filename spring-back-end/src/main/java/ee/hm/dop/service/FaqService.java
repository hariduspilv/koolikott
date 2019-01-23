package ee.hm.dop.service;

import ee.hm.dop.dao.FaqDao;
import ee.hm.dop.model.Faq;
import ee.hm.dop.model.User;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.util.List;

import static ee.hm.dop.utils.UserUtil.mustBeAdmin;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Transactional
public class FaqService {

    @Inject
    private FaqDao faqDao;

    public List<Faq> findAllFaq() {
        return faqDao.findAll();
    }

    public Faq save(Faq faq, User user) {
        mustBeAdmin(user);
        validateFaq(faq);
        faq.setCreatedAt(LocalDateTime.now());
        return faqDao.createOrUpdate(faq);
    }

    public void delete(Faq faq, User loggedInUser) {
        mustBeAdmin(loggedInUser);
        Faq dbFaq = faqDao.findById(faq.getId());
        faqDao.remove(dbFaq);
    }

    private ResponseStatusException badRequest(String s) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, s);
    }

    private void validateFaq(Faq faq) {
        if (isBlank(faq.getQuestionEst())) throw badRequest("Question Est is Empty");
        if (isBlank(faq.getQuestionEng())) throw badRequest("Question Eng is Empty");
        if (isBlank(faq.getQuestionRus())) throw badRequest("Question Rus is Empty");
        if (isBlank(faq.getAnswerEst())) throw badRequest("Answer Est is Empty");
        if (isBlank(faq.getAnswerEng())) throw badRequest("Answer Eng is Empty");
        if (isBlank(faq.getAnswerRus())) throw badRequest("Answer Rus is Empty");
    }
}
