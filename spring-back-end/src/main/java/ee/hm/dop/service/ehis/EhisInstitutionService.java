package ee.hm.dop.service.ehis;

import ee.hm.dop.config.Configuration;
import ee.hm.dop.dao.InstitutionEhisDao;
import ee.hm.dop.model.ehis.InstitutionEhis;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static ee.hm.dop.utils.ConfigurationProperties.XROAD_EHIS_INSTITUTIONS_LIST;

@Service
public class EhisInstitutionService {

    private static Logger logger = LoggerFactory.getLogger(EhisInstitutionService.class);

    @Autowired
    private EhisInstitutionParser ehisInstitutionParser;
    @Autowired
    private Configuration configuration;
    @Autowired
    private InstitutionEhisDao institutionEhisDao;

    public void getInstitutionsAndUpdateDb() throws MalformedURLException, DocumentException {
        URL docUrl = new URL(configuration.getString(XROAD_EHIS_INSTITUTIONS_LIST));
        ehisInstitutionParser.parseAndUpdateDb(docUrl);
    }

    public List<InstitutionEhis> findAll() {
        return institutionEhisDao.findAll();
    }
}
