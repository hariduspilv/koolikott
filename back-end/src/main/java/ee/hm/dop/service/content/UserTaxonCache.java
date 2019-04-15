package ee.hm.dop.service.content;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.model.User;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static ee.hm.dop.utils.ConfigurationProperties.*;

public class UserTaxonCache {
    private static Logger logger = LoggerFactory.getLogger(UserTaxonCache.class);

    @Inject
    private TaxonDao taxonDao;
    @Inject
    private Configuration configuration;
    private LoadingCache<Long, List<Long>> userTaxonCache;

    @Inject
    public void postConstruct() {
        userTaxonCache = CacheBuilder.newBuilder()
                .expireAfterAccess(configuration.getInt(CACHE_TIME), TimeUnit.MINUTES)
                .maximumSize(configuration.getInt(CACHE_MAX_SIZE))
                .build(
                        new CacheLoader<Long, List<Long>>() {
                            public List<Long> load(Long id) {
                                return taxonDao.getUserTaxonWithChildren(id);
                            }
                        }
                );
    }


    public synchronized List<Long> getUserTaxonsWithChildren(User user) {
        try {
            return userTaxonCache.get(user.getId());
        } catch (ExecutionException e) {
            logger.error("Cache error with user id= " + user.getId());
            return null;
        }
    }
}
