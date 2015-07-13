package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.SEARCH_SERVER;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.configuration.Configuration;

import ee.hm.dop.model.SearchResponse;
import ee.hm.dop.model.SearchResponse.Document;
import ee.hm.dop.model.SearchResponse.Response;

@Singleton
public class SolrService implements SearchEngineService {

    private static final String SEARCH_PATH = "select?q=%s&wt=json";

    @Inject
    private Client client;

    @Inject
    private Configuration configuration;

    @Override
    public List<Long> search(String query) {
        SearchResponse searchResponse = getTarget(format(SEARCH_PATH, encodeQuery(query))).request(
                MediaType.APPLICATION_JSON).get(SearchResponse.class);

        List<Long> result = new ArrayList<>();

        Response response = searchResponse.getResponse();
        if (response != null) {
            for (Document document : response.getDocuments()) {
                result.add(document.getId());
            }
        }

        return result;
    }

    private WebTarget getTarget(String path) {
        return client.target(getFullURL(path));
    }

    private String getFullURL(String path) {
        String serverUrl = configuration.getString(SEARCH_SERVER);
        return serverUrl + path;
    }

    private String encodeQuery(String query) {
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return encodedQuery;
    }
}
