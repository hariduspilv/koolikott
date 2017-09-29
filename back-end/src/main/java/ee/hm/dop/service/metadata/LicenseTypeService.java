package ee.hm.dop.service.metadata;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.LicenseTypeDao;
import ee.hm.dop.model.LicenseType;

public class LicenseTypeService {

    @Inject
    private LicenseTypeDao licenseTypeDao;

    public List<LicenseType> getAllLicenseTypes() {
        return licenseTypeDao.findAll();
    }

}