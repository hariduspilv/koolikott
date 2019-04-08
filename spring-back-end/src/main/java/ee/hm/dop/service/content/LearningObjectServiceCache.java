package ee.hm.dop.service.content;

import ee.hm.dop.dao.TaxonDao;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@Service
public class LearningObjectServiceCache {

    @Inject
    TaxonDao taxonDao;

    @Cacheable("LearningObjectServiceCache_userTaxonCache")
    public List<Long> getUserTaxonWithChildren(Long user) {
        return taxonDao.getUserTaxonWithChildren(Arrays.asList(user));
    }
}
