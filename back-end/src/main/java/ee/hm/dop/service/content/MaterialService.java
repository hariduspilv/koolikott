package ee.hm.dop.service.content;

import ee.hm.dop.dao.BrokenContentDao;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.dao.UserLikeDao;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.ChangedLearningObject;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.PeerReview;
import ee.hm.dop.model.Publisher;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLike;
import ee.hm.dop.model.enums.EducationalContextC;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.service.useractions.PeerReviewService;
import ee.hm.dop.service.author.AuthorService;
import ee.hm.dop.service.author.PublisherService;
import ee.hm.dop.service.learningObject.LearningObjectHandler;
import ee.hm.dop.service.metadata.CrossCurricularThemeService;
import ee.hm.dop.service.metadata.KeyCompetenceService;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.TaxonUtils;
import ee.hm.dop.utils.UrlUtil;
import ee.hm.dop.utils.UserUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.http.util.TextUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joda.time.DateTime.now;

public class MaterialService implements LearningObjectHandler {

    private static final String DEFAULT_PROTOCOL = "http://";
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private MaterialDao materialDao;
    @Inject
    private UserLikeDao userLikeDao;
    @Inject
    private AuthorService authorService;
    @Inject
    private PublisherService publisherService;
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private BrokenContentDao brokenContentDao;
    @Inject
    private PeerReviewService peerReviewService;
    @Inject
    private KeyCompetenceService keyCompetenceService;
    @Inject
    private CrossCurricularThemeService crossCurricularThemeService;
    @Inject
    private ChangedLearningObjectService changedLearningObjectService;
    @Inject
    private Configuration configuration;
    @Inject
    private ReducedLearningObjectDao reducedLearningObjectDao;

    public Material get(long materialId, User loggedInUser) {
        if (UserUtil.isUserAdminOrModerator(loggedInUser)) {
            return materialDao.findById(materialId);
        }
        return materialDao.findByIdNotDeleted(materialId);
    }

    public void increaseViewCount(Material material) {
        material.setViews(material.getViews() + 1);
        createOrUpdate(material);

        solrEngineService.updateIndex();
    }

    public Material createMaterial(Material material, User creator, boolean updateSearchIndex) {
        if (material.getId() != null || materialWithSameSourceExists(material)) {
            throw new IllegalArgumentException("Error creating Material, material already exists.");
        }

        material.setSource(UrlUtil.processURL(material.getSource()));

        cleanPeerReviewUrls(material);

        material.setCreator(creator);

        if (creator != null && isUserPublisher(creator)) {
            material.setEmbeddable(true);
        }

        material.setRecommendation(null);

        Material createdMaterial = createOrUpdate(material);
        if (updateSearchIndex) {
            solrEngineService.updateIndex();
        }

        return createdMaterial;
    }

    private void checkKeyCompetences(Material material) {
        if (!CollectionUtils.isEmpty(material.getKeyCompetences())) {
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
        if (!CollectionUtils.isEmpty(material.getCrossCurricularThemes())) {
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

    public void delete(Long materialID, User loggedInUser) {
        Material originalMaterial = materialDao.findByIdNotDeleted(materialID);
        validateMaterialNotNull(originalMaterial);

        if (!UserUtil.isUserAdmin(loggedInUser) && !UserUtil.isUserModerator(loggedInUser)) {
            throw new RuntimeException("Logged in user must be an administrator or a moderator.");
        }

        materialDao.delete(originalMaterial);
        solrEngineService.updateIndex();
    }

    public void restore(Material material, User loggedInUser) {
        Material originalMaterial = materialDao.findById(material.getId());
        validateMaterialNotNull(originalMaterial);

        if (!UserUtil.isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Logged in user must be an administrator.");
        }

        materialDao.restore(originalMaterial);
        solrEngineService.updateIndex();
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

    public void addComment(Comment comment, Material material) {
        if (isEmpty(comment.getText())) {
            throw new RuntimeException("Comment is missing text.");
        }

        if (comment.getId() != null) {
            throw new RuntimeException("Comment already exists.");
        }

        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        validateMaterialNotNull(originalMaterial);

        comment.setAdded(DateTime.now());
        originalMaterial.getComments().add(comment);
        materialDao.createOrUpdate(originalMaterial);
    }

    public UserLike addUserLike(Material material, User loggedInUser, boolean isLiked) {
        validateMaterialAndIdNotNull(material);
        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        validateMaterialNotNull(originalMaterial);

        userLikeDao.deleteMaterialLike(originalMaterial, loggedInUser);

        UserLike like = new UserLike();
        like.setLearningObject(originalMaterial);
        like.setCreator(loggedInUser);
        like.setLiked(isLiked);
        like.setAdded(DateTime.now());

        return userLikeDao.update(like);
    }

    public Recommendation addRecommendation(Material material, User loggedInUser) {
        validateMaterialAndIdNotNull(material);

        validateUserIsAdmin(loggedInUser);

        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());

        validateMaterialNotNull(originalMaterial);

        Recommendation recommendation = new Recommendation();
        recommendation.setCreator(loggedInUser);
        recommendation.setAdded(DateTime.now());
        originalMaterial.setRecommendation(recommendation);

        originalMaterial = materialDao.createOrUpdate(originalMaterial);

        solrEngineService.updateIndex();

        return originalMaterial.getRecommendation();
    }

    public void removeRecommendation(Material material, User loggedInUser) {
        validateMaterialAndIdNotNull(material);

        validateUserIsAdmin(loggedInUser);

        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());

        validateMaterialNotNull(originalMaterial);

        originalMaterial.setRecommendation(null);

        materialDao.createOrUpdate(originalMaterial);

        solrEngineService.updateIndex();
    }

    public void removeUserLike(Material material, User loggedInUser) {
        validateMaterialAndIdNotNull(material);
        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        validateMaterialNotNull(originalMaterial);

        userLikeDao.deleteMaterialLike(originalMaterial, loggedInUser);
    }

    public UserLike getUserLike(Material material, User loggedInUser) {
        validateMaterialAndIdNotNull(material);

        return userLikeDao.findMaterialUserLike(material, loggedInUser);
    }

    public void delete(Material material) {
        materialDao.delete(material);
    }

    public Material update(Material material, User changer, boolean updateSearchIndex) {
        if (material == null || material.getId() == null) {
            throw new IllegalArgumentException("Material id parameter is mandatory");
        }

        material.setSource(UrlUtil.processURL(material.getSource()));

        if (materialWithSameSourceExists(material)) {
            throw new IllegalArgumentException("Error updating Material: material with given source already exists");
        }

        cleanPeerReviewUrls(material);

        Material originalMaterial = getMaterial(material, changer);
        validateMaterialUpdate(originalMaterial, changer);

        if (!UserUtil.isUserAdmin(changer)) {
            material.setRecommendation(originalMaterial.getRecommendation());
        }
        //Should not be able to update repository
        material.setRepository(originalMaterial.getRepository());

        // Should not be able to update view count
        material.setViews(originalMaterial.getViews());
        // Should not be able to update added date, must keep the original
        material.setAdded(originalMaterial.getAdded());
        material.setUpdated(now());

        Material updatedMaterial = null;
        //Null changer is the automated updating of materials during synchronization
        if (changer == null || UserUtil.isUserAdminOrModerator(changer) || isThisUserMaterial(changer, originalMaterial)) {
            updatedMaterial = createOrUpdate(material);
            if (updateSearchIndex) solrEngineService.updateIndex();
        }

        processChanges(updatedMaterial);

        return updatedMaterial;
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

    private Material getMaterial(Material material, User changer) {
        if (UserUtil.isUserAdminOrModerator(changer)) {
            return materialDao.findById(material.getId());
        }
        return materialDao.findByIdNotDeleted(material.getId());
    }

    private void cleanPeerReviewUrls(Material material) {
        List<PeerReview> peerReviews = material.getPeerReviews();
        if (peerReviews != null) {
            for (PeerReview peerReview : peerReviews) {
                if (!peerReview.getUrl().contains(configuration.getString(SERVER_ADDRESS))) {
                    peerReview.setUrl(UrlUtil.processURL(peerReview.getUrl()));
                }
            }
        }
    }

    private boolean materialWithSameSourceExists(Material material) {
        if (material.getSource() == null && material.getUploadedFile() != null) return false;

        List<Material> materialsWithGivenSource = getBySource(material.getSource(), true);
        if (isNotEmpty(materialsWithGivenSource)) {
            if (!listContainsMaterial(materialsWithGivenSource, material)) {
                return true;
            }
        }

        return false;
    }

    private boolean listContainsMaterial(List<Material> list, Material material) {
        return list.stream().anyMatch(m -> m.getId().equals(material.getId()));
    }

    private boolean isThisUserMaterial(User user, Material originalMaterial) {
        return user != null && originalMaterial.getCreator().getUsername().equals(user.getUsername());
    }

    private void validateMaterialUpdate(Material originalMaterial, User changer) {
        if (originalMaterial == null) {
            throw new IllegalArgumentException("Error updating Material: material does not exist.");
        }

        if (originalMaterial.getRepository() != null && changer != null && !UserUtil.isUserAdminOrModerator(changer)) {
            throw new IllegalArgumentException("Normal user can't update external repository material");
        }
    }

    public List<ReducedLearningObject> getByCreator(User creator, int start, int maxResults) {
        return reducedLearningObjectDao.findMaterialByCreator(creator, start, maxResults);
    }

    public long getByCreatorSize(User creator) {
        return materialDao.findByCreatorSize(creator);
    }

    private Material createOrUpdate(Material material) {
        Long materialId = material.getId();
        if (materialId == null) {
            logger.info("Creating material");
            material.setAdded(now());
        } else {
            logger.info("Updating material");
        }

        cleanTextFields(material);

        checkKeyCompetences(material);
        checkCrossCurricularThemes(material);

        setAuthors(material);
        setPublishers(material);
        setPeerReviews(material);
        material = applyRestrictions(material);

        return materialDao.createOrUpdate(material);
    }

    private void cleanTextFields(Material material) {
        String regex = "[^\\u0000-\\uFFFF]";
        String replacement = "\uFFFD";

        if (material.getTitles() != null)
            material.getTitles().forEach(title -> title.setText(title.getText().replaceAll(regex, replacement)));

        if (material.getDescriptions() != null)
            material.getDescriptions().forEach(desc -> desc.setText(desc.getText().replaceAll(regex, replacement)));
    }

    private Material applyRestrictions(Material material) {
        boolean areKeyCompetencesAndCrossCurricularThemesAllowed = false;

        if (material.getTaxons() != null && !material.getTaxons().isEmpty()) {
            List<EducationalContext> educationalContexts = material.getTaxons().stream()
                    .map(TaxonUtils::getEducationalContext)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            for (EducationalContext educationalContext : educationalContexts) {
                if (educationalContext.getName().equals(EducationalContextC.BASICEDUCATION)
                        || educationalContext.getName().equals(EducationalContextC.SECONDARYEDUCATION)) {
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

    private boolean isUserPublisher(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getPublisher() != null;
    }

    public BrokenContent addBrokenMaterial(Material material, User loggedInUser) {
        if (material == null || material.getId() == null) {
            throw new RuntimeException("Material not found while adding broken material");
        }
        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found while adding broken material");
        }

        BrokenContent brokenContent = new BrokenContent();
        brokenContent.setCreator(loggedInUser);
        brokenContent.setMaterial(material);

        return brokenContentDao.update(brokenContent);
    }

    public List<Material> getDeletedMaterials() {
        return materialDao.findDeletedMaterials();
    }

    public Long getDeletedMaterialsCount() {
        return materialDao.findDeletedMaterialsCount();
    }

    public List<BrokenContent> getBrokenMaterials() {
        return brokenContentDao.getBrokenMaterials();
    }

    public Long getBrokenMaterialCount() {
        return brokenContentDao.getCount();
    }

    public void setMaterialNotBroken(Material material) {
        if (material == null || material.getId() == null) {
            throw new RuntimeException("Material not found while adding broken material");
        }
        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found while adding broken material");
        }

        brokenContentDao.deleteBrokenMaterials(originalMaterial.getId());
    }

    public Boolean hasSetBroken(long materialId, User loggedInUser) {
        List<BrokenContent> brokenContents = brokenContentDao.findByMaterialAndUser(materialId, loggedInUser);
        return isNotEmpty(brokenContents);
    }

    public Boolean isBroken(long materialId) {
        List<BrokenContent> brokenContents = brokenContentDao.findByMaterial(materialId);
        return isNotEmpty(brokenContents);
    }

    public List<Language> getLanguagesUsedInMaterials() {
        return materialDao.findLanguagesUsedInMaterials();
    }

    private void validateMaterialAndIdNotNull(Material material) {
        if (material == null || material.getId() == null) {
            throw new RuntimeException("Material not found");
        }
    }

    private void validateMaterialNotNull(Material material) {
        if (material == null) {
            throw new RuntimeException("Material not found");
        }
    }

    private void validateUserIsAdmin(User loggedInUser) {
        if (!UserUtil.isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Only admin can do this");
        }
    }

    @Override
    public boolean hasPermissionsToAccess(User user, LearningObject learningObject) {
        if (!(learningObject instanceof Material)) {
            return false;
        }
        Material material = (Material) learningObject;
        return !material.isDeleted() || UserUtil.isUserAdmin(user);
    }

    @Override
    public boolean hasPermissionsToUpdate(User user, LearningObject learningObject) {
        if (!(learningObject instanceof Material)) {
            return false;
        }
        Material material = (Material) learningObject;
        if (UserUtil.isUserAdminOrModerator(user) || UserUtil.isUserCreator(material, user)) {
            return true;
        }
        return !material.isDeleted() || UserUtil.isUserAdmin(user);
    }

    @Override
    public boolean isPublic(LearningObject learningObject) {
        return true;
    }

    public List<Material> getBySource(String materialSource, boolean deleted) {
        materialSource = UrlUtil.getURLWithoutProtocolAndWWW(UrlUtil.processURL(materialSource));
        if (materialSource != null) {
            return materialDao.findBySource(materialSource, deleted);
        } else {
            throw new RuntimeException("No material source link provided");
        }
    }

    public Material getOneBySource(String materialSource, boolean deleted) {
        materialSource = UrlUtil.getURLWithoutProtocolAndWWW(UrlUtil.processURL(materialSource));
        if (materialSource != null) {
            return materialDao.findOneBySource(materialSource, deleted);
        } else {
            throw new RuntimeException("No material source link provided");
        }
    }
}
