package ee.hm.dop.service.synchronizer;

import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.ChapterBlock;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Repository;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.synchronizer.oaipmh.SynchronizationAudit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Slf4j
@Service
@Transactional
public class SynchronizeMaterialsExecutor {

    public static final String MATERIAL_REGEX = "class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"";
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private RepositoryService repositoryService;


    public static void main(String[] args) {
        String s = "<div class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"10\"></div><div class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"9511\"></div><div class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"5553\"></div><div class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"10491\"></div><h3 class=\"subchapter\">sassis</h3><div class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"10613\"></div><div class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"8206\"></div><h3 class=\"subchapter\">asdsadsadadad</h3>";
        Pattern pattern = Pattern.compile("class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"");
        Matcher matcher = pattern.matcher(s);
        List<String > s123 = new ArrayList<>();
        while (matcher.find()){
            s123.add(matcher.group());
        }
        System.out.println(s123);
    }

    public synchronized void run() {

        //run only once
        //getAllPortfolios
        List<Portfolio> portfolios = new ArrayList<>();
        List<String> results = new ArrayList<>();
        Pattern pattern = Pattern.compile(MATERIAL_REGEX);
        for (Portfolio portfolio : portfolios) {
            for (Chapter chapter : portfolio.getChapters()) {
                for (ChapterBlock block : chapter.getBlocks()) {
                    if (StringUtils.isNotBlank(block.getHtmlContent())) {
                        Matcher matcher = pattern.matcher(block.getHtmlContent());
                        while (matcher.find()) {
                            results.add(matcher.group());
                        }
                    }
                }
            }
            //domagic
            //transform strings to material ids
            List<Long> fromFrontIds = new ArrayList<>();
            //save
        }


        List<SynchronizationAudit> audits = new ArrayList<>();
        try {
            RepositoryService repositoryService = newRepositoryService();
            List<Repository> repositories = repositoryService.getAllUsedRepositories();

            log.info(format("Synchronizing %d repositories...", repositories.size()));

            for (Repository repository : repositories) {
                log.info(format("Synchonizing repository %S:", repository));
                //For every repository make a new transaction - one fail will not roll back all repositories

                SynchronizationAudit audit = repositoryService.synchronize(repository);
                if (audit != null) audits.add(audit);
            }

            log.info("Synchronization repository service finished execution.");
        } catch (Exception e) {
            log.error("Unexpected error while synchronizing materials.", e);
        } finally {
            if (audits.stream().anyMatch(SynchronizationAudit::changeOccured)) {
                log.info("Solr full import after synchronizing all materials");
                solrEngineService.fullImport();
            } else {
                log.info("Synchronizing materials doesn't need solr update");
            }
        }
    }

    protected RepositoryService newRepositoryService() {
        return repositoryService;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized void runAsync() {
        run();
    }
}
