package ee.hm.dop.service.metadata;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.model.Language;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class MaterialMetadataService {

    @Inject
    private MaterialDao materialDao;

    public List<Language> getLanguagesUsedInMaterials() {
        return materialDao.findLanguagesUsedInMaterials();
    }
}
