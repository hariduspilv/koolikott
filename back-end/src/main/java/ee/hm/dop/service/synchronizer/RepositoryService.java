package ee.hm.dop.service.synchronizer;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.RepositoryDao;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.RepositoryURL;
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.service.content.enums.SearchIndexStrategy;
import ee.hm.dop.service.files.PictureSaver;
import ee.hm.dop.service.synchronizer.oaipmh.MaterialIterator;
import ee.hm.dop.service.synchronizer.oaipmh.RepositoryManager;
import ee.hm.dop.service.synchronizer.oaipmh.SynchronizationAudit;
import ee.hm.dop.utils.DbUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.List;

import static java.lang.String.format;

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
    private MaterialDao materialDao;
    @Inject
    private PictureSaver pictureSaver;

    public List<Repository> getAllRepositories() {
        return repositoryDao.findAll();
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
    }

    private int getCount(int count) {
        if (++count >= BATCH_SIZE) {
            DbUtils.emptyCache();
            count = 0;
        }
        return count;
    }

    private void handleMaterial(Repository repository, Material material, SynchronizationAudit audit) {
        Material existentMaterial = materialDao.findByRepository(repository, material.getRepositoryIdentifier());

        material.setRepository(repository);
        if (repository.isEstonianPublisher()) {
            material.setEmbeddable(true);
        }

        if (existentMaterial != null) {
            boolean isRepoMaterial = isRepoMaterial(repository, existentMaterial);
            updateMaterial(material, existentMaterial, audit, isRepoMaterial);
        } else if (!material.isDeleted()) {
            createMaterial(material);
            audit.newMaterialCreated();
        } else {
            logger.error("Material set as deleted, not updating or creating, repository id: " + material.getRepositoryIdentifier());
        }

        logger.info("Material handled, repository id: " + material.getRepositoryIdentifier());
    }

    boolean isRepoMaterial(Repository repository, Material existentMaterial) {
        String domainName = getDomainName(existentMaterial.getSource());
        return StringUtils.isNotBlank(domainName) &&
                repository.getRepositoryURLs().stream()
                        .map(RepositoryURL::getBaseURL)
                        .map(this::getDomainName)
                        .anyMatch(r -> r.equals(domainName));
    }

    String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            if (uri.getScheme() == null) {
                uri = new URI("http://" + url);
            }

            String domain = uri.getHost().trim();
            return domain.startsWith("www.") ? domain.substring(4) : domain;

        } catch (Exception e) {
            logger.error("Could not get domain name from material during synchronization - updating all metafields of the material");
            return null;
        }
    }

    private void createMaterial(Material material) {
        logger.info("Creating material, with repo id: ", material.getRepositoryIdentifier());
        createPicture(material);
        materialService.createMaterialBySystemUser(material, SearchIndexStrategy.SKIP_UPDATE);
    }

    private void createPicture(Material material) {
        if (material.getPicture() != null) {
            Picture picture = pictureSaver.create(material.getPicture());
            material.setPicture(picture);
        }
    }

    Material updateMaterial(Material newMaterial, Material existentMaterial, SynchronizationAudit audit, boolean isRepoMaterial) {
        Material updatedMaterial = null;

        if (isRepoMaterial) {
            if (newMaterial.isDeleted()) {
                logger.info("Deleting material, as it was deleted in it's repository and is owned by the repo (has repo baseLink)");
                materialService.delete(existentMaterial);
                audit.existingMaterialDeleted();
            } else {
                logger.info("Updating material with repository link - updating all fields, that are not null in the new imported material");
                createPicture(newMaterial);
                mergeTwoObjects(newMaterial, existentMaterial);

                updatedMaterial = materialService.updateBySystem(existentMaterial, SearchIndexStrategy.SKIP_UPDATE);
                audit.existingMaterialUpdated();
            }
        } else {
            logger.info("Updating material with external link - updating all fields that are currently null in DB");
            createPicture(newMaterial);
            mergeTwoObjects(existentMaterial, newMaterial);

            updatedMaterial = materialService.updateBySystem(newMaterial, SearchIndexStrategy.SKIP_UPDATE);
            audit.existingMaterialUpdated();
        }

        return updatedMaterial;
    }

    /**
     * Data from the source will be copied to the destination.
     * If the source data is not null or empty, then existing data in the destination will be overwritten
     *
     * @param source Data being copied to the destination
     * @param dest   Object into which data will be copied and overwritten
     */
    private void mergeTwoObjects(Object source, Object dest) {
        try {
            new BeanUtilsBean() {
                @Override
                public void copyProperty(Object dest, String name, Object value)
                        throws IllegalAccessException, InvocationTargetException {
                    if (value != null && !isEmpty(value)) {
                        super.copyProperty(dest, name, value);
                    }
                }
            }.copyProperties(dest, source);
        } catch (Exception e) {
            logger.error("Unable to merge existing material and downloaded material from the repository", e);
        }
    }

    private boolean isEmpty(Object value) {
        if (value instanceof List) {
            return ((List) value).isEmpty();
        } else {
            return value instanceof String && ((String) value).isEmpty();
        }
    }

    private void updateRepositoryData(Repository repository) {
        repositoryDao.updateRepository(repository);
    }
}
