package ee.hm.dop.service;

import static ee.hm.dop.utils.UserUtils.isAdmin;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.dao.BrokenContentDAO;
import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.dao.UserLikeDAO;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Publisher;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLike;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.service.LearningObjectService.LearningObjectHandlerFactory;
import ee.hm.dop.utils.TaxonUtils;
import ezvcard.util.org.apache.commons.codec.binary.Base64;

public class MaterialService implements LearningObjectHandler {

    static {
        LearningObjectHandlerFactory.register(MaterialService.class, Material.class);
    }

    public static final String BASICEDUCATION = "BASICEDUCATION";
    public static final String SECONDARYEDUCATION = "SECONDARYEDUCATION";
    public static final String PUBLISHER = "PUBLISHER";

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
    private SearchEngineService searchEngineService;

    @Inject
    private BrokenContentDAO brokenContentDAO;

    @Inject
    private TagUpVoteService tagUpVoteService;

    public Material get(long materialId, User loggedInUser) {
        if (isUserAdmin(loggedInUser)) {
            return materialDao.findById(materialId);
        } else {
            return materialDao.findByIdNotDeleted(materialId);
        }
    }

    public List<Material> getNewestMaterials(int numberOfMaterials) {
        return materialDao.findNewestMaterials(numberOfMaterials);
    }

    public List<Material> getPopularMaterials(int numberOfMaterials) {
        return materialDao.findPopularMaterials(numberOfMaterials);
    }

    public void increaseViewCount(Material material) {
        material.setViews(material.getViews() + 1);
        createOrUpdate(material);
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

        // Do not upload picture when creating material
        material.setPicture(null);

        if (!isUserAdmin(creator) && !isUserPublisher(creator)) {
            material.setCurriculumLiterature(false);
        }

        Material createdMaterial = createOrUpdate(material);
        if (updateSearchIndex) {
            searchEngineService.updateIndex();
        }

        return createdMaterial;
    }

    public void delete(Material material, User loggedInUser) {
        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        validateMaterialNotNull(originalMaterial);

        if (!isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Logged in user must be an administrator.");
        }

        materialDao.delete(originalMaterial);
        searchEngineService.updateIndex();
    }

    public void restore(Material material, User loggedInUser) {
        Material originalMaterial = materialDao.findById(material.getId());
        validateMaterialNotNull(originalMaterial);

        if (!isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Logged in user must be an administrator.");
        }

        materialDao.restore(originalMaterial);
        searchEngineService.updateIndex();
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

        searchEngineService.updateIndex();

        return originalMaterial.getRecommendation();
    }

    public void removeRecommendation(Material material, User loggedInUser) {
        validateMaterialAndIdNotNull(material);

        validateUserIsAdmin(loggedInUser);

        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());

        validateMaterialNotNull(originalMaterial);

        originalMaterial.setRecommendation(null);

        materialDao.update(originalMaterial);

        searchEngineService.updateIndex();
    }

    public void removeUserLike(Material material, User loggedInUser) {
        validateMaterialAndIdNotNull(material);
        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        validateMaterialNotNull(originalMaterial);

        userLikeDAO.deleteMaterialLike(originalMaterial, loggedInUser);
    }

    public UserLike getUserLike(Material material, User loggedInUser) {
        validateMaterialAndIdNotNull(material);
        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        if (originalMaterial == null && !isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Material not found");
        }

        return userLikeDAO.findMaterialUserLike(originalMaterial, loggedInUser);
    }

    public String getMaterialPicture(Material material) {
        byte[] picture = materialDao.findPictureByMaterial(material);
        return Base64.encodeBase64String(picture);
    }

    public void delete(Material material) {
        materialDao.delete(material);
    }

    public Material updateByUser(Material material, User user) {
        Material returned = null;

        if (material == null) {
            throw new IllegalArgumentException("Material id parameter is mandatory");
        }

        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());

        if (originalMaterial != null && originalMaterial.getRepository() != null) {
            throw new IllegalArgumentException("Can't update external repository material");
        }

        if (!isUserAdmin(user)) {
            material.setRecommendation(originalMaterial.getRecommendation());
        }

        // We do not update/upload picture in this method
        material.setPicture(originalMaterial.getPicture());

        if (isUserAdmin(user) || isThisPublisherMaterial(user, originalMaterial)) {
            returned = update(material);
        }

        return returned;
    }

    private boolean isThisPublisherMaterial(User user, Material originalMaterial) {
        return originalMaterial.getCreator().getUsername().equals(user.getUsername()) && isUserPublisher(user);
    }

    public Material update(Material material) {
        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        validateMaterialUpdate(material, originalMaterial);

        // Should not be able to update view count
        material.setViews(originalMaterial.getViews());
        // Should not be able to update added date, must keep the original
        material.setAdded(originalMaterial.getAdded());

        Material returnedMaterial = createOrUpdate(material);

        searchEngineService.updateIndex();

        return returnedMaterial;
    }

    public Material updatePicture(Material material, User loggedInUser) {
        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());

        if (originalMaterial == null) {
            throw new RuntimeException("Material not found");
        }

        if (originalMaterial.getCreator().getId() != loggedInUser.getId()) {
            throw new RuntimeException("Logged in user must be the creator of this material.");
        }

        originalMaterial.setPicture(material.getPicture());
        originalMaterial.setHasPicture(material.getPicture() != null);
        return (Material) materialDao.update(originalMaterial);
    }

    private void validateMaterialUpdate(Material material, Material originalMaterial) {
        if (originalMaterial == null) {
            throw new IllegalArgumentException("Error updating Material: material does not exist.");
        }

        final String ErrorModifyRepository = "Error updating Material: Not allowed to modify repository.";
        if (material.getRepository() == null && originalMaterial.getRepository() != null) {
            throw new IllegalArgumentException(ErrorModifyRepository);
        }

        if (material.getRepository() != null && !material.getRepository().equals(originalMaterial.getRepository())) {
            throw new IllegalArgumentException(ErrorModifyRepository);
        }
    }

    public String getMaterialPicture(Material material, User loggedInUser) {
        byte[] picture;
        if (isUserAdmin(loggedInUser)) {
            picture = materialDao.findPictureByMaterial(material);
        } else {
            picture = materialDao.findNotDeletedPictureByMaterial(material);
        }

        return Base64.encodeBase64String(picture);
    }

    public List<Material> getByCreator(User creator) {
        List<Material> materials = new ArrayList<>();
        materialDao.findByCreator(creator).stream().forEach(material -> materials.add((Material) material));
        return materials;
    }

    private Material createOrUpdate(Material material) {
        Long materialId = material.getId();
        if (materialId != null) {
            logger.info(format("Updating material %s", materialId));
        } else {
            logger.info("Creating material.");
        }

        setAuthors(material);
        setPublishers(material);
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

    private boolean isUserPublisher(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getRole() == Role.PUBLISHER;
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
        materialDao.getDeletedMaterials().stream().forEach(material -> materials.add((Material) material));
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

    public Material addTag(Material material, Tag tag, User loggedInUser) {
        if (material == null) {
            throw new RuntimeException("Material not found");
        }

        List<Tag> tags = material.getTags();
        if (!tags.contains(tag)) {
            tags.add(tag);
            material.setTags(tags);

            material = (Material) materialDao.update(material);
            searchEngineService.updateIndex();
        } else {
            TagUpVote tagUpVote = new TagUpVote();
            tagUpVote.setMaterial(material);
            tagUpVote.setTag(tag);

            tagUpVoteService.upVote(tagUpVote, loggedInUser);
        }

        return material;
    }

    @Override
    public boolean hasAccess(User user, LearningObject learningObject) {
        if (!(learningObject instanceof Material)) {
            return false;
        }

        Material material = (Material) learningObject;

        return !material.isDeleted() || isAdmin(user);
    }

    public List<Material> getBySource(String materialSource) {
        if (materialSource != null) {
            return materialDao.findBySource(materialSource);
        } else {
            throw new RuntimeException("No material source link provided");
        }
    }
}
