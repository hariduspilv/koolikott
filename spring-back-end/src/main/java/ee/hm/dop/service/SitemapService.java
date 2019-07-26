package ee.hm.dop.service;

import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.SitemapIndexGenerator;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import ee.hm.dop.dao.LanguageStringDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SitemapService {

    private static final Logger logger = LoggerFactory.getLogger(SitemapService.class);

    private static final String BASE_URL = "http://localhost:3001"; //TODO

    @Inject
    private LanguageStringDao languageStringDao;

    public void createSitemap() throws MalformedURLException, ParseException {
        String pathname = "/home/marekr/Proged/spring-back-end/src/main/resources"; //TODO

        File file = new File(pathname);
        String urlString;
        int nrOfUrl = 0;

        logger.info("SITEMAP: started portfolio_daily generator");
        WebSitemapGenerator webSitemapGeneratorPortfolio = WebSitemapGenerator
                .builder(BASE_URL, file)
                .fileNamePrefix("/portfolio_daily")
//                .gzip(true)
                .build();

        List<String> portfolioTitles = languageStringDao.findAllPortfolioTitles();
        for (String s : portfolioTitles) {
            urlString = BASE_URL + "/portfolio_daily/" + s;
            WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(urlString)
                    .lastMod(String.valueOf(LocalDateTime.now()))
                    .priority(0.9)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();
            webSitemapGeneratorPortfolio.addUrl(wsmUrl);
            nrOfUrl++;
        }
        webSitemapGeneratorPortfolio.write();


        logger.info("SITEMAP: started material_daily generator");
        WebSitemapGenerator webSitemapGeneratorMaterial = WebSitemapGenerator
                .builder(BASE_URL, file)
                .fileNamePrefix("/material_daily")
//                .gzip(true)
                .build();

        List<String> materialTitles = languageStringDao.findAllMaterialTitles();
        for (String s : materialTitles) {
            urlString = BASE_URL + "/material_daily/" + s;
            WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(urlString)
                    .lastMod(String.valueOf(LocalDateTime.now()))
                    .priority(0.9)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();
            webSitemapGeneratorMaterial.addUrl(wsmUrl);
            nrOfUrl++;
        }
        webSitemapGeneratorMaterial.write();

        File outFile = new File(pathname + "/sitemaps_index.xml");
        SitemapIndexGenerator sitemapIndexGenerator = new SitemapIndexGenerator(BASE_URL, outFile);
        sitemapIndexGenerator.addUrl(BASE_URL + "/portfolio_daily.xml");
        sitemapIndexGenerator.addUrl(BASE_URL + "/material_daily.xml");
        sitemapIndexGenerator.write();

        logger.info("SITEMAP: sitemapIndex generator ended");
        logger.info("SITEMAP: added " + nrOfUrl + " urls");
    }
}
