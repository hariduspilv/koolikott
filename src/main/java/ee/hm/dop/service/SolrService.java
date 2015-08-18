package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.SEARCH_SERVER;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.model.solr.SearchResponse;

@Singleton
public class SolrService implements SearchEngineService {

    private static final Logger logger = LoggerFactory.getLogger(SolrService.class);

    private static final int RESULTS_PER_PAGE = 24;
    private static final String SEARCH_PATH = "select?q=%s&wt=json&start=%d&rows=" + RESULTS_PER_PAGE;

    private static final String SOLR_IMPORT_PARTIAL = "dataimport?command=full-import&clean=false&wt=json";

    @Inject
    private Client client;

    @Inject
    private Configuration configuration;

    @Override
    public SearchResponse search(String query, long start) {
        return executeCommand(format(SEARCH_PATH, encodeQuery(query), start));
    }

    @Override
    public void updateIndex() {
        logger.info("Updating Solr index.");
        executeCommand(SOLR_IMPORT_PARTIAL);
    }

    protected SearchResponse executeCommand(String command) {
        SearchResponse searchResponse = getTarget(command).request(MediaType.APPLICATION_JSON)
                .get(SearchResponse.class);

        logCommand(command, searchResponse);

        return searchResponse;
    }

    protected void logCommand(String command, SearchResponse searchResponse) {
        long responseCode = searchResponse.getResponseHeader().getStatus();
        if (responseCode != 0) {
            logger.warn("Solr responded with code " + responseCode + ", url was "
                    + configuration.getString(SEARCH_SERVER) + command);
        }
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
