package ee.hm.dop.service;

import static ee.hm.dop.utils.UserUtils.isAdmin;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joda.time.DateTime.now;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ee.hm.dop.dao.BrokenContentDAO;
import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.dao.UserLikeDAO;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.PeerReview;
import ee.hm.dop.model.Publisher;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLike;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.service.learningObject.LearningObjectHandler;
import ee.hm.dop.utils.TaxonUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaterialService implements LearningObjectHandler {

    public static final String BASICEDUCATION = "BASICEDUCATION";
    public static final String SECONDARYEDUCATION = "SECONDARYEDUCATION";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private MaterialDAO materialDao;

    @Inject
    private UserLikeDAO userLikeDAO;

    @Inject
    private AuthorService authorService;

    @Inject
    private PublisherService publisherService;

    @Inject
    private SolrEngineService solrEngineService;

    @Inject
    private BrokenContentDAO brokenContentDAO;

    @Inject
    private PeerReviewService peerReviewService;

    public Material get(long materialId, User loggedInUser) {
        if (isUserAdmin(loggedInUser) || isUserModerator(loggedInUser)) {
            return materialDao.findById(materialId);
        } else {
            return materialDao.findByIdNotDeleted(materialId);
        }
    }

    public void increaseViewCount(Material material) {
        material.setViews(material.getViews() + 1);
        createOrUpdate(material);

        solrEngineService.updateIndex();
    }

    public Material createMaterial(Material material, User creator, boolean updateSearchIndex) {
        if (material.getId() != null) {
            throw new IllegalArgumentException("Error creating Material, material already exists.");
        }

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

    public void delete(Long materialID, User loggedInUser) {
        Material originalMaterial = materialDao.findByIdNotDeleted(materialID);
        validateMaterialNotNull(originalMaterial);

        if (!isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Logged in user must be an administrator.");
        }

        if (originalMaterial.getRepository() != null || originalMaterial.getRepositoryIdentifier() != null) {
            throw new RuntimeException("Can not delete external repository material");
        }

        materialDao.delete(originalMaterial);
        solrEngineService.updateIndex();
    }

    public void restore(Material material, User loggedInUser) {
        Material originalMaterial = materialDao.findById(material.getId());
        validateMaterialNotNull(originalMaterial);

        if (!isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Logged in user must be an administrator.");
        }

        if (originalMaterial.getRepository() != null || originalMaterial.getRepositoryIdentifier() != null) {
            throw new RuntimeException("Can not restore external repository material");
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
        if(peerReviews != null){
            for (int i = 0; i < peerReviews.size(); i++) {
                PeerReview peerReview = peerReviews.get(i);
                PeerReview returnedPeerReview = peerReviewService.createPeerReview(peerReview.getUrl());
                if(returnedPeerReview != null){
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
        materialDao.update(originalMaterial);
    }

    public UserLike addUserLike(Material material, User loggedInUser, boolean isLiked) {
        validateMaterialAndIdNotNull(material);
        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        validateMaterialNotNull(originalMaterial);

        userLikeDAO.deleteMaterialLike(originalMaterial, loggedInUser);

        UserLike like = new UserLike();
        like.setLearningObject(originalMaterial);
        like.setCreator(loggedInUser);
        like.setLiked(isLiked);
        like.setAdded(DateTime.now());

        return userLikeDAO.update(like);
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

        originalMaterial = (Material) materialDao.update(originalMaterial);

        solrEngineService.updateIndex();

        return originalMaterial.getRecommendation();
    }

    public void removeRecommendation(Material material, User loggedInUser) {
        validateMaterialAndIdNotNull(material);

        validateUserIsAdmin(loggedInUser);

        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());

        validateMaterialNotNull(originalMaterial);

        originalMaterial.setRecommendation(null);

        materialDao.update(originalMaterial);

        solrEngineService.updateIndex();
    }

    public void removeUserLike(Material material, User loggedInUser) {
        validateMaterialAndIdNotNull(material);
        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        validateMaterialNotNull(originalMaterial);

        userLikeDAO.deleteMaterialLike(originalMaterial, loggedInUser);
    }

    public UserLike getUserLike(Material material, User loggedInUser) {
        validateMaterialAndIdNotNull(material);

        return userLikeDAO.findMaterialUserLike(material, loggedInUser);
    }

    public void delete(Material material) {
        materialDao.delete(material);
    }

    public Material update(Material material, User changer, boolean updateSearchIndex) {
        Material updatedMaterial = null;

        if (material == null || material.getId() == null) {
            throw new IllegalArgumentException("Material id parameter is mandatory");
        }

        Material originalMaterial;

        if (isUserAdmin(changer) || isUserModerator(changer)) {
            originalMaterial = materialDao.findById(material.getId());
        } else {
            originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        }

        validateMaterialUpdate(originalMaterial, changer);

        if (!isUserAdmin(changer)) {
            material.setRecommendation(originalMaterial.getRecommendation());
        }
        //Should not be able to update repository
        material.setRepository(originalMaterial.getRepository());

        // Should not be able to update view count
        material.setViews(originalMaterial.getViews());
        // Should not be able to update added date, must keep the original
        material.setAdded(originalMaterial.getAdded());
        material.setUpdated(now());

        //Null changer is the automated updating of materials during synchronization
        if (changer == null || isUserAdmin(changer) || isUserModerator(changer) || isThisUserMaterial(changer, originalMaterial)) {
            updatedMaterial = createOrUpdate(material);
            if (updateSearchIndex) solrEngineService.updateIndex();
        }

        return updatedMaterial;
    }

    private boolean isThisUserMaterial(User user, Material originalMaterial) {
        return user != null && originalMaterial.getCreator().getUsername().equals(user.getUsername());
    }

    private void validateMaterialUpdate(Material originalMaterial, User changer) {
        if (originalMaterial == null) {
            throw new IllegalArgumentException("Error updating Material: material does not exist.");
        }

        if (originalMaterial.getRepository() != null && changer != null && !isUserAdminOrPublisher(changer)) {
            throw new IllegalArgumentException("Can't update external repository material");
        }
    }

    public List<Material> getByCreator(User creator) {
        List<Material> materials = new ArrayList<>();
        materialDao.findByCreator(creator).stream().forEach(material -> materials.add((Material) material));
        return materials;
    }

    private Material createOrUpdate(Material material) {
        Long materialId = material.getId();
        if (materialId == null) {
            logger.info("Creating material");
            material.setAdded(now());
        } else {
            logger.info("Updating material");
        }

        setAuthors(material);
        setPublishers(material);
        setPeerReviews(material);
        material = applyRestrictions(material);

        return (Material) materialDao.update(material);
    }

    private Material applyRestrictions(Material material) {
        boolean areKeyCompetencesAndCrossCurricularThemesAllowed = false;

        if (material.getTaxons() != null && !material.getTaxons().isEmpty()) {
            List<EducationalContext> educationalContexts = material.getTaxons().stream()
                    .map(TaxonUtils::getEducationalContext).filter(Objects::nonNull).collect(Collectors.toList());

            for (EducationalContext educationalContext : educationalContexts) {
                if (educationalContext.getName().equals(BASICEDUCATION)
                        || educationalContext.getName().equals(SECONDARYEDUCATION)) {
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

    private boolean isUserAdmin(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getRole() == Role.ADMIN;
    }

    private boolean isUserModerator(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getRole() == Role.MODERATOR;
    }

    private boolean isUserPublisher(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getPublisher() != null;
    }

    private boolean isUserAdminOrPublisher(User loggedInUser) {
        return isUserModerator(loggedInUser) || isUserAdmin(loggedInUser);
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

        return brokenContentDAO.update(brokenContent);
    }

    public List<Material> getDeletedMaterials() {
        List<Material> materials = new ArrayList<>();
        materialDao.findDeletedMaterials().stream().forEach(material -> materials.add((Material) material));
        return materials;
    }

    public List<BrokenContent> getBrokenMaterials() {
        return brokenContentDAO.getBrokenMaterials();
    }

    public void setMaterialNotBroken(Material material) {
        if (material == null || material.getId() == null) {
            throw new RuntimeException("Material not found while adding broken material");
        }
        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found while adding broken material");
        }

        brokenContentDAO.deleteBrokenMaterials(originalMaterial.getId());
    }

    public Boolean hasSetBroken(long materialId, User loggedInUser) {
        List<BrokenContent> brokenContents = brokenContentDAO.findByMaterialAndUser(materialId, loggedInUser);
        return brokenContents.size() != 0;
    }

    public Boolean isBroken(long materialId) {
        List<BrokenContent> brokenContents = brokenContentDAO.findByMaterial(materialId);
        return brokenContents.size() != 0;
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
        if (!isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Only admin can do this");
        }
    }

    @Override
    public boolean hasAccess(User user, LearningObject learningObject) {
        if (!(learningObject instanceof Material)) {
            return false;
        }

        Material material = (Material) learningObject;

        return !material.isDeleted() || isAdmin(user);
    }

    @Override
    public boolean isPublic(LearningObject learningObject) {
        return true;
    }

    public List<Material> getBySource(String materialSource) {
        if (materialSource != null) {
            return materialDao.findBySource(materialSource);
        } else {
            throw new RuntimeException("No material source link provided");
        }
    }
}
