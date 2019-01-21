package ee.hm.dop.rest;

import ee.hm.dop.service.atom.AtomFeedService;
import org.apache.abdera.model.Feed;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/")
public class AtomFeedResource extends BaseResource{

    @Inject
    private AtomFeedService atomFeedService;

    @GetMapping
    @RequestMapping(value = "{lang}/feed", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    public Feed getMaterials(@PathVariable("lang") String lang) {
        return atomFeedService.getFeed(lang);
    }
}
