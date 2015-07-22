package ee.hm.dop.guice.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Provider;
import com.google.inject.Singleton;

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

    private static final Map<String, List<Long>> searchResults;
    
    private static final int RESULTS_PER_PAGE = 3;

    @Override
    public List<Long> search(String query, long start) {
        if (!searchResults.containsKey(query)) {
            return Collections.emptyList();
        }

        List<Long> allResults = searchResults.get(query);
        List<Long> selectedResults = new ArrayList<>();
        for (int i = 0; i < allResults.size(); i++) {
            if (i >= start && i < start + RESULTS_PER_PAGE) {
                selectedResults.add(allResults.get(i));
            }
        }
        
        return selectedResults;
    }
    
    @Override 
    public long countResults(String query) {
        if (!searchResults.containsKey(query)) {
            return 0;
        }

        return searchResults.get(query).size(); 
    }

    static {
        searchResults = new HashMap<>();

        addArabicQuery();
        addBigQuery();
    }

    private static void addArabicQuery() {
        String arabicQuery = "المدرسية";
        ArrayList<Long> arabicSearchResult = new ArrayList<>();
        arabicSearchResult.add((long) 3);

        searchResults.put(arabicQuery, arabicSearchResult);
    }
    
    private static void addBigQuery() {
        String bigQuery = "thishasmanyresults";
        ArrayList<Long> bigSearchResults = new ArrayList<>();
        for (long i = 0; i < 8; i++) {
            bigSearchResults.add(i);
        }
        
        searchResults.put(bigQuery, bigSearchResults);
    }
}
