package ee.hm.dop.guice.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Provider;
import com.google.inject.Singleton;

import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;
import ee.hm.dop.service.SearchEngineService;

/**
 * Guice provider of Search Engine Service.
 */
@Singleton
public class SearchEngineServiceTestProvider implements Provider<SearchEngineService> {

    @Override
    public synchronized SearchEngineService get() {
        return new SearchEngineServiceMock();
    }
}

class SearchEngineServiceMock implements SearchEngineService {

    private static final Map<String, List<Document>> searchResponses;

    private static final int RESULTS_PER_PAGE = 3;

    @Override
    public SearchResponse search(String query, long start) {
        if (!searchResponses.containsKey(query)) {
            return new SearchResponse();
        }

        List<Document> allDocuments = searchResponses.get(query);
        List<Document> selectedDocuments = new ArrayList<>();
        for (int i = 0; i < allDocuments.size(); i++) {
            if (i >= start && i < start + RESULTS_PER_PAGE) {
                selectedDocuments.add(allDocuments.get(i));
            }
        }

        Response response = new Response();
        response.setDocuments(selectedDocuments);
        response.setStart(start);
        response.setTotalResults(selectedDocuments.size());

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResponse(response);

        return searchResponse;
    }

    static {
        searchResponses = new HashMap<>();

        addArabicQuery();
        addBigQuery();
    }

    private static void addArabicQuery() {
        String arabicQuery = "المدرسية";
        ArrayList<Document> arabicSearchResult = new ArrayList<>();
        Document newDocument = new Document();
        newDocument.setId("3");
        arabicSearchResult.add(newDocument);

        searchResponses.put(arabicQuery, arabicSearchResult);
    }

    private static void addBigQuery() {
        String bigQuery = "thishasmanyresults";
        ArrayList<Document> bigQueryDocuments = new ArrayList<>();
        for (long i = 0; i < 8; i++) {
            Document newDocument = new Document();
            newDocument.setId(Long.toString(i));
            bigQueryDocuments.add(newDocument);
        }

        searchResponses.put(bigQuery, bigQueryDocuments);
    }
}
