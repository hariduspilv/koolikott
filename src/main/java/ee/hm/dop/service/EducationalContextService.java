package ee.hm.dop.service;

import ee.hm.dop.dao.EducationalContextDAO;
import ee.hm.dop.model.EducationalContext;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by mart.laus on 6.08.2015.
 */
public class EducationalContextService {

    @Inject
    private EducationalContextDAO educationalContextDAO;

    public List<EducationalContext> educationalContexts() {
        return educationalContextDAO.findAll();
    }

    public EducationalContext getEducationalContextByName(String name) {
        return educationalContextDAO.findEducationalContextByName(name);
    }
}
