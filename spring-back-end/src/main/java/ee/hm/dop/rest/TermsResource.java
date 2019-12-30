package ee.hm.dop.rest;

import ee.hm.dop.model.Terms;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.TermsService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("terms")
public class TermsResource extends BaseResource {

    @Inject
    private TermsService termsService;

    @GetMapping()
    public List<Terms> getTerms(@RequestParam("type") String termType) {
        return termsService.getTerms(termType);
    }

    @PostMapping
    @Secured(RoleString.ADMIN)
    public Terms save(@RequestBody Terms terms) {
        return termsService.save(terms, getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("delete")
    @Secured(RoleString.ADMIN)
    public void delete(@RequestBody Terms terms) {
         termsService.delete(terms, getLoggedInUser());
    }

}
