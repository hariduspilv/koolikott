package ee.hm.dop.rest;

import ee.hm.dop.model.Faq;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.FaqService;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("faq")
public class FaqResource extends BaseResource {

    @Inject
    private FaqService faqService;

    @GetMapping
    @RequestMapping
    public List<Faq> getFaqs() {
        return faqService.findAllFaq();
    }

    @PostMapping
    @Secured(RoleString.ADMIN)
    public Faq save(Faq faq) {
        return faqService.save(faq, getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("delete")
    @Secured(RoleString.ADMIN)
    public void delete(Faq faq) {
         faqService.delete(faq, getLoggedInUser());
    }

}
