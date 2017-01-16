package ee.hm.dop.service;

import static ee.hm.dop.service.TranslationService.filterByLanguage;
import static ee.hm.dop.utils.ConfigurationProperties.MAX_FEED_ITEMS;
import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.LanguageDAO;
import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.dao.PortfolioDAO;
import ee.hm.dop.dao.TranslationDAO;
import ee.hm.dop.dao.VersionDAO;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Version;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.commons.configuration.Configuration;

public class AtomFeedService {

    private int maxFeedItems;

    @Inject
    private Configuration configuration;

    @Inject
    private MaterialDAO materialDAO;

    @Inject
    private PortfolioDAO portfolioDAO;

    @Inject
    private VersionDAO versionDAO;

    @Inject
    private TranslationDAO translationDAO;

    @Inject
    private LanguageDAO languageDAO;

    private Factory factory;

    private Feed feed;

    private String lang;

    private List<Entry> entryList = new ArrayList<>();

    public AtomFeedService() {
        Abdera abdera = new Abdera();
        factory = abdera.getFactory();
        feed = factory.newFeed();
    }

    public Feed getFeed(String lang) {
        this.lang = lang;
        maxFeedItems = configuration.getInt(MAX_FEED_ITEMS);

        feed.sortEntriesByUpdated(true);
        feed.setId(translateString("FEED_ID"));
        feed.setTitle(translateString("FEED_TITLE"));
        feed.setIcon(format("%s/favicon.ico", configuration.getString(SERVER_ADDRESS)));
        feed.setLogo(format("%s/koolikott.png", configuration.getString(SERVER_ADDRESS)));

        checkVersion();

        return getFeedEntries();
    }

    private Feed getFeedEntries() {
        for (Material material : materialDAO.findNewestMaterials(maxFeedItems, 0)) {
            Entry entry = materialToEntry(material);
            if (entry != null)
                entryList.add(entry);
        }

        for (Version version : versionDAO.getAllVersions()) {
            Entry entry = versionToEntry(version);
            if (entry != null)
                entryList.add(entry);
        }

        for (Portfolio portfolio : portfolioDAO.findNewestPortfolios(maxFeedItems, 0)) {
            Entry entry = portfolioToEntry(portfolio);
            if (entry != null)
                entryList.add(entry);
        }

        for (Entry entry : entryList.subList(0, maxFeedItems)) {
            feed.addEntry(entry);
        }

        return feed;
    }

    private Entry materialToEntry(Material material) {
        Entry entry = factory.newEntry();
        entry.setId(format("material:%d", material.getId()));
        String title = translateMaterialTitle(material.getTitles());
        if (title != null)
            entry.setTitle(title);
        else
            return null;
        for (Author author : material.getAuthors()) {
            entry.addAuthor(format("%s %s", author.getName(), author.getSurname()));
        }
        entry.setUpdated(material.getAdded().toDate());
        entry.addLink(format("%s/material?id=%s", configuration.getString(SERVER_ADDRESS), material.getId().toString()));
        return entry;
    }

    private Entry portfolioToEntry(Portfolio portfolio) {
        Entry entry = factory.newEntry();
        entry.setId(format("portfolio:%d", portfolio.getId()));
        entry.setTitle(format(translateString("FEED_PORTFOLIO_TITLE"), portfolio.getTitle()));
        entry.addAuthor(format("%s %s", portfolio.getOriginalCreator().getName(), portfolio.getOriginalCreator().getSurname()));
        entry.addLink(format("%s/portfolio?id=%s", configuration.getString(SERVER_ADDRESS), portfolio.getId()));
        entry.setUpdated(portfolio.getAdded().toDate());
        return entry;
    }

    private Entry versionToEntry(Version version) {
        Entry entry = factory.newEntry();
        entry.setId(format("version:%d", version.getId()));
        entry.setTitle(format(translateString("FEED_VERSION_TITLE"), version.getVersion()));
        entry.addLink("https://github.com/hariduspilv/koolikott/blob/master/CHANGELOG.md");
        entry.setUpdated(version.getReleased().toDate());
        return entry;
    }

    private void checkVersion() {
        String projectVersion = configuration.getString("version");
        Version persistedVersion = versionDAO.getLatestVersion();
        if (persistedVersion == null || !projectVersion.equals(persistedVersion.getVersion()))
            versionDAO.addVersion(projectVersion);
    }

    private String translateMaterialTitle(List<LanguageString> titles) {
        String titleTranslation = translateString("FEED_MATERIAL_TITLE");

        if (filterByLanguage(titles, lang) != null) {
            return format(titleTranslation, filterByLanguage(titles, lang).getText());
        }
        if (filterByLanguage(titles, "est") != null) {
            return format(titleTranslation, filterByLanguage(titles, "est").getText());
        }
        if (!titles.isEmpty()) {
            return format(titleTranslation, titles.get(0).getText());
        }

        return null;
    }

    private String translateString(String toTranslate) {
        Long langCode = languageDAO.findByCode(lang).getId();
        return translationDAO.getTranslationByKeyAndLangcode(toTranslate, langCode);
    }

}
