package ee.hm.dop.service;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.dao.BrokenContentDAO;
import ee.hm.dop.dao.ImproperContentDAO;
import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.dao.RecommendationDAO;
import ee.hm.dop.dao.UserLikeDAO;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Publisher;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLike;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.utils.TaxonUtils;

public class MaterialService {

    public static final String BASICEDUCATION = "BASICEDUCATION";
    public static final String SECONDARYEDUCATION = "SECONDARYEDUCATION";
    public static final String PUBLISHER = "PUBLISHER";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private MaterialDAO materialDao;

    @Inject
    private UserLikeDAO userLikeDAO;

    @Inject
    private RecommendationDAO recommendationeDAO;

    @Inject
    private AuthorService authorService;

    @Inject
    private PublisherService publisherService;

    @Inject
    private SearchEngineService searchEngineService;

    @Inject
    private ImproperContentDAO improperContentDAO;

    @Inject
    private BrokenContentDAO brokenContentDAO;

    public Material get(long materialId) {
        return materialDao.findById(materialId);
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
        setAuthors(material);
        setPublishers(material);

        if (creator != null && creator.getRole().toString().equals(PUBLISHER)) {
            material.setEmbeddable(true);
        }

        Material createdMaterial = createOrUpdate(material);
        if (updateSearchIndex) {
            searchEngineService.updateIndex();
        }

        return createdMaterial;
    }

    public void delete(Material material, User loggedInUser) {
        Material originalMaterial = materialDao.findById(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found");
        }

        if (!isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Logged in user must be an administrator.");
        }

        materialDao.delete(originalMaterial);
        searchEngineService.updateIndex();
    }

    public void restore(Material material, User loggedInUser) {
        Material originalMaterial = materialDao.findDeletedById(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found");
        }

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
                if (publisher != null) {
                    Publisher returnedPublisher = publisherService.getPublisherByName(publisher.getName());
                    if (returnedPublisher != null) {
                        publishers.set(i, returnedPublisher);
                    } else {
                        returnedPublisher = publisherService.createPublisher(publisher.getName(),
                                publisher.getWebsite());
                        publishers.set(i, returnedPublisher);
                    }
                }
            }
        }
    }

    private void setAuthors(Material material) {
        List<Author> authors = material.getAuthors();
        if (authors != null) {
            for (int i = 0; i < authors.size(); i++) {
                Author author = authors.get(i);
                if (author != null) {
                    Author returnedAuthor = authorService.getAuthorByFullName(author.getName(), author.getSurname());
                    if (returnedAuthor != null) {
                        authors.set(i, returnedAuthor);
                    } else {
                        returnedAuthor = authorService.createAuthor(author.getName(), author.getSurname());
                        authors.set(i, returnedAuthor);
                    }
                }
            }
        }
    }

    public void addComment(Comment comment, Material material) {
        if (isEmpty(comment.getText())) {
            throw new RuntimeException("Comment is missing text.");
        }

        if (comment.getId() != null) {
            throw new RuntimeException("Comment already exists.");
        }

        Material originalMaterial = materialDao.findById(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found");
        }

        comment.setAdded(DateTime.now());
        originalMaterial.getComments().add(comment);
        materialDao.update(originalMaterial);
    }

    public UserLike addUserLike(Material material, User loggedInUser, boolean isLiked) {
        if (material == null || material.getId() == null) {
            throw new RuntimeException("Material not found");
        }
        Material originalMaterial = materialDao.findById(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found");
        }

        userLikeDAO.deleteMaterialLike(originalMaterial, loggedInUser);

        UserLike like = new UserLike();
        like.setMaterial(originalMaterial);
        like.setCreator(loggedInUser);
        like.setLiked(isLiked);
        like.setAdded(DateTime.now());

        return userLikeDAO.update(like);
    }

    public Recommendation addRecommendation(Material material, User loggedInUser) {
        if (!isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Logged in user must be an administrator.");
        }
        if (material == null || material.getId() == null) {
            throw new RuntimeException("Material not found");
        }
        Material originalMaterial = materialDao.findById(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found");
        }

        Recommendation recommendation = new Recommendation();
        recommendation.setMaterial(originalMaterial);
        recommendation.setCreator(loggedInUser);
        recommendation.setAdded(DateTime.now());

        return recommendationeDAO.update(recommendation);
    }

    public void removeRecommendation(Material material, User loggedInUser) {
        if (!isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Logged in user must be an administrator.");
        }
        if (material == null || material.getId() == null) {
            throw new RuntimeException("Material not found");
        }
        Material originalMaterial = materialDao.findById(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found");
        }

        recommendationeDAO.deleteMaterialRecommendation(originalMaterial);
    }

    public void removeUserLike(Material material, User loggedInUser) {
        if (material == null || material.getId() == null) {
            throw new RuntimeException("Material not found");
        }
        Material originalMaterial = materialDao.findById(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found");
        }

        userLikeDAO.deleteMaterialLike(originalMaterial, loggedInUser);
    }

    public UserLike getUserLike(Material material, User loggedInUser) {

        if (material == null || material.getId() == null) {
            throw new RuntimeException("Material not found");
        }
        Material originalMaterial = materialDao.findById(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found");
        }

        UserLike like = userLikeDAO.findMaterialUserLike(originalMaterial, loggedInUser);
        return like;
    }

    public void update(Material material) {
        Material originalMaterial = materialDao.findById(material.getId());
        validateMaterialUpdate(material, originalMaterial);

        // Should not be able to update view count
        material.setViews(originalMaterial.getViews());
        // Should not be able to update added date, must keep the original
        material.setAdded(originalMaterial.getAdded());

        createOrUpdate(material);
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

    public byte[] getMaterialPicture(Material material) {
        return materialDao.findPictureByMaterial(material);
    }

    public List<Material> getByCreator(User creator) {
        return materialDao.findByCreator(creator);
    }

    private Material createOrUpdate(Material material) {
        Long materialId = material.getId();
        if (materialId != null) {
            logger.info(format("Updating material %s", materialId));
        } else {
            logger.info("Creating material.");
        }

        material = applyRestrictions(material);
        return materialDao.update(material);
    }

    public void delete(Material material) {
        materialDao.delete(material);
    }

    private Material applyRestrictions(Material material) {
        boolean areKeyCompetencesAndCrossCurricularThemesAllowed = false;

        if (material.getTaxons() != null && !material.getTaxons().isEmpty()) {
            List<EducationalContext> educationalContexts = material.getTaxons().stream()
                    .map(TaxonUtils::getEducationalContext).collect(Collectors.toList());

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

    public ImproperContent addImproperMaterial(Material material, User loggedInUser) {
        if (material == null || material.getId() == null) {
            throw new RuntimeException("Material not found while adding improper material");
        }
        Material originalMaterial = materialDao.findById(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found while adding improper material");
        }

        ImproperContent improperContent = new ImproperContent();
        improperContent.setCreator(loggedInUser);
        improperContent.setMaterial(material);

        return improperContentDAO.update(improperContent);
    }

    public List<ImproperContent> getImproperMaterials() {
        return improperContentDAO.getImproperMaterials();
    }

    public BrokenContent addBrokenMaterial(Material material, User loggedInUser) {
        if (material == null || material.getId() == null) {
            throw new RuntimeException("Material not found while adding broken material");
        }
        Material originalMaterial = materialDao.findById(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found while adding broken material");
        }

        BrokenContent brokenContent = new BrokenContent();
        brokenContent.setCreator(loggedInUser);
        brokenContent.setMaterial(material);

        return brokenContentDAO.update(brokenContent);
    }

    public List<Material> getDeletedMaterials() {
        return materialDao.getDeletedMaterials();
    }

    public void setMaterialNotBroken(Long id) {
        brokenContentDAO.deleteBrokenMaterials(id);
    }

    public Boolean hasSetImproper(long materialId, User loggedInUser) {
        List<ImproperContent> improperContents = improperContentDAO.findByMaterialAndUser(materialId, loggedInUser);
        return improperContents.size() != 0;
    }

    public Boolean hasSetBroken(long materialId, User loggedInUser) {
        List<BrokenContent> improperContents = brokenContentDAO.findByMaterialAndUser(materialId, loggedInUser);
        return improperContents.size() != 0;
    }

    public Boolean isBroken(long materialId, User loggedInUser) {
        List<BrokenContent> improperContents = brokenContentDAO.findByMaterial(materialId);
        return improperContents.size() != 0;
    }

    public void removeImproperMaterials(Long id) {
        improperContentDAO.deleteImproperMaterials(id);
    }

    public Boolean isSetImproper(long materialId) {
        List<ImproperContent> improperContents = improperContentDAO.getByMaterial(materialId);

        return improperContents.size() != 0;
    }
}
