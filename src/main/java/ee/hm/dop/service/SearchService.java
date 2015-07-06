package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ee.hm.dop.dao.SearchDAO;

@Singleton
public class SearchService {

    @Inject
    private SearchDAO searchDAO;

    public List<Long> search(String query) {
        return searchDAO.search(query);
    }
}
