package ee.hm.dop.service.content;

import ee.hm.dop.config.Configuration;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.OriginalPictureDao;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.EducationalContextC;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.service.author.AuthorService;
import ee.hm.dop.service.author.PublisherService;
import ee.hm.dop.service.content.enums.GetMaterialStrategy;
import ee.hm.dop.service.content.enums.SearchIndexStrategy;
import ee.hm.dop.service.files.PictureService;
import ee.hm.dop.service.metadata.CrossCurricularThemeService;
import ee.hm.dop.service.metadata.KeyCompetenceService;
import ee.hm.dop.service.reviewmanagement.ChangeProcessStrategy;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeService;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.useractions.PeerReviewService;
import ee.hm.dop.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;
import static java.time.LocalDateTime.now;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
@Transactional
public class MaterialService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private MaterialDao materialDao;
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private ReviewableChangeService reviewableChangeService;
    @Inject
    private Configuration configuration;
    @Inject
    private AuthorService authorService;
    @Inject
    private PublisherService publisherService;
    @Inject
    private PeerReviewService peerReviewService;
    @Inject
    private KeyCompetenceService keyCompetenceService;
    @Inject
    private CrossCurricularThemeService crossCurricularThemeService;
    @Inject
    private FirstReviewAdminService firstReviewAdminService;
    @Inject
    private MaterialGetter materialGetter;
    @Inject
    private OriginalPictureDao originalPictureDao;
    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private PortfolioService portfolioService;
    @Inject
    private PictureService pictureService;


    public Material findByRepository(Repository repository, String repositoryIdentifier) {
        return materialDao.findByRepository(repository, repositoryIdentifier);
    }

    public Material createMaterialBySystemUser(Material material, SearchIndexStrategy strategy) {
        return createMaterial(material, null, strategy);
    }

    public Material createMaterial(Material material, User creator, SearchIndexStrategy strategy) {
        mustBeNewMaterial(material);

        material.setSource(UrlUtil.processURL(material.getSource()));
        cleanPeerReviewUrls(material);
        material.setCreator(creator);
        material.setVisibility(Visibility.PRIVATE);
        if (UserUtil.isPublisher(creator)) {
            material.setEmbeddable(true);
        }
        material.setRecommendation(null);
        Material createdMaterial = createOrUpdate(material);
        if (strategy.updateIndex()) {
            solrEngineService.updateIndex();
        }
        firstReviewAdminService.save(createdMaterial);
        return createdMaterial;
    }

    private void mustBeNewMaterial(Material material) {
        if (material.getId() != null || materialWithSameSourceExists(material)) {
            throw new IllegalArgumentException(String.format("Error creating Material, material already exists: id %s, source: %s", material.getId(), material.getSource()));
        }
    }

    public void delete(Material material) {
        materialDao.delete(material);
    }

    public Material updateBySystem(Material material, SearchIndexStrategy strategy) {
        return update(material, null, strategy);
    }

    public Material update(Material material, User changer, SearchIndexStrategy strategy) {
        ValidatorUtil.mustHaveId(material, material != null ? material.getId() : null);
        Material originalMaterial = materialGetter.get(material.getId(), changer);
        mustHavePermission(changer, originalMaterial);
        mustBeValid(originalMaterial, changer);
        String sourceBefore = originalMaterial.getSource();
        material.setSource(UrlUtil.processURL(material.getSource()));
        mustHaveUniqueSource(material);
        material.setVisibility(material.getVisibility());

        cleanPeerReviewUrls(material);
        if (!UserUtil.isAdmin(changer)) {
            material.setRecommendation(originalMaterial.getRecommendation());
        }
        material.setId(originalMaterial.getId());
        material.setRepository(originalMaterial.getRepository());
        material.setViews(originalMaterial.getViews());
        material.setAdded(originalMaterial.getAdded());
        material.setUpdated(now());

        material.setFirstReviews(originalMaterial.getFirstReviews());
        material.setUnReviewed(originalMaterial.getUnReviewed());
        material.setImproperContents(originalMaterial.getImproperContents());
        material.setImproper(originalMaterial.getUnReviewed());
        material.setReviewableChanges(originalMaterial.getReviewableChanges());
        material.setChanged(originalMaterial.getChanged());

        Material updatedMaterial = createOrUpdate(material);
        boolean materialChanged = reviewableChangeService.processChanges(updatedMaterial, changer, sourceBefore, ChangeProcessStrategy.processStrategy(material));
        if (materialChanged) {
            updatedMaterial = materialDao.createOrUpdate(updatedMaterial);
        }
        if (strategy.updateIndex()) {
            solrEngineService.updateIndex();
        }
        return updatedMaterial;
    }

    private void mustHaveUniqueSource(Material material) {
        if (materialWithSameSourceExists(material)) {
            throw new IllegalArgumentException("Error updating Material: material with given source already exists");
        }
    }

    private void mustHavePermission(User changer, Material originalMaterial) {
        if (changer != null && !UserUtil.isAdminOrModerator(changer) && !UserUtil.isCreator(originalMaterial, changer)) {
            throw ValidatorUtil.permissionError();
        }
    }

    private void cleanPeerReviewUrls(Material material) {
        List<PeerReview> peerReviews = material.getPeerReviews();
        if (isNotEmpty(peerReviews)) {
            for (PeerReview peerReview : peerReviews) {
                if (!peerReview.getUrl().contains(configuration.getString(SERVER_ADDRESS))) {
                    peerReview.setUrl(UrlUtil.processURL(peerReview.getUrl()));
                }
            }
        }
    }

    private boolean materialWithSameSourceExists(Material material) {
        if (material.getSource() == null && material.getUploadedFile() != null) return false;
        List<Material> materialsWithGivenSource = materialGetter.getBySource(material.getSource(), GetMaterialStrategy.INCLUDE_DELETED);
        return isNotEmpty(materialsWithGivenSource) &&
                materialsWithGivenSource.stream()
                        .noneMatch(m -> m.getId().equals(material.getId()));
    }

    private void mustBeValid(Material originalMaterial, User changer) {
        if (originalMaterial == null) {
            throw new IllegalArgumentException("Error updating Material: material does not exist.");
        }

        if (originalMaterial.getRepository() != null && changer != null && !UserUtil.isAdminOrModerator(changer)) {
            throw new IllegalArgumentException("Normal user can't update external repository material");
        }
    }

    private Material createOrUpdate(Material material) {
        Long materialId = material.getId();
        boolean isNew = materialId == null;

        if (isNew) {
            logger.info("Creating material");
            material.setAdded(now());
        } else {
            logger.info("Updating material, id: " + materialId);
        }
        TextFieldUtil.cleanTextFields(material);
        checkKeyCompetences(material);
        checkCrossCurricularThemes(material);
        setAuthors(material);
        setPublishers(material);
        setPeerReviews(material);
        if (CollectionUtils.isEmpty(material.getTaxons()) || cantSet(material)) {
            material.setKeyCompetences(null);
            material.setCrossCurricularThemes(null);
        }

        if (material.getPicture() != null) {
            if (material.getPicture().getId() == null) {
                material.setPicture(null);
            } else {
                OriginalPicture originalPicture = originalPictureDao.findById(material.getPicture().getId());
                material.getPicture().setData(originalPicture.getData());
                material.getPicture().setName(originalPicture.getName());
            }
        }

        Material createOrUpdate = materialDao.createOrUpdate(material);
        logCreated(isNew, createOrUpdate);
        return createOrUpdate;
    }

    private void logCreated(boolean isNew, Material createOrUpdate) {
        if (isNew) {
            logger.info("Created material, id: " + createOrUpdate.getId());
        } else {
            logger.info("Updated material, id: " + createOrUpdate.getId());
        }
        if (createOrUpdate.getRepositoryIdentifier() != null) {
            if (isNew) {
                logger.info("Created material, repositoryIdentifier: " + createOrUpdate.getRepositoryIdentifier());
            } else {
                logger.info("Updated material, repositoryIdentifier: " + createOrUpdate.getRepositoryIdentifier());
            }
        }
    }

    private boolean cantSet(Material material) {
        List<EducationalContext> educationalContexts = material.getTaxons().stream()
                .map(TaxonUtils::getEducationalContext)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return CollectionUtils.isEmpty(educationalContexts) || educationalContexts.stream().noneMatch(e -> EducationalContextC.BASIC_AND_SECONDARY.contains(e.getName()));
    }

    private void checkKeyCompetences(Material material) {
        if (isNotEmpty(material.getKeyCompetences())) {
            for (int i = 0; i < material.getKeyCompetences().size(); i++) {
                if (material.getKeyCompetences().get(i).getId() == null) {
                    KeyCompetence keyCompetenceByName = keyCompetenceService.findKeyCompetenceByName(material.getKeyCompetences().get(i).getName());
                    if (keyCompetenceByName == null) {
                        throw new IllegalArgumentException();
                    }
                    material.getKeyCompetences().set(i, keyCompetenceByName);
                }
            }
        }
    }

    private void checkCrossCurricularThemes(Material material) {
        if (isNotEmpty(material.getCrossCurricularThemes())) {
            for (int i = 0; i < material.getCrossCurricularThemes().size(); i++) {
                if (material.getCrossCurricularThemes().get(i).getId() == null) {
                    CrossCurricularTheme crossCurricularTheme = crossCurricularThemeService.getThemeByName(material.getCrossCurricularThemes().get(i).getName());
                    if (crossCurricularTheme == null) {
                        throw new IllegalArgumentException();
                    }
                    material.getCrossCurricularThemes().set(i, crossCurricularTheme);
                }
            }
        }
    }

    private void setPublishers(Material material) {
        List<Publisher> publishers = material.getPublishers();
        if (publishers != null) {
            for (int i = 0; i < publishers.size(); i++) {
                Publisher publisher = publishers.get(i);
                if (publisher != null && publisher.getName() != null) {
                    Publisher returnedPublisher = publisherService.getPublisherByName(publisher.getName());
                    if (returnedPublisher != null) {
                        publishers.set(i, returnedPublisher);
                    } else {
                        returnedPublisher = publisherService.createPublisher(publisher.getName(),
                                publisher.getWebsite());
                        publishers.set(i, returnedPublisher);
                    }
                } else {
                    publishers.remove(i);
                }
            }
            material.setPublishers(publishers);
        }
    }

    private void setAuthors(Material material) {
        List<Author> authors = material.getAuthors();
        if (authors != null) {
            for (int i = 0; i < authors.size(); i++) {
                Author author = authors.get(i);
                if (author != null && author.getName() != null && author.getSurname() != null) {
                    Author returnedAuthor = authorService.getAuthorByFullName(author.getName(), author.getSurname());
                    if (returnedAuthor != null) {
                        authors.set(i, returnedAuthor);
                    } else {
                        returnedAuthor = authorService.createAuthor(author.getName(), author.getSurname());
                        authors.set(i, returnedAuthor);
                    }
                } else {
                    authors.remove(i);
                }
            }
            material.setAuthors(authors);
        }
    }

    private void setPeerReviews(Material material) {
        List<PeerReview> peerReviews = material.getPeerReviews();
        if (peerReviews != null) {
            for (int i = 0; i < peerReviews.size(); i++) {
                PeerReview peerReview = peerReviews.get(i);
                PeerReview returnedPeerReview = peerReviewService.createPeerReview(peerReview.getUrl());
                if (returnedPeerReview != null) {
                    peerReviews.set(i, returnedPeerReview);
                }
            }
        }
        material.setPeerReviews(peerReviews);
    }

    public List<Portfolio> getRelatedPortfolios(Long id) {
        return portfolioDao.getRelatedPortfolios(id);
    }

    public List<Material> getAllMaterialIfLearningObjectIsPortfolio(LearningObject lo) {
        List<Material> portfolioMaterials = new ArrayList<>();
        Portfolio portfolio = portfolioService.findById(lo.getId());

        if (portfolio != null) {
            List<ChapterBlock> chapterBlocks = new ArrayList<>();
            logger.info(String.format("Starting to handle portfolio with id --- %s", portfolio.getId()));
            logger.info(String.format("Trying to get chapters: %s", portfolio.getChapters()));
            if (portfolio.getChapters().size() > 0) {
                portfolio.getChapters().forEach(chapter -> {
                    if (chapter.getBlocks().size() > 0)
                        chapterBlocks.addAll(chapter.getBlocks());
                });
                if (chapterBlocks.size() > 0)
                    chapterBlocks.forEach(chapterBlock -> portfolioMaterials.addAll(getMaterialFromChapterBlock(chapterBlock.getHtmlContent())));
            }
        }

        return portfolioMaterials;
    }

    private List<Material> getMaterialFromChapterBlock(String htmlContent) {
        List<Material> materials = new ArrayList<>();
        List<String> partsOfChapterBlock = Arrays.asList(htmlContent.split("<div"));

        partsOfChapterBlock.forEach(partOfChapterBlock -> {
            if (partOfChapterBlock.contains("material") && partOfChapterBlock.contains("data-id")) {
                Long id = parseMaterialId(partOfChapterBlock);
                if (id > 0) {
                    materials.add(materialDao.findById(id));
                }
            }
        });

        return materials;
    }

    private long parseMaterialId(String partOfChapterBlock) {
        return parseIdFromChapterBlock(partOfChapterBlock);
    }

    public boolean materialHasUnacceptableLicense(Material material, boolean userIsLoggingIn) {
        LicenseType licenseType = material.getLicenseType();
        if (licenseType == null) {
            return true;
        }

        boolean materialPictureHasUnacceptableLicense = material.getPicture() != null && pictureService.pictureHasUnAcceptableLicence(material.getPicture());

        if (userIsLoggingIn) {
            return !licenseType.getName().equals("CCBY") &&
                    !licenseType.getName().equals("CCBYSA") &&
                    !licenseType.getName().equals("CCBYSA30") ||
                    materialPictureHasUnacceptableLicense;
        } else {
            return licenseType.getId() == 1 ||
                    licenseType.getId() == 4 ||
                    licenseType.getId() == 7 ||
                    materialPictureHasUnacceptableLicense;
        }
    }

    private long parseIdFromChapterBlock(String partOfChapterBlock) {
        StringBuilder idString = new StringBuilder();
        String unparsedId = partOfChapterBlock.split("data-id")[1];

        unparsedId.chars().forEach(c -> {
            if (Character.isDigit((char) c)) {
                idString.append((char) c);
            }
        });

        return Long.parseLong(idString.toString());
    }
}
