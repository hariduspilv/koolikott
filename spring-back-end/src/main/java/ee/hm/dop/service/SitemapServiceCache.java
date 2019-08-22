package ee.hm.dop.service;


import ee.hm.dop.dao.MaterialTitleDao;
import ee.hm.dop.model.MaterialTitle;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class SitemapServiceCache {

    @Inject
    private MaterialTitleDao materialTitleDao;

    @Cacheable("SitemapService_findMaterialsTitles")
    public List<MaterialTitle> findAllMaterialsTitles(){
        return materialTitleDao.findAllMaterialTitles();
    }
}
