package ee.hm.dop.service.synchronizer;

import ee.hm.dop.dao.AuthenticatedUserDao;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
@Transactional
public class AuthenticatedUserCleaner {

    @Inject
    private AuthenticatedUserDao authenticatedUserDao;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticatedUserCleaner.class);

    public synchronized void run() {
        try {
            AuthenticatedUserDao authenticationStateDao = newAuthenticationStateDao();
            LocalDateTime _3hoursBefore = LocalDateTime.now().minusHours(3);
            long allNeededToRemove = authenticationStateDao.findCountOfOlderThan(_3hoursBefore);
            logger.info(String.format("Authentication State Cleaner found %s rows to remove", allNeededToRemove));

            int deleted = authenticationStateDao.deleteOlderThan(_3hoursBefore);

            logger.info(String.format("Authentication State Cleaner deleted %s rows", deleted));
        } catch (Exception e) {
            logger.error("Unexpected error while cleaning Authentication State", e);
        } finally {
        }
    }

    protected AuthenticatedUserDao newAuthenticationStateDao() {
        return authenticatedUserDao;
    }

    @Async
    public synchronized void runAsync() {
        run();
    }
}
