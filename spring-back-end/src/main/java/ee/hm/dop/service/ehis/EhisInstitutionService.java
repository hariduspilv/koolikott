package ee.hm.dop.service.ehis;

import ee.hm.dop.config.Configuration;
import ee.hm.dop.dao.InstitutionEhisDao;
import ee.hm.dop.model.ehis.InstitutionEhis;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static ee.hm.dop.utils.ConfigurationProperties.XROAD_EHIS_INSTITUTIONS_LIST;

@Service
public class EhisInstitutionService {

    @Autowired
    private EhisInstitutionParser ehisInstitutionParser;
    @Autowired
    private Configuration configuration;
    @Autowired
    private InstitutionEhisDao institutionEhisDao;

    public InstitutionEhis getInstitutionEhisById(Long id) {
        return institutionEhisDao.findById(id);
    }

    public List<InstitutionEhis> getInstitutionEhisById(List<Long> id) {
        return institutionEhisDao.findById(id);
    }

    public List<Integer> getInstitutionsAndUpdateDb() throws MalformedURLException, DocumentException {
        URL docUrl = new URL(configuration.getString(XROAD_EHIS_INSTITUTIONS_LIST));
        return ehisInstitutionParser.parseAndUpdateDb(docUrl);
    }

    public List<InstitutionEhis> findAll() {
        return institutionEhisDao.findAll();
    }

    public List<String> getInstitutionAreas() {
        return institutionEhisDao.getInstitutionAreas();
    }

    public List<InstitutionEhis> getInstitutionPerArea(String area) {
        return institutionEhisDao.getInstitutionPerArea(area);
    }
}
