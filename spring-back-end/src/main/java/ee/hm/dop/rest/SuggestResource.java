package ee.hm.dop.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.service.SuggestionStrategy;
import ee.hm.dop.service.solr.SuggestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("suggest")
public class SuggestResource extends BaseResource {

    @Inject
    private SuggestService suggestService;

    @GetMapping
    @Produces(MediaType.APPLICATION_JSON)
    public Response suggest(@RequestParam("q") String query){
        return suggestService.suggest(query, SuggestionStrategy.SUGGEST_URL);
    }

    @GetMapping
    @RequestMapping("tag")
    @Produces(MediaType.APPLICATION_JSON)
    public Response suggestSystemTag(@RequestParam("q") String query){
        return suggestService.suggest(query, SuggestionStrategy.SUGGEST_TAG);
    }
}
