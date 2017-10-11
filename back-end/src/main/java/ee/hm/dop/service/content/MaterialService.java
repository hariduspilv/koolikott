package ee.hm.dop.service.content;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.EducationalContextC;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.service.author.AuthorService;
import ee.hm.dop.service.author.PublisherService;
import ee.hm.dop.service.content.enums.GetMaterialStrategy;
import ee.hm.dop.service.content.enums.SearchIndexStrategy;
import ee.hm.dop.service.metadata.CrossCurricularThemeService;
import ee.hm.dop.service.metadata.KeyCompetenceService;
import ee.hm.dop.service.reviewmanagement.ChangedLearningObjectService;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.useractions.PeerReviewService;
import ee.hm.dop.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.joda.time.DateTime.now;

public class MaterialService  {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private MaterialDao materialDao;
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private ChangedLearningObjectService changedLearningObjectService;
    @Inject
    private Configuration configuration;
    @Inject
    private ReducedLearningObjectDao reducedLearningObjectDao;
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

    public Material get(Long materialId, User loggedInUser) {
        if (UserUtil.isAdminOrModerator(loggedInUser)) {
            return materialDao.findById(materialId);
        }
        return materialDao.findByIdNotDeleted(materialId);
    }

    public Material createMaterialBySystemUser(Material material, SearchIndexStrategy strategy) {
        return createMaterial(material, null, strategy);
    }

    public Material createMaterial(Material material, User creator, SearchIndexStrategy strategy) {
        mustBeNewMaterial(material);

        material.setSource(UrlUtil.processURL(material.getSource()));
        cleanPeerReviewUrls(material);
        material.setCreator(creator);
        if (UserUtil.isPublisher(creator)) {
            material.setEmbeddable(true);
        }
        material.setRecommendation(null);
        Material createdMaterial = createOrUpdate(material);
        if (strategy.updateIndex()) {
            solrEngineService.updateIndex();
        }
        return createdMaterial;
    }

    private void mustBeNewMaterial(Material material) {
        if (material.getId() != null || materialWithSameSourceExists(material)) {
            throw new IllegalArgumentException("Error creating Material, material already exists.");
        }
    }

    public Material validateAndFindNotDeleted(Material material) {
        return ValidatorUtil.findValid(material, (Function<Long, Material>) materialDao::findByIdNotDeleted);
    }

    public void delete(Material material) {
        materialDao.delete(material);
    }

    public Material updateBySystem(Material material, SearchIndexStrategy strategy) {
        return update(material, null, strategy);
    }

    public Material update(Material material, User changer, SearchIndexStrategy strategy) {
        ValidatorUtil.mustHaveId(material);
        material.setSource(UrlUtil.processURL(material.getSource()));

        if (materialWithSameSourceExists(material)) {
            throw new IllegalArgumentException("Error updating Material: material with given source already exists");
        }

        cleanPeerReviewUrls(material);
        Material originalMaterial = get(material.getId(), changer);
        validateMaterialUpdate(originalMaterial, changer);
        if (!UserUtil.isAdmin(changer)) {
            material.setRecommendation(originalMaterial.getRecommendation());
        }
        material.setRepository(originalMaterial.getRepository());
        material.setViews(originalMaterial.getViews());
        material.setAdded(originalMaterial.getAdded());
        material.setUpdated(now());

        Material updatedMaterial = getUpdatedMaterial(material, changer, strategy, originalMaterial);
        processChanges(updatedMaterial);
        return updatedMaterial;
    }

    private Material getUpdatedMaterial(Material material, User changer, SearchIndexStrategy strategy, Material originalMaterial) {
        //Null changer is the automated updating of materials during synchronization
        if (changer == null || UserUtil.isAdminOrModerator(changer) || UserUtil.isCreator(originalMaterial, changer)) {
            Material updatedMaterial = createOrUpdate(material);
            if (strategy.updateIndex()) {
                solrEngineService.updateIndex();
            }
            return updatedMaterial;
        }
        throw ValidatorUtil.permissionError();
    }

    private void processChanges(Material material) {
        List<ChangedLearningObject> changes = changedLearningObjectService.getAllByLearningObject(material.getId());
        if (isNotEmpty(changes)) {
            for (ChangedLearningObject change : changes) {
                if (!changedLearningObjectService.learningObjectHasThis(material, change)) {
                    changedLearningObjectService.removeChangeById(change.getId());
                }
            }
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

        List<Material> materialsWithGivenSource = getBySource(material.getSource(), GetMaterialStrategy.INCLUDE_DELETED);
        return isNotEmpty(materialsWithGivenSource) &&
                materialsWithGivenSource.stream()
                        .noneMatch(m -> m.getId().equals(material.getId()));
    }

    private void validateMaterialUpdate(Material originalMaterial, User changer) {
        if (originalMaterial == null) {
            throw new IllegalArgumentException("Error updating Material: material does not exist.");
        }

        if (originalMaterial.getRepository() != null && changer != null && !UserUtil.isAdminOrModerator(changer)) {
            throw new IllegalArgumentException("Normal user can't update external repository material");
        }
    }

    public SearchResult getByCreatorResult(User creator, int start, int maxResults) {
        List<Searchable> userFavorites = new ArrayList<>(getByCreator(creator, start, maxResults));
        return new SearchResult(userFavorites, getByCreatorSize(creator), start);
    }

    private List<ReducedLearningObject> getByCreator(User creator, int start, int maxResults) {
        return reducedLearningObjectDao.findMaterialByCreator(creator, start, maxResults);
    }

    public long getByCreatorSize(User creator) {
        return materialDao.findByCreatorSize(creator);
    }

    private Material createOrUpdate(Material material) {
        Long materialId = material.getId();
        boolean isNew;
        if (materialId == null) {
            logger.info("Creating material");
            material.setAdded(now());
            isNew = true;
        } else {
            logger.info("Updating material");
            isNew = false;
        }
        TextFieldUtil.cleanTextFields(material);
        checkKeyCompetences(material);
        checkCrossCurricularThemes(material);
        setAuthors(material);
        setPublishers(material);
        setPeerReviews(material);
        material = applyRestrictions(material);
        material.setVisibility(Visibility.PUBLIC);

        Material updatedMaterial = materialDao.createOrUpdate(material);
        if (isNew) {
            firstReviewAdminService.save(updatedMaterial);
        }

        return updatedMaterial;
    }

    private Material applyRestrictions(Material material) {
        boolean areKeyCompetencesAndCrossCurricularThemesAllowed = false;

        if (CollectionUtils.isNotEmpty(material.getTaxons())) {
            List<EducationalContext> educationalContexts = material.getTaxons().stream()
                    .map(TaxonUtils::getEducationalContext)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            for (EducationalContext educationalContext : educationalContexts) {
                if (EducationalContextC.BASIC_AND_SECONDARY.contains(educationalContext.getName())) {
                    areKeyCompetencesAndCrossCurricularThemesAllowed = true;
                }
            }
        }

        if (!areKeyCompetencesAndCrossCurricularThemesAllowed) {
            material.setKeyCompetences(null);
            material.setCrossCurricularThemes(null);
        }

        return material;
    }

    public List<Material> getBySource(String materialSource, GetMaterialStrategy getMaterialStrategy) {
        materialSource = UrlUtil.getURLWithoutProtocolAndWWW(UrlUtil.processURL(materialSource));
        checkLink(materialSource);
        return materialDao.findBySource(materialSource, getMaterialStrategy);
    }

    public Material getOneBySource(String materialSource, GetMaterialStrategy getMaterialStrategy) {
        materialSource = UrlUtil.getURLWithoutProtocolAndWWW(UrlUtil.processURL(materialSource));
        checkLink(materialSource);
        return materialDao.findOneBySource(materialSource, getMaterialStrategy);
    }

    private void checkLink(String materialSource) {
        if (materialSource == null) {
            throw new RuntimeException("No material source link provided");
        }
    }

    public void checkKeyCompetences(Material material) {
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

    public void checkCrossCurricularThemes(Material material) {
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

    public void setPublishers(Material material) {
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

    public void setAuthors(Material material) {
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

    public void setPeerReviews(Material material) {
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
}
