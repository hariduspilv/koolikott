package ee.hm.dop.service.synchronizer;

import ee.hm.dop.service.SitemapService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
public class GenerateSitemapXmlsExecutor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateSitemapXmlsExecutor.class);

    @Inject
    private SitemapService sitemapService;

    public synchronized void run() {

        try {
            logger.info("SITEMAP: sitemapIndex generator started");
            long startOfSitemapGen = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

            int nrOfUrl = sitemapService.createSitemap();

            long endOfSitemapGen = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            long durationOfSitemapGen = endOfSitemapGen - startOfSitemapGen;

            logger.info("SITEMAP: sitemapIndex generator ended");
            logger.info("SITEMAP: added " + nrOfUrl + " urls");
            logger.info("SITEMAP: sitemapIndex generator took " + durationOfSitemapGen + " seconds");

        } catch (Exception e) {
            logger.error("Unexpected error while generating sitemaps", e);
        }
    }

    @Async
    @Transactional
    public synchronized void runAsync() {
        run();
    }
}
