package ee.hm.dop.service.solr;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class SuggestService {

    @Inject
    private SolrEngineService solrEngineService;

    public List<String> suggest(String query) {
        if (query.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return solrEngineService.suggest(query);
    }

}
