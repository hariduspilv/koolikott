package ee.hm.dop.service.content;

<<<<<<< HEAD
import ee.hm.dop.dao.BrokenContentDao;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.dao.UserLikeDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.EducationalContextC;
import ee.hm.dop.model.interfaces.ILearningObject;
import ee.hm.dop.model.interfaces.IMaterial;
import ee.hm.dop.model.interfaces.IPortfolio;
=======
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.EducationalContextC;
import ee.hm.dop.model.enums.Visibility;
>>>>>>> new-develop
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.service.author.AuthorService;
import ee.hm.dop.service.author.PublisherService;
import ee.hm.dop.service.content.enums.GetMaterialStrategy;
import ee.hm.dop.service.content.enums.SearchIndexStrategy;
<<<<<<< HEAD
import ee.hm.dop.service.learningObject.PermissionItem;
import ee.hm.dop.service.metadata.CrossCurricularThemeService;
import ee.hm.dop.service.metadata.KeyCompetenceService;
=======
import ee.hm.dop.service.metadata.CrossCurricularThemeService;
import ee.hm.dop.service.metadata.KeyCompetenceService;
import ee.hm.dop.service.reviewmanagement.ChangedLearningObjectService;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
>>>>>>> new-develop
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.useractions.PeerReviewService;
import ee.hm.dop.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
<<<<<<< HEAD
import org.joda.time.DateTime;
=======
>>>>>>> new-develop
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
<<<<<<< HEAD
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joda.time.DateTime.now;

public class MaterialService implements PermissionItem {
=======
import static org.joda.time.DateTime.now;

public class MaterialService {
>>>>>>> new-develop

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private MaterialDao materialDao;
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
<<<<<<< HEAD
    private BrokenContentDao brokenContentDao;
    @Inject
=======
>>>>>>> new-develop
    private ChangedLearningObjectService changedLearningObjectService;
    @Inject
    private Configuration configuration;
    @Inject
<<<<<<< HEAD
    private ReducedLearningObjectDao reducedLearningObjectDao;
    @Inject
=======
>>>>>>> new-develop
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
<<<<<<< HEAD
    private FirstReviewService firstReviewService;

    public Material get(Long materialId, User loggedInUser) {
        if (UserUtil.isAdminOrModerator(loggedInUser)) {
            return materialDao.findById(materialId);
        }
        return materialDao.findByIdNotDeleted(materialId);
    }

    public void increaseViewCount(Material material) {
        material.setViews(material.getViews() + 1);
        createOrUpdate(material);
        solrEngineService.updateIndex();
    }
=======
    private FirstReviewAdminService firstReviewAdminService;
    @Inject
    private MaterialGetter materialGetter;
>>>>>>> new-develop

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

<<<<<<< HEAD
    //todo admin functionality
    public void delete(Long materialID, User loggedInUser) {
        UserUtil.mustBeModeratorOrAdmin(loggedInUser);

        Material originalMaterial = materialDao.findByIdNotDeleted(materialID);
        ValidatorUtil.mustHaveEntity(originalMaterial);

        materialDao.delete(originalMaterial);
        solrEngineService.updateIndex();
    }

    //todo admin functionality
    public void restore(Material material, User loggedInUser) {
        UserUtil.mustBeAdmin(loggedInUser);

        Material originalMaterial = validateAndFindWithDeleted(material);

        materialDao.restore(originalMaterial);
        solrEngineService.updateIndex();
    }

    public Material validateAndFindNotDeleted(Material material) {
        return ValidatorUtil.findValid(material, (Function<Long, Material>) materialDao::findByIdNotDeleted);
    }

    public Material validateAndFindWithDeleted(Material material) {
        return ValidatorUtil.findValid(material, (Function<Long, Material>) materialDao::findById);
    }

=======
>>>>>>> new-develop
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
<<<<<<< HEAD
        Material originalMaterial = get(material.getId(), changer);
=======
        Material originalMaterial = materialGetter.get(material.getId(), changer);
>>>>>>> new-develop
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
<<<<<<< HEAD

        List<Material> materialsWithGivenSource = getBySource(material.getSource(), GetMaterialStrategy.INCLUDE_DELETED);
=======
        List<Material> materialsWithGivenSource = materialGetter.getBySource(material.getSource(), GetMaterialStrategy.INCLUDE_DELETED);
>>>>>>> new-develop
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

<<<<<<< HEAD
    public SearchResult getByCreatorResult(User creator, int start, int maxResults) {
        List<Searchable> userFavorites = new ArrayList<>(getByCreator(creator, start, maxResults));
        return new SearchResult(userFavorites, getByCreatorSize(creator), start);
    }

    public List<ReducedLearningObject> getByCreator(User creator, int start, int maxResults) {
        return reducedLearningObjectDao.findMaterialByCreator(creator, start, maxResults);
    }

    public long getByCreatorSize(User creator) {
        return materialDao.findByCreatorSize(creator);
    }

=======
>>>>>>> new-develop
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
<<<<<<< HEAD

        Material updatedMaterial = materialDao.createOrUpdate(material);
        if (isNew) {
            firstReviewService.save(updatedMaterial);
=======
        material.setVisibility(Visibility.PUBLIC);

        Material updatedMaterial = materialDao.createOrUpdate(material);
        if (isNew) {
            firstReviewAdminService.save(updatedMaterial);
>>>>>>> new-develop
        }

        return updatedMaterial;
    }

    private Material applyRestrictions(Material material) {
<<<<<<< HEAD
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

    public BrokenContent addBrokenMaterial(Material material, User loggedInUser) {
        Material originalMaterial = validateAndFindNotDeleted(material);

        BrokenContent brokenContent = new BrokenContent();
        brokenContent.setCreator(loggedInUser);
        brokenContent.setMaterial(originalMaterial);
        return brokenContentDao.update(brokenContent);
    }

    public Boolean hasSetBroken(long materialId, User loggedInUser) {
        return isNotEmpty(brokenContentDao.findByMaterialAndUser(materialId, loggedInUser));
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
=======
        if (CollectionUtils.isEmpty(material.getTaxons()) || cantSet(material)) {
            material.setKeyCompetences(null);
            material.setCrossCurricularThemes(null);
        }
        return material;
    }

    private boolean cantSet(Material material) {
        List<EducationalContext> educationalContexts = material.getTaxons().stream()
                .map(TaxonUtils::getEducationalContext)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return CollectionUtils.isEmpty(educationalContexts) || educationalContexts.stream().noneMatch(e -> EducationalContextC.BASIC_AND_SECONDARY.contains(e.getName()));
    }

    private void checkKeyCompetences(Material material) {
>>>>>>> new-develop
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

<<<<<<< HEAD
    public void checkCrossCurricularThemes(Material material) {
=======
    private void checkCrossCurricularThemes(Material material) {
>>>>>>> new-develop
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

<<<<<<< HEAD
    public void setPublishers(Material material) {
=======
    private void setPublishers(Material material) {
>>>>>>> new-develop
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

<<<<<<< HEAD
    public void setAuthors(Material material) {
=======
    private void setAuthors(Material material) {
>>>>>>> new-develop
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

<<<<<<< HEAD
    public void setPeerReviews(Material material) {
=======
    private void setPeerReviews(Material material) {
>>>>>>> new-develop
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
<<<<<<< HEAD

    @Override
    public boolean canView(User user, ILearningObject learningObject) {
        return isNotPrivate(learningObject) || UserUtil.isAdmin(user);
    }

    @Override
    public boolean canInteract(User user, ILearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof IMaterial)) return false;
        return isPublic(learningObject) || UserUtil.isAdmin(user);
    }

    @Override
    public boolean canUpdate(User user, ILearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof IMaterial)) return false;
        return UserUtil.isAdminOrModerator(user) || UserUtil.isCreator(learningObject, user);
    }

    @Override
    public boolean isPublic(ILearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof IMaterial)) return false;
        //todo true simulates that visibility is public, waiting for db change
        return true && !learningObject.isDeleted();
    }

    @Override
    public boolean isNotPrivate(ILearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof IMaterial)) return false;
        //todo true simulates that visibility is public, waiting for db change
        return true && !learningObject.isDeleted();
    }

=======
>>>>>>> new-develop
}
