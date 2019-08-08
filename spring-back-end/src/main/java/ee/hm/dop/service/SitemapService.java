package ee.hm.dop.service;

import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.SitemapIndexGenerator;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import ee.hm.dop.config.Configuration;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.model.MaterialTitle;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.redfin.sitemapgenerator.WebSitemapUrl.Options;
import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;
import static ee.hm.dop.utils.ConfigurationProperties.SITEMAP_PATH;

@Service
@Transactional
public class SitemapService {

    private static final String TERMS = "kasutustingimused";
    private static final String USER_MANUALS = "videojuhendid";
    private static final String FAQ = "kkk";
    private static final String PROFILE = "profiil";

    private static final String IMPROPER = "toolaud/sobimatu";
    private static final String UNREVIEWED = "toolaud/kontrollimata";
    private static final String CHANGES = "toolaud/muudetud";
    private static final String SENTEMAILS = "toolaud/saadetudKirjad";

    private static final String MODERATORS = "toolaud/aineeksperdid";
    private static final String RESTRICTED_USERS = "toolaud/piiratudKasutajad";
    private static final String DELETED = "toolaud/kustutatud";
    private static final String STATS_EXPERT = "toolaud/statistika/ekspert";
    private static final String GDPR = "toolaud/gdpr";
    private static final String USER_MANUALS_ADMIN = "toolaud/videojuhendidAdmin";

    private static final String MATERIAL = "/material/";
    private static final String PORTFOLIO = "/portfolio/";
    private static final String USER = "/user/";

    private static final List<String> URLS = Arrays.asList(FAQ, USER_MANUALS, TERMS, PROFILE, IMPROPER, UNREVIEWED, CHANGES, SENTEMAILS, MODERATORS, RESTRICTED_USERS, DELETED, STATS_EXPERT, GDPR, USER_MANUALS_ADMIN);

    @Inject
    private Configuration configuration;
    @Inject
    private SitemapServiceCache sitemapServiceCache;
    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private UserDao userDao;

    public int createSitemap() throws MalformedURLException, ParseException {
        final String BASE_URL = configuration.getString(SERVER_ADDRESS);
        File file = new File(configuration.getString(SITEMAP_PATH));
        int nrOfUrl = 0;

        WebSitemapGenerator webSitemapGeneratorPortfolio = generatePrefixSpecific("/portfolios", file);

        for (Portfolio portfolio : portfolioDao.findAll()) {
            WebSitemapUrl wsmUrl = new Options(BASE_URL + PORTFOLIO + portfolio.getId() + "-" + portfolio.getTitle())
                    .lastMod(portfolio.getUpdated() == null ? String.valueOf(portfolio.getAdded()) : String.valueOf(portfolio.getUpdated()))
                    .priority(0.9)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();
            webSitemapGeneratorPortfolio.addUrl(wsmUrl);
            nrOfUrl++;
        }
        webSitemapGeneratorPortfolio.write();

        WebSitemapGenerator webSitemapGeneratorMaterial = generatePrefixSpecific("/materials", file);

        List<MaterialTitle> materialTitles = sitemapServiceCache.findAllMaterialsTitles();
        for (MaterialTitle materialTitle : materialTitles) {
            WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(BASE_URL + MATERIAL + materialTitle.getId() + "-" + materialTitle.getText())
                    .lastMod(materialTitle.getTime())
                    .priority(0.9)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();
            webSitemapGeneratorMaterial.addUrl(wsmUrl);
            nrOfUrl++;
        }
        webSitemapGeneratorMaterial.write();

        WebSitemapGenerator webSitemapGeneratorUsers = generatePrefixSpecific("/users", file);
        for (User user : userDao.findAll()) {
            WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(BASE_URL + USER + user.getUsername())
                    .lastMod(String.valueOf(LocalDateTime.now()))
                    .priority(0.8)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();

            webSitemapGeneratorUsers.addUrl(wsmUrl);
            nrOfUrl++;
        }
        webSitemapGeneratorUsers.write();

        WebSitemapGenerator webSitemapGeneratorOther = generatePrefixSpecific("/otherUrls", file);
        for (String url : URLS) {
            WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(BASE_URL + "/" + url)
                    .lastMod(String.valueOf(LocalDateTime.now()))
                    .priority(0.9)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();
            webSitemapGeneratorOther.addUrl(wsmUrl);
            nrOfUrl++;
        }
        webSitemapGeneratorOther.write();

        File outFile = new File(configuration.getString(SITEMAP_PATH) + "/sitemaps_index.xml");
        SitemapIndexGenerator sitemapIndexGenerator = new SitemapIndexGenerator(BASE_URL, outFile);
        sitemapIndexGenerator.addUrl(BASE_URL + "/portfolios.xml");
        sitemapIndexGenerator.addUrl(BASE_URL + "/materials.xml");
        sitemapIndexGenerator.addUrl(BASE_URL + "/otherUrls.xml");
        sitemapIndexGenerator.addUrl(BASE_URL + "/users.xml");
        sitemapIndexGenerator.write();
        return nrOfUrl;
    }

    private WebSitemapGenerator generatePrefixSpecific(String prefix, File file) throws MalformedURLException {
        return WebSitemapGenerator
                .builder(configuration.getString(SERVER_ADDRESS), file)
                .fileNamePrefix(prefix)
//                .gzip(true)//TODO in case of need
                .build();
    }
}