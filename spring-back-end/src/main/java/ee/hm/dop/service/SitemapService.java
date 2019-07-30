package ee.hm.dop.service;

import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.SitemapIndexGenerator;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
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

@Service
@Transactional
public class SitemapService {

    private static final String BASE_URL = "http://localhost:3001"; //TODO
    private static final String PATH_NAME = "/home/marekr/Proged/front-end"; //TODO
    private static final String FAQ = "faq";
    private static final String USER_MANUALS = "usermanuals";
    private static final String TERMS = "terms";
    private static final List<String> URLS = Arrays.asList(FAQ, USER_MANUALS, TERMS);

    @Inject
    private SitemapServiceCache sitemapServiceCache;
    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private UserDao userDao;

    public int createSitemap() throws MalformedURLException, ParseException {
        File file = new File(PATH_NAME);
        int nrOfUrl = 0;

        WebSitemapGenerator webSitemapGeneratorPortfolio = generatePrefixSpecific("/portfolios", file);

        for (Portfolio portfolio : portfolioDao.findAll()) {
            WebSitemapUrl wsmUrl = new Options(BASE_URL + "/portfolio/" + portfolio.getId() + "-" + portfolio.getTitle())
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
            WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(BASE_URL + "/material/" + materialTitle.getId() + "-" + materialTitle.getText())
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
            WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(BASE_URL + "/" + user.getUsername())
                    .lastMod(String.valueOf(LocalDateTime.now())) //TODO
                    .priority(0.9)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();

            webSitemapGeneratorUsers.addUrl(wsmUrl);
            nrOfUrl++;
        }
        webSitemapGeneratorUsers.write();

        WebSitemapGenerator webSitemapGeneratorOther = generatePrefixSpecific("/otherUrls", file);
        for (String url : URLS) {
            WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(BASE_URL + "/" + url)
                    .lastMod(String.valueOf(LocalDateTime.now())) // TODO
                    .priority(0.9)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();
            webSitemapGeneratorOther.addUrl(wsmUrl);
            nrOfUrl++;
        }
        webSitemapGeneratorOther.write();

        File outFile = new File(PATH_NAME + "/sitemaps_index.xml");
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
                .builder(BASE_URL, file)
                .fileNamePrefix(prefix)
//                .gzip(true)
                .build();
    }
}