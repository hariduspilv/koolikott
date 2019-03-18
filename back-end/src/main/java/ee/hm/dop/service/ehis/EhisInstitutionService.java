package ee.hm.dop.service.ehis;

import ee.hm.dop.dao.InstitutionEhisDao;
import ee.hm.dop.model.ehis.InstitutionEhis;
import org.apache.commons.configuration2.Configuration;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class EhisInstitutionService {

    private static Logger logger = LoggerFactory.getLogger(EhisInstitutionService.class);

    @Inject
    private EhisInstitutionParser ehisInstitutionParser;
    @Inject
    private Configuration configuration;

    @Inject
    private InstitutionEhisDao institutionEhisDao;

    public InstitutionEhis getInstitutionEhisById(Long id) {
        return institutionEhisDao.findById(id);
    }

    public List<InstitutionEhis> getInstitutionEhisById(List<Long> id) {
        return institutionEhisDao.findById(id);
    }

    public void getInstitutionsAndUpdateDb() throws MalformedURLException, DocumentException {
        URL docUrl = new URL("http://enda.ehis.ee/avaandmed/rest/oppeasutused/-/-/-/-/-/-/-/-/-/0/0/XML");

//        URL docUrl = new URL(configuration.getString(XROAD_EHIS_INSTITUTIONS_LIST));
        ehisInstitutionParser.parseAndUpdateDb(docUrl);
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
