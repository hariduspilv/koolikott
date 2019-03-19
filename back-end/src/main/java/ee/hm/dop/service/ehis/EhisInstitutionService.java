package ee.hm.dop.service.ehis;

import ee.hm.dop.dao.InstitutionEhisDao;
import ee.hm.dop.model.ehis.InstitutionEhis;
import ee.hm.dop.utils.ConfigurationProperties;
import org.apache.commons.configuration2.Configuration;
import org.dom4j.DocumentException;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class EhisInstitutionService {

    @Inject
    private EhisInstitutionParser ehisInstitutionParser;
    @Inject
    private Configuration configuration;
    @Inject
    private InstitutionEhisDao institutionEhisDao;

    public List<Integer> getInstitutionsAndUpdateDb() throws MalformedURLException, DocumentException {
        URL docUrl = new URL(configuration.getString(ConfigurationProperties.XROAD_EHIS_INSTITUTIONS_LIST));
        return ehisInstitutionParser.parseAndUpdateDb(docUrl);
    }

    public List<InstitutionEhis> findAll() {
        return institutionEhisDao.findAll();
    }

}
