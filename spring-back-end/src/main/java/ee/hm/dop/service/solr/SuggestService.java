package ee.hm.dop.service.solr;

import ee.hm.dop.service.SuggestionStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import javax.inject.Inject;

@Service
@Transactional
public class SuggestService {

    @Inject
    private SolrEngineService solrEngineService;

    public List<String> suggest(String query, SuggestionStrategy suggestionStrategy) {
        if (query.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return solrEngineService.suggest(query, suggestionStrategy);
    }

}
