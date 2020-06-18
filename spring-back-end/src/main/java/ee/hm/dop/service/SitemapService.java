package ee.hm.dop.service;

import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.SitemapIndexGenerator;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import ee.hm.dop.config.Configuration;
import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.model.MaterialTitle;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.service.files.UploadedFileService;
import ee.hm.dop.service.files.enums.FileDirectory;
import ee.hm.dop.utils.tokenizer.TitleUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;
import static ee.hm.dop.utils.ConfigurationProperties.SITEMAPS_DIRECTORY;

@Service
@Transactional
public class SitemapService {

    private static final String TERMS = "kasutustingimused";
    private static final String USER_MANUALS = "videojuhendid";
    private static final String FAQ = "kkk";
    private static final String PROFILE = "profiil";

    private static final String IMPROPER = "toolaud/teatatud-oppevara";
    private static final String UNREVIEWED = "toolaud/uus-oppevara";
    private static final String CHANGES = "toolaud/muudetud-oppevara";
    private static final String SENTEMAILS = "toolaud/saadetud-teated";

    private static final String USERS = "toolaud/kasutajad";
    private static final String DELETED = "toolaud/kustutatud-oppevara";
    private static final String STATS_EXPERT = "toolaud/ekspertide-statistika";
    private static final String GDPR = "toolaud/gdpr";
    private static final String USER_MANUALS_ADMIN = "toolaud/videojuhendid";

    private static final String MATERIAL = "/oppematerjal/";
    private static final String PORTFOLIO = "/kogumik/";

    public static final String REST_SITEMAP = "/sitemapFile";

    private static final List<String> URLS = Arrays.asList(FAQ, USER_MANUALS, TERMS, PROFILE, IMPROPER, UNREVIEWED, CHANGES, SENTEMAILS, USERS, DELETED, STATS_EXPERT, GDPR, USER_MANUALS_ADMIN);

    @Inject
    private Configuration configuration;
    @Inject
    private SitemapServiceCache sitemapServiceCache;
    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private UploadedFileService uploadedFileService;
    @Inject
    private Environment environment;

    public int createSitemap() throws MalformedURLException, ParseException, UnsupportedEncodingException {

        String property = environment.getProperty("server.servlet.contextPath");
        final String BASE_URL = configuration.getString(SERVER_ADDRESS);
        File file = new File(configuration.getString(SITEMAPS_DIRECTORY));
        int nrOfUrl = 0;
        List<String> urls = new ArrayList<>();

        WebSitemapGenerator webSitemapGeneratorPortfolio = generatePrefixSpecific("portfolios", file);

        for (Portfolio portfolio : portfolioDao.getAllPortfoliosDeletedExcluded()) {
            WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(BASE_URL + PORTFOLIO + portfolio.getId() + "-" + TitleUtils.replaceChars(portfolio.getTitle()))
                    .lastMod(portfolio.getUpdated() == null ? String.valueOf(portfolio.getAdded()) : String.valueOf(portfolio.getUpdated()))
                    .priority(0.9)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();
            webSitemapGeneratorPortfolio.addUrl(wsmUrl);
            nrOfUrl++;
        }
        String portfolioXml = String.join("", webSitemapGeneratorPortfolio.writeAsStrings());
        uploadedFileService.uploadXmlFile(portfolioXml, "portfolios.xml", FileDirectory.SITEMAPS);
        urls.add(String.join("", webSitemapGeneratorPortfolio.writeAsStrings()));

        WebSitemapGenerator webSitemapGeneratorMaterial = generatePrefixSpecific("materials", file);

        List<MaterialTitle> materialTitles = sitemapServiceCache.findAllMaterialsTitles();
        for (MaterialTitle materialTitle : materialTitles) {
            WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(BASE_URL + MATERIAL + materialTitle.getId() + "-" + TitleUtils.replaceChars(materialTitle.getText()))
                    .lastMod(materialTitle.getTime())
                    .priority(0.9)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();
            webSitemapGeneratorMaterial.addUrl(wsmUrl);
            nrOfUrl++;
        }
        String materialXml = String.join("", webSitemapGeneratorMaterial.writeAsStrings());
        uploadedFileService.uploadXmlFile(materialXml, "materials.xml", FileDirectory.SITEMAPS);
        urls.add(String.join("", webSitemapGeneratorMaterial.writeAsStrings()));

        WebSitemapGenerator webSitemapGeneratorUsers = generatePrefixSpecific("users", file);

        for (String user : learningObjectDao.findUsersLearningobjectCreators()) {
            WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(BASE_URL + "/" + user)
                    .lastMod(String.valueOf(LocalDateTime.now()))
                    .priority(0.8)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();

            webSitemapGeneratorUsers.addUrl(wsmUrl);
            nrOfUrl++;
        }

        String usersXml = String.join("", webSitemapGeneratorUsers.writeAsStrings());
        uploadedFileService.uploadXmlFile(usersXml, "users.xml", FileDirectory.SITEMAPS);
        urls.add(String.join("", webSitemapGeneratorUsers.writeAsStrings()));

        WebSitemapGenerator webSitemapGeneratorOther = generatePrefixSpecific("otherUrls", file);
        for (String url : URLS) {
            WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(BASE_URL + "/" + url)
                    .lastMod(String.valueOf(LocalDateTime.now()))
                    .priority(0.9)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();
            webSitemapGeneratorOther.addUrl(wsmUrl);
            nrOfUrl++;
        }
        String othersXml = String.join("", webSitemapGeneratorOther.writeAsStrings());
        uploadedFileService.uploadXmlFile(othersXml, "otherUrls.xml", FileDirectory.SITEMAPS);
        urls.add(String.join("", webSitemapGeneratorOther.writeAsStrings()));

        File outFile = new File(configuration.getString(SITEMAPS_DIRECTORY) + "sitemapIndex.xml");

        SitemapIndexGenerator sitemapIndexGenerator = new SitemapIndexGenerator(BASE_URL + property + REST_SITEMAP, outFile);
        sitemapIndexGenerator.addUrl(BASE_URL + property + "/portfolios.xml");
        sitemapIndexGenerator.addUrl(BASE_URL + property + "/materials.xml");
        sitemapIndexGenerator.addUrl(BASE_URL + property + "/otherUrls.xml");
        sitemapIndexGenerator.addUrl(BASE_URL + property + "/users.xml");

        String indexXml = String.join("", sitemapIndexGenerator.writeAsString());
        uploadedFileService.uploadXmlFile(indexXml, "sitemapIndex.xml", FileDirectory.SITEMAPS);
        urls.add(String.join("", sitemapIndexGenerator.writeAsString()));

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