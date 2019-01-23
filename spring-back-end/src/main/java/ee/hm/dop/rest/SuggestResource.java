package ee.hm.dop.rest;

import ee.hm.dop.service.SuggestionStrategy;
import ee.hm.dop.service.solr.SuggestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("suggest")
public class SuggestResource extends BaseResource {

    @Inject
    private SuggestService suggestService;

    @GetMapping
    public List<String> suggest(@RequestParam("q") String query){
        return suggestService.suggest(query, SuggestionStrategy.SUGGEST_URL);
    }

    @GetMapping("tag")
    public List<String> suggestSystemTag(@RequestParam("q") String query){
        return suggestService.suggest(query, SuggestionStrategy.SUGGEST_TAG);
    }
}
