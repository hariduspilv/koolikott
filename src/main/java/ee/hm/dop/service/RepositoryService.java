package ee.hm.dop.service;

import static java.lang.String.format;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.dao.RepositoryDAO;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Repository;
import ee.hm.dop.oaipmh.MaterialIterator;
import ee.hm.dop.oaipmh.RepositoryManager;
import ee.hm.dop.oaipmh.SynchronizationAudit;
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

    @Inject
    private MaterialDAO materialDAO;

    @Inject
    private PictureService pictureService;

    @Inject
    private SearchEngineService searchEngineService;

    public List<Repository> getAllRepositorys() {
        List<Repository> repositories = repositoryDAO.findAll();

        if (repositories == null) {
            repositories = Collections.emptyList();
        }

        return repositories;
    }

    public void synchronize(Repository repository) {
        logger.info(format("Updating materials for %s", repository));

        SynchronizationAudit audit = new SynchronizationAudit();

        long start = System.currentTimeMillis();
        DateTime startSyncDateTime;

        MaterialIterator materials;
        try {
            materials = repositoryManager.getMaterialsFrom(repository);
            startSyncDateTime = DateTime.now();
        } catch (Exception e) {
            logger.error(format("Error while getting material from %s. No material will be updated.", repository), e);
            return;
        }

        int count = 0;
        while (materials.hasNext()) {
            try {
                Material material = materials.next();
                if (material != null) {
                    handleMaterial(repository, material, audit);
                    audit.successfullyDownloaded();

                    if (material.isDeleted()) {
                        audit.deletedMaterialDownloaded();
                    }
                }
            } catch (Exception e) {
                logger.error("An error occurred while getting the next material from repository.", e);
                audit.failedToDownload();
            }

            count = getCount(count);
        }

        repository.setLastSynchronization(startSyncDateTime);
        updateRepositoryData(repository);

        long end = System.currentTimeMillis();
        String message = "Updating materials took %s milliseconds. Successfully downloaded %s"
                + " materials (of which %s are deleted materials) and %s materials failed to download of total %s";
        logger.info(format(message, end - start, audit.getSuccessfullyDownloaded(), audit.getDeletedMaterialsDownloaded(),
                audit.getFailedToDownload(), audit.getSuccessfullyDownloaded() + audit.getFailedToDownload()));
        logger.info(format("%s new materials were created, %s existing materials were updated and %s existing materials were deleted",
                audit.getNewMaterialsCreated(), audit.getExistingMaterialsUpdated(), audit.getExistingMaterialsDeleted()));

        updateSolrIndex();
    }

    private void updateSolrIndex() {
        logger.info("Updating Search Engine index...");
        searchEngineService.updateIndex();
    }

    private int getCount(int count) {
        if (++count >= MAX_IMPORT_BEFORE_EMPTY_CACHE) {
            DbUtils.emptyCache();
            count = 0;
        }
        return count;
    }

    private void handleMaterial(Repository repository, Material material, SynchronizationAudit audit) {
        Material existentMaterial = materialDAO.findByRepositoryAndRepositoryIdentifier(repository,
                material.getRepositoryIdentifier());

        material.setRepository(repository);
        if (repository.isEstonianPublisher()) {
            material.setEmbeddable(true);
        }

        if (existentMaterial != null) {
            updateMaterial(material, existentMaterial, audit);
        } else if (!material.isDeleted()) {
            createMaterial(material);
            audit.newMaterialCreated();
        }
    }

    private void createMaterial(Material material) {
        createPicture(material);
        materialService.createMaterial(material, null, false);
    }

    private void createPicture(Material material) {
        if (material.getPicture() != null) {
            Picture picture = pictureService.create(material.getPicture());
            material.setPicture(picture);
        }
    }

    private void updateMaterial(Material material, Material existentMaterial, SynchronizationAudit audit) {
        if (material.isDeleted()) {
            materialService.delete(existentMaterial);
            audit.existingMaterialDeleted();
        } else {
            material.setId(existentMaterial.getId());
            createPicture(material);
            materialService.update(material, null);
            audit.existingMaterialUpdated();
        }
    }

    public void updateRepositoryData(Repository repository) {
        repositoryDAO.updateRepository(repository);
    }
}
