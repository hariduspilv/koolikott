package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.EducationalContextDAO;
import ee.hm.dop.model.EducationalContext;

/**
 * Created by mart.laus on 6.08.2015.
 */
public class EducationalContextService {

    @Inject
    private EducationalContextDAO educationalContextDAO;

    public EducationalContext getEducationalContextByName(String name) {
        return educationalContextDAO.findEducationalContextByName(name);
    }
}
