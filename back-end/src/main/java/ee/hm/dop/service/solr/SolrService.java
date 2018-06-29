package ee.hm.dop.service.solr;

import ee.hm.dop.model.solr.SolrSearchResponse;
import ee.hm.dop.service.SuggestionStrategy;
import org.apache.commons.configuration.Configuration;
import org.apache.solr.client.solrj.*;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.Suggestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static ee.hm.dop.service.solr.SearchCommandBuilder.getSearchCommand;
import static ee.hm.dop.utils.ConfigurationProperties.SEARCH_SERVER;

@Singleton
public class SolrService implements SolrEngineService {

    static final String SOLR_IMPORT_PARTIAL = "dataimport?command=delta-import&wt=json";
    static final String SOLR_DATAIMPORT_STATUS = "dataimport?command=status&wt=json";
    static final String SOLR_STATUS_BUSY = "busy";
    private static final Logger logger = LoggerFactory.getLogger(SolrService.class);
    private static final int RESULTS_PER_PAGE = 24;
    private static final int SUGGEST_COUNT = 5;
    private static final String SUGGEST_URL = "/suggest";
    private static final String SUGGEST_TAG_URL = "/suggest_tag";
    @Inject
    private Client client;
    @Inject
    private Configuration configuration;
    private SolrClient solrClient;
    private SolrIndexThread indexThread;

    @Inject
    public void postConstruct() {
        postConstruct(configuration.getString(SEARCH_SERVER));
    }

    void postConstruct(String url) {
        solrClient = new HttpSolrClient.Builder()
                .withBaseSolrUrl(url)
                .build();
        indexThread = new SolrIndexThread();
        indexThread.start();
    }

    @Override
    public SolrSearchResponse search(SolrSearchRequest searchRequest) {
        Long itemLimit = searchRequest.getItemLimit() == 0
                ? RESULTS_PER_PAGE
                : Math.min(searchRequest.getItemLimit(), RESULTS_PER_PAGE);

        return executeCommand(getSearchCommand(searchRequest, itemLimit));
    }

    @Override
    public SolrSearchResponse limitlessSearch(SolrSearchRequest searchRequest) {
        SearchGrouping initialGrouping = searchRequest.getGrouping();
        long initialStart = searchRequest.getFirstItem();
        searchRequest.setGrouping(SearchGrouping.GROUP_NONE);
        searchRequest.setFirstItem(0);
        SolrSearchResponse response = executeCommand(getSearchCommand(searchRequest, (long) 2147483647));
        searchRequest.setGrouping(initialGrouping);
        searchRequest.setFirstItem(initialStart);
        return response;
    }

    @Override
    public List<String> suggest(String query, SuggestionStrategy suggestionStrategy) {
        if (query.isEmpty()) return null;
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setRequestHandler(suggestionStrategy.suggestTag() ? SUGGEST_TAG_URL : SUGGEST_URL);
        solrQuery.setQuery(query);

        QueryResponse qr;
        try {
            qr = solrClient.query(solrQuery, SolrRequest.METHOD.POST);
        } catch (SolrServerException | IOException e) {
            logger.error("The SolrServer encountered an error.");
            return null;
        }

        if (qr.getSuggesterResponse() == null) return null;
        List<Suggestion> combinedSuggestions = new ArrayList<>();

        if (suggestionStrategy.suggestTag()) {
            combinedSuggestions.addAll(qr.getSuggesterResponse().getSuggestions().get("dopTagSuggester"));
        } else {
            combinedSuggestions.addAll(qr.getSuggesterResponse().getSuggestions().get("linkSuggester"));
            combinedSuggestions.addAll(qr.getSuggesterResponse().getSuggestions().get("dopSuggester"));
        }

        List<String> suggestions = combinedSuggestions.stream().map(Suggestion::getTerm).collect(Collectors.toList());
        return suggestions.size() > SUGGEST_COUNT ? suggestions.subList(0, SUGGEST_COUNT - 1) : suggestions;
    }

    @Override
    public void updateIndex() {
        indexThread.updateIndex();
    }

    private boolean isIndexingInProgress() {
        SolrSearchResponse response = executeCommand(SOLR_DATAIMPORT_STATUS);
        return response.getStatus().equals(SOLR_STATUS_BUSY);
    }

    SolrSearchResponse executeCommand(String command) {
        SolrSearchResponse searchResponse = getTarget(command).request(MediaType.APPLICATION_JSON).get(SolrSearchResponse.class);
        logCommand(command, searchResponse);
        return searchResponse;
    }

    private void logCommand(String command, SolrSearchResponse searchResponse) {
        long responseCode = searchResponse.getResponseHeader().getStatus();

        String statusMessages = "";
        if (searchResponse.getStatusMessages() != null) {
            statusMessages = "Status messages: " + searchResponse.getStatusMessages().entrySet().stream()
                    .map(Entry::toString)
                    .collect(Collectors.joining(";", "[", "]"));
        }

        String logMessage = String.format("Solr responded with code %s, url was %s %s", responseCode,
                configuration.getString(SEARCH_SERVER) + command, statusMessages);

        if (responseCode != 0) {
            logger.info(logMessage);
        } else {
            logger.debug(logMessage);
        }
    }

    private WebTarget getTarget(String path) {
        return client.target(getFullURL(path));
    }

    private String getFullURL(String path) {
        String serverUrl = configuration.getString(SEARCH_SERVER);
        return serverUrl + path;
    }

    private class SolrIndexThread extends Thread {
        public static final int _1_SEC = 1000;
        private final Object lock = new Object();
        private boolean updateIndex;

        public void updateIndex() {
            synchronized (lock) {
                updateIndex = true;
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (updateIndex) {
                        synchronized (lock) {
                            updateIndex = false;
                            lock.notifyAll();
                            logger.info("Updating Solr index.");
                            executeCommand(SOLR_IMPORT_PARTIAL);
                            waitForCommandToFinish();
                        }
                    }

                    sleep(_1_SEC);
                }
            } catch (InterruptedException e) {
                logger.info("Solr indexing thread interrupted.");
            }
        }

        private void waitForCommandToFinish() throws InterruptedException {
            while (isIndexingInProgress()) {
                sleep(_1_SEC);
            }
        }
    }
}
