package ee.hm.dop.service.synchronizer;

import ee.hm.dop.dao.RepositoryDao;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.RepositoryURL;
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.service.content.enums.SearchIndexStrategy;
import ee.hm.dop.service.files.PictureSaver;
import ee.hm.dop.service.synchronizer.oaipmh.MaterialIterator;
import ee.hm.dop.service.synchronizer.oaipmh.RepositoryManager;
import ee.hm.dop.service.synchronizer.oaipmh.SynchronizationAudit;
import ee.hm.dop.utils.UrlUtil;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

import static ee.hm.dop.service.synchronizer.MergeUtil.mergeTwoObjects;
import static java.lang.String.format;

@Service
@Transactional
public class RepositoryService {

    private static final int BATCH_SIZE = 50;
    private static final Logger logger = LoggerFactory.getLogger(RepositoryService.class);

    @Inject
    private RepositoryDao repositoryDao;
    @Inject
    private RepositoryManager repositoryManager;
    @Inject
    private MaterialService materialService;
    @Inject
    private PictureSaver pictureSaver;

    public List<Repository> getAllRepositories() {
        return repositoryDao.findAll();
    }

    public List<Repository> getAllUsedRepositories() {
        return repositoryDao.findByFieldList("used", true);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SynchronizationAudit synchronize(Repository repository) {
        logStart(repository);

        long start = System.currentTimeMillis();

        MaterialIteratorAndDate materialIteratorAndDate = getMaterialIterator(repository);
        if (materialIteratorAndDate == null) return null;

        SynchronizationAudit audit = synchronize(repository, materialIteratorAndDate.getIterator());

        repository.setLastSynchronization(materialIteratorAndDate.getSyncDate());
        repositoryDao.updateRepository(repository);

        logEnd(audit, start);
        return audit;
    }

    private MaterialIteratorAndDate getMaterialIterator(Repository repository) {
        try {
            MaterialIterator materials = repositoryManager.getMaterialsFrom(repository);
            return new MaterialIteratorAndDate(materials, LocalDateTime.now());
        } catch (Exception e) {
            logger.error(format("Error while getting material from %s. No material will be updated.", repository), e);
            return null;
        }
    }

    private SynchronizationAudit synchronize(Repository repository, MaterialIterator materials) {
        SynchronizationAudit audit = new SynchronizationAudit();
        int count = 0;
        while (materials.hasNext()) {
            try {
                Material material = materials.next();

                if (material != null) {
                    logger.info("Trying to update or create next material with repo id: " + material.getRepositoryIdentifier());

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
            count = incrementCountAndFlush(count);
        }
        return audit;
    }

    private void logStart(Repository repository) {
        logger.info(format("Updating materials for %s", repository));
    }

    private void logEnd(SynchronizationAudit audit, long start) {
        long end = System.currentTimeMillis();
        logger.info(format("Updating materials took %s milliseconds. Successfully downloaded %s"
                        + " materials (of which %s are deleted materials) and %s materials failed to download of total %s", end - start, audit.getSuccessfullyDownloaded(), audit.getDeletedMaterialsDownloaded(),
                audit.getFailedToDownload(), audit.getSuccessfullyDownloaded() + audit.getFailedToDownload()));
        logger.info(format("%s new materials were created, %s existing materials were updated and %s existing materials were deleted", audit.getNewMaterialsCreated(), audit.getExistingMaterialsUpdated(), audit.getExistingMaterialsDeleted()));
    }

    private int incrementCountAndFlush(int count) {
        if (++count >= BATCH_SIZE) {
            repositoryDao.flush();
            count = 0;
        }
        return count;
    }

    private void handleMaterial(Repository repository, Material material, SynchronizationAudit audit) {
        Material existentMaterial = materialService.findByRepository(repository, material.getRepositoryIdentifier());

        material.setRepository(repository);
        if (repository.isContentIsEmbeddable()) material.setEmbeddable(true);

        if (existentMaterial != null) {
            MaterialHandlingStrategy strategy = materialHandlingStrategy(repository, existentMaterial);
            updateMaterial(material, existentMaterial, audit, strategy);
        } else if (!material.isDeleted()) {
            createMaterial(material, audit);
        } else {
            logger.error("Material set as deleted, not updating or creating, repository id: " + material.getRepositoryIdentifier());
        }

        logger.info("Material handled, repository id: " + material.getRepositoryIdentifier());
    }

    private MaterialHandlingStrategy materialHandlingStrategy(Repository repository, Material existentMaterial) {
        return MaterialHandlingStrategy.of(isFromSameRepo(repository, existentMaterial));
    }

    boolean isFromSameRepo(Repository repository, Material existentMaterial) {
        String domainName = UrlUtil.tryToGetDomainName(existentMaterial.getSource());
        return StringUtils.isNotBlank(domainName) &&
                repository.getRepositoryURLs().stream()
                        .map(RepositoryURL::getBaseURL)
                        .map(UrlUtil::tryToGetDomainName)
                        .filter(Objects::nonNull)
                        .anyMatch(r -> r.equals(domainName));
    }

    private void createMaterial(Material material, SynchronizationAudit audit) {
        logger.info("Creating material, with repo id: " + material.getRepositoryIdentifier());
        createPicture(material);
        materialService.createMaterialBySystemUser(material, SearchIndexStrategy.SKIP_UPDATE);
        audit.newMaterialCreated();
    }

    private void createPicture(Material material) {
        if (material.getPicture() != null) {
            material.setPicture(pictureSaver.create(material.getPicture()));
        }
    }

    Material updateMaterial(Material newMaterial, Material existentMaterial, SynchronizationAudit audit, MaterialHandlingStrategy strategy) {
        if (strategy.isOtherRepo()) {
            logger.info("Updating material with external link - updating all fields that are currently null in DB");
            createPicture(newMaterial);
            return update(existentMaterial, newMaterial, audit);
        }

        if (strategy.isSameRepo()) {
            if (newMaterial.isDeleted()) {
                logger.info("Deleting material, as it was deleted in it's repository and is owned by the repo (has repo baseLink)");
                materialService.delete(existentMaterial);
                audit.existingMaterialDeleted();
                return null;
            }
            logger.info("Updating material with repository link - updating all fields, that are not null in the new imported material");
            createPicture(newMaterial);
            return update(newMaterial, existentMaterial, audit);
        }

        throw new IllegalStateException("unknown strategy");
    }

    private Material update(Material source, Material destination, SynchronizationAudit audit) {
        mergeTwoObjects(source, destination);
        Material updatedMaterial = materialService.updateBySystem(destination, SearchIndexStrategy.SKIP_UPDATE);
        audit.existingMaterialUpdated();
        return updatedMaterial;
    }
}
