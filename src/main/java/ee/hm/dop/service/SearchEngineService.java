package ee.hm.dop.service;

import java.util.List;

public interface SearchEngineService {

    public List<Long> search(String query);
}
