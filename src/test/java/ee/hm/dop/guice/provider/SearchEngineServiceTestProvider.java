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

    @Override
    public List<Long> search(String query) {
        if (!searchResults.containsKey(query)) {
            return Collections.emptyList();
        }

        return searchResults.get(query);
    }

    static {
        searchResults = new HashMap<>();

        addArabicQuery();
    }

    private static void addArabicQuery() {
        String arabicQuery = "المدرسية";
        ArrayList<Long> arabicSearchResult = new ArrayList<>();
        arabicSearchResult.add((long) 3);

        searchResults.put(arabicQuery, arabicSearchResult);
    }
}
