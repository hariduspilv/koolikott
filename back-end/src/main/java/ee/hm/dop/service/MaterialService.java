package ee.hm.dop.service;

import ee.hm.dop.dao.BrokenContentDAO;
import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.dao.UserLikeDAO;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.PeerReview;
import ee.hm.dop.model.Publisher;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLike;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.service.learningObject.LearningObjectHandler;
import ee.hm.dop.utils.TaxonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.TextUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joda.time.DateTime.now;

public class MaterialService extends BaseService implements LearningObjectHandler {

    public static final String BASICEDUCATION = "BASICEDUCATION";
    public static final String SECONDARYEDUCATION = "SECONDARYEDUCATION";
    private final String PDF_EXTENSION = ".pdf\"";
    private final String PDF_MIME_TYPE = "application/pdf";
    private final String OCTET_STREAM_MIME_TYPE = "application/octet-stream";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private MaterialDAO materialDAO;

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

    @Inject
    private KeyCompetenceService keyCompetenceService;

    @Inject
    private CrossCurricularThemeService crossCurricularThemeService;

    @Inject
    private Configuration configuration;

    public Material get(long materialId, User loggedInUser) {
        if (isUserAdmin(loggedInUser) || isUserModerator(loggedInUser)) {
            return materialDAO.findById(materialId);
        } else {
            return materialDAO.findByIdNotDeleted(materialId);
        }
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

        material.setSource(cleanURL(material.getSource()));

        List<PeerReview> peerReviews = material.getPeerReviews();
        if (peerReviews != null) {
            for (PeerReview peerReview : peerReviews) {
                if (!peerReview.getUrl().contains(configuration.getString(SERVER_ADDRESS))) {
                    peerReview.setUrl(cleanURL(peerReview.getUrl()));
                }
            }
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
        Material originalMaterial = materialDAO.findByIdNotDeleted(materialID);
        validateMaterialNotNull(originalMaterial);

        if (!isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Logged in user must be an administrator.");
        }

        materialDAO.delete(originalMaterial);
        solrEngineService.updateIndex();
    }

    public void restore(Material material, User loggedInUser) {
        Material originalMaterial = materialDAO.findById(material.getId());
        validateMaterialNotNull(originalMaterial);

        if (!isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Logged in user must be an administrator.");
        }

        materialDAO.restore(originalMaterial);
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

        Material originalMaterial = materialDAO.findByIdNotDeleted(material.getId());
        validateMaterialNotNull(originalMaterial);

        comment.setAdded(DateTime.now());
        originalMaterial.getComments().add(comment);
        materialDAO.update(originalMaterial);
    }

    public UserLike addUserLike(Material material, User loggedInUser, boolean isLiked) {
        validateMaterialAndIdNotNull(material);
        Material originalMaterial = materialDAO.findByIdNotDeleted(material.getId());
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

        Material originalMaterial = materialDAO.findByIdNotDeleted(material.getId());

        validateMaterialNotNull(originalMaterial);

        Recommendation recommendation = new Recommendation();
        recommendation.setCreator(loggedInUser);
        recommendation.setAdded(DateTime.now());
        originalMaterial.setRecommendation(recommendation);

        originalMaterial = (Material) materialDAO.update(originalMaterial);

        solrEngineService.updateIndex();

        return originalMaterial.getRecommendation();
    }

    public void removeRecommendation(Material material, User loggedInUser) {
        validateMaterialAndIdNotNull(material);

        validateUserIsAdmin(loggedInUser);

        Material originalMaterial = materialDAO.findByIdNotDeleted(material.getId());

        validateMaterialNotNull(originalMaterial);

        originalMaterial.setRecommendation(null);

        materialDAO.update(originalMaterial);

        solrEngineService.updateIndex();
    }

    public void removeUserLike(Material material, User loggedInUser) {
        validateMaterialAndIdNotNull(material);
        Material originalMaterial = materialDAO.findByIdNotDeleted(material.getId());
        validateMaterialNotNull(originalMaterial);

        userLikeDAO.deleteMaterialLike(originalMaterial, loggedInUser);
    }

    public UserLike getUserLike(Material material, User loggedInUser) {
        validateMaterialAndIdNotNull(material);

        return userLikeDAO.findMaterialUserLike(material, loggedInUser);
    }

    public void delete(Material material) {
        materialDAO.delete(material);
    }

    public Material update(Material material, User changer, boolean updateSearchIndex) {
        Material updatedMaterial = null;

        if (material == null || material.getId() == null) {
            throw new IllegalArgumentException("Material id parameter is mandatory");
        }

        material.setSource(cleanURL(material.getSource()));

        List<PeerReview> peerReviews = material.getPeerReviews();
        if (peerReviews != null) {
            for (PeerReview peerReview : peerReviews) {
                if (!peerReview.getUrl().contains(configuration.getString(SERVER_ADDRESS))) {
                    peerReview.setUrl(cleanURL(peerReview.getUrl()));
                }
            }
        }

        if (materialWithSameSourceExists(material)) {
            throw new IllegalArgumentException("Error updating Material: material with given source already exists");
        }

        Material originalMaterial;

        if (isUserAdmin(changer) || isUserModerator(changer)) {
            originalMaterial = materialDAO.findById(material.getId());
        } else {
            originalMaterial = materialDAO.findByIdNotDeleted(material.getId());
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

    private boolean materialWithSameSourceExists(Material material) {
        if (material.getSource() == null && material.getUploadedFile() != null) return false;

        List<Material> materialsWithGivenSource = getBySource(material.getSource(), true);
        if (materialsWithGivenSource != null && materialsWithGivenSource.size() > 0) {
            if (!materialsWithGivenSource.get(0).getId().equals(material.getId())) {
                return true;
            }
        }

        return false;
    }

    private boolean isThisUserMaterial(User user, Material originalMaterial) {
        return user != null && originalMaterial.getCreator().getUsername().equals(user.getUsername());
    }

    private void validateMaterialUpdate(Material originalMaterial, User changer) {
        if (originalMaterial == null) {
            throw new IllegalArgumentException("Error updating Material: material does not exist.");
        }

        if (originalMaterial.getRepository() != null && changer != null && !isUserAdminOrPublisher(changer)) {
            throw new IllegalArgumentException("Normal user can't update external repository material");
        }
    }

    public List<LearningObject> getByCreator(User creator, int start, int maxResults) {
        return materialDAO.findByCreator(creator, start, maxResults);
    }

    public long getByCreatorSize(User creator) {
        return materialDAO.findByCreatorSize(creator);
    }

    private Material createOrUpdate(Material material) {
        Long materialId = material.getId();
        if (materialId == null) {
            logger.info("Creating material");
            material.setAdded(now());
        } else {
            logger.info("Updating material");
        }

        checkKeyCompetences(material);
        checkCrossCurricularThemes(material);

        setAuthors(material);
        setPublishers(material);
        setPeerReviews(material);
        material = applyRestrictions(material);

        return (Material) materialDAO.update(material);
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
        Material originalMaterial = materialDAO.findByIdNotDeleted(material.getId());
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
        materialDAO.findDeletedMaterials().forEach(material -> materials.add((Material) material));
        return materials;
    }

    public List<BrokenContent> getBrokenMaterials() {
        return brokenContentDAO.getBrokenMaterials();
    }

    public Long getBrokenMaterialCount() {
        return brokenContentDAO.getCount();
    }

    public void setMaterialNotBroken(Material material) {
        if (material == null || material.getId() == null) {
            throw new RuntimeException("Material not found while adding broken material");
        }
        Material originalMaterial = materialDAO.findByIdNotDeleted(material.getId());
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
        return materialDAO.findLanguagesUsedInMaterials();
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
    public boolean hasPermissionsToAccess(User user, LearningObject learningObject) {
        if (!(learningObject instanceof Material)) {
            return false;
        }

        Material material = (Material) learningObject;

        return !material.isDeleted() || isUserAdmin(user);
    }

    @Override
    public boolean hasPermissionsToUpdate(User user, LearningObject learningObject) {
        if (!(learningObject instanceof Material)) {
            return false;
        }

        Material material = (Material) learningObject;

        if(isUserAdminOrPublisher(user) || isUserCreator(material, user)){
            return true;
        }

        return !material.isDeleted() || isUserAdmin(user);
    }

    @Override
    public boolean isPublic(LearningObject learningObject) {
        return true;
    }

    public List<Material> getBySource(String materialSource, boolean deleted) {
        materialSource = getURLWithoutScheme(cleanURL(materialSource));
        if (materialSource != null) {
            return materialDAO.findBySource(materialSource, deleted);
        } else {
            throw new RuntimeException("No material source link provided");
        }
    }

    public Material getOneBySource(String materialSource, boolean deleted) {
        materialSource = getURLWithoutScheme(cleanURL(materialSource));
        if (materialSource != null) {
            return materialDAO.findOneBySource(materialSource, deleted);
        } else {
            throw new RuntimeException("No material source link provided");
        }
    }


    private String getURLWithoutScheme(String materialSource) {
        if (TextUtils.isBlank(materialSource)) return null;

        try {
            URI uri = new URI(materialSource);
            String hostName = uri.getHost();

            if (hostName.startsWith("www.") && isValidURL(hostName.substring(4))) {
                hostName = hostName.substring(4);
            }

            uri = new URIBuilder()
                    .setHost(hostName)
                    .setPath(uri.getPath())
                    .setCustomQuery(uri.getQuery())
                    .build();

            return uri.toString().substring(2);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fix URL");
        }
    }

    private String cleanURL(String materialSource) {
        if (TextUtils.isBlank(materialSource)) return null;

        try {
            materialSource = materialSource.replaceAll("/$", "");

            URI uri = new URI(materialSource);
            if (uri.getScheme() == null) {
                uri = new URI("http://" + materialSource);
            }

            return uri.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fix URL");
        }
    }

    private boolean isValidURL(String url) {
        Pattern p = Pattern.compile("^(?:https?://)?(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$");
        Matcher m = p.matcher(url);
        return m.matches();
    }

    public Response getProxyUrl(String url_param) throws IOException {
        String mediaType = OCTET_STREAM_MIME_TYPE;
        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod(url_param);
        client.executeMethod(get);
        if(get.getResponseHeaders("Content-Disposition").length == 0){
            return Response.noContent().build();
        }
        String contentDisposition = get.getResponseHeaders("Content-Disposition")[0].getValue();
        if(contentDisposition.endsWith(PDF_EXTENSION)){
            mediaType = PDF_MIME_TYPE;
        }
        contentDisposition = contentDisposition.replace("attachment", "Inline");
        return Response.ok(get.getResponseBody(), mediaType).header("Content-Disposition",
                contentDisposition).build();
    }

}
