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
    private static final String SEARCH_PATH = "select?q=%s&sort=%s&wt=json&start=%d&rows=" + RESULTS_PER_PAGE;

    protected static final String SOLR_IMPORT_PARTIAL = "dataimport?command=delta-import&wt=json";
    protected static final String SOLR_DATAIMPORT_STATUS = "dataimport?command=status&wt=json";

    protected static final String SOLR_STATUS_BUSY = "busy";

    @Inject
    private Client client;

    @Inject
    private Configuration configuration;

    private SolrIndexThread indexThread;

    public SolrService() {
        indexThread = new SolrIndexThread();
        indexThread.start();
    }

    @Override
    public SearchResponse search(String query, long start, String sort) {
        return executeCommand(format(SEARCH_PATH, encodeQuery(query), formatSort(sort), start));
    }

    @Override
    public void updateIndex() {
        indexThread.updateIndex();
    }

    public boolean isIndexingInProgress() {
        SearchResponse response = executeCommand(SOLR_DATAIMPORT_STATUS);
        return response.getStatus().equals(SOLR_STATUS_BUSY);
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

    private String formatSort(String sort) {
        if (sort != null) {
            return encodeQuery(sort);
        } else {
            return "";
        }
    }

    private class SolrIndexThread extends Thread {
        private boolean updateIndex;

        public synchronized void updateIndex() {
            updateIndex = true;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (updateIndex) {
                        updateIndex = false;
                        logger.info("Updating Solr index.");
                        executeCommand(SOLR_IMPORT_PARTIAL);
                        waitForCommandToFinish();
                    }
                    sleep(1000);
                }
            } catch (InterruptedException e) {
                logger.info("Solr indexing thread interrupted.");
            }
        }

        private void waitForCommandToFinish() throws InterruptedException {
            while (isIndexingInProgress()) {
                sleep(1000);
            }
        }
    }
}
