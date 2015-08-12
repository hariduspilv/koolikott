package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.LicenseTypeDAO;
import ee.hm.dop.model.LicenseType;

public class LicenseTypeService {

    @Inject
    private LicenseTypeDAO licenseTypeDAO;

    public List<LicenseType> getAllLicenseTypes() {
        return licenseTypeDAO.findAll();
    }

}
