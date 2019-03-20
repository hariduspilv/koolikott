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
    public List<Integer> getInstitutionsAndUpdateDb() throws MalformedURLException, DocumentException {
        URL docUrl = new URL(configuration.getString(ConfigurationProperties.XROAD_EHIS_INSTITUTIONS_LIST));
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
