package ee.hm.dop.service.metadata;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.model.Language;

import javax.inject.Inject;
import java.util.List;

public class MaterialMetadataService {

    @Inject
    private MaterialDao materialDao;

    public List<Language> getLanguagesUsedInMaterials() {
        return materialDao.findLanguagesUsedInMaterials();
    }
}
