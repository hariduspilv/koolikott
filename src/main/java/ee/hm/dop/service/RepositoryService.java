package ee.hm.dop.service;

import static java.lang.String.format;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.dao.RepositoryDAO;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Repository;
import ee.hm.dop.oaipmh.MaterialIterator;
import ee.hm.dop.oaipmh.RepositoryManager;
import ee.hm.dop.utils.DbUtils;

public class RepositoryService {

    private static final int MAX_IMPORT_BEFORE_EMPTY_CACHE = 50;

    private static final Logger logger = LoggerFactory.getLogger(RepositoryService.class);

    @Inject
    private RepositoryDAO repositoryDAO;

    @Inject
    private RepositoryManager repositoryManager;

    @Inject
    private MaterialService materialService;

    public List<Repository> getAllRepositorys() {
        return repositoryDAO.findAll();
    }

    public void updateRepository(Repository repository) {
        logger.info(format("Updating materials for %s", repository));

        long failedMaterials = 0;
        long successfulMaterials = 0;
        long start = System.currentTimeMillis();

        MaterialIterator materials;
        try {
            materials = repositoryManager.getMaterialsFrom(repository);
        } catch (Exception e) {
            logger.error(format("Error while getting material from %s. No material will be updated.", repository), e);
            return;
        }

        int count = 0;
        while (materials.hasNext()) {
            try {
                Material material = materials.next();
                handleMaterial(material);
                successfulMaterials++;
            } catch (Exception e) {
                logger.error("An error occurred while getting the next material from repository.", e);
                failedMaterials++;
            }

            if (++count >= MAX_IMPORT_BEFORE_EMPTY_CACHE) {
                DbUtils.emptyCache();
                count = 0;
            }
        }

        repository.setLastSynchronization(DateTime.now());
        updateRepositoryData(repository);

        long end = System.currentTimeMillis();
        String message = "Updating materials took %s milliseconds. Successfully downloaded %s"
                + " materials and %s materials failed to download of total %s";
        logger.info(format(message, end - start, successfulMaterials, failedMaterials, successfulMaterials
                + failedMaterials));
    }

    private void handleMaterial(Material material) {
        if (material != null) {
            materialService.createMaterial(material);
        }
    }

    public void updateRepositoryData(Repository repository) {
        repositoryDAO.updateRepository(repository);
    }
}
