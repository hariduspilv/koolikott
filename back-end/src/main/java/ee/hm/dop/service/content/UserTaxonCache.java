package ee.hm.dop.service.content;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UserTaxonCache {
    private static Logger logger = LoggerFactory.getLogger(UserTaxonCache.class);

    @Inject
    private TaxonDao taxonDao;

    LoadingCache<Long, List<Long>> userTaxonCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(60, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<Long, List<Long>>() {
                        public List<Long> load(Long id) {
                            return taxonDao.getUserTaxonWithChildren(id);
                        }
                    }
            );

    public synchronized List<Long> getUserTaxonsWithChildren(User user) {
        try {
            return userTaxonCache.get(user.getId());
        } catch (ExecutionException e) {
            logger.error("Cache error with user id= " + user.getId());
            return null;
        }
    }
}
