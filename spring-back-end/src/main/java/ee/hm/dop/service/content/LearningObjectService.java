package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.dao.TaxonPositionDao;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.TaxonLevel;
import ee.hm.dop.model.taxon.TaxonPosition;
import ee.hm.dop.model.taxon.TaxonPositionDTO;
import ee.hm.dop.service.permission.PermissionFactory;
import ee.hm.dop.service.permission.PermissionItem;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ee.hm.dop.model.enums.LicenseType.CCBYSA30;
import static ee.hm.dop.utils.UserUtil.isAdmin;
import static ee.hm.dop.utils.UserUtil.isModerator;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Service
@Transactional
public class LearningObjectService {

    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private PermissionFactory permissionFactory;
    @Inject
    private LearningObjectServiceCache learningObjectServiceCache;
    @Inject
    private TaxonPositionDao taxonPositionDao;
    @Inject
    private PortfolioService portfolioService;
    @Inject
    private ReducedLearningObjectDao reducedLearningObjectDao;
    @Inject
    private ReducedUserService reducedUserService;


    public LearningObject get(long learningObjectId, User user) {
        LearningObject learningObject = learningObjectDao.findById(learningObjectId);
        return canAccess(user, learningObject) ? learningObject : null;
    }

    public void incrementViewCount(LearningObject learningObject) {
        LearningObject originalLearningObject = validateAndFindIncludeDeleted(learningObject);
        learningObjectDao.incrementViewCount(originalLearningObject);
    }

    public boolean canAccess(User user, LearningObject learningObject) {
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canInteract(user, learningObject);
    }

    public boolean canView(User user, LearningObject learningObject) {
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canView(user, learningObject);
    }

    public boolean canUpdate(User user, LearningObject learningObject) {
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canUpdate(user, learningObject);
    }

    public LearningObject validateAndFind(LearningObject learningObject) {
        return ValidatorUtil.findValid(learningObject, learningObjectDao::findByIdNotDeleted);
    }

    public LearningObject validateAndFindIncludeDeleted(LearningObject learningObject) {
        return ValidatorUtil.findValid(learningObject, learningObjectDao::findById);
    }

    public LearningObject validateAndFindDeletedOnly(LearningObject learningObject) {
        return ValidatorUtil.findValid(learningObject, learningObjectDao::findByIdDeleted);
    }

    private PermissionItem getLearningObjectHandler(LearningObject learningObject) {
        return permissionFactory.get(learningObject);
    }

    public boolean showUnreviewed(Long id, User user) {
        if (isAdmin(user)) {
            return true;
        } else if (isModerator(user)) {
            LearningObject obj = learningObjectDao.findById(id);
            if (obj == null) return false;
            List<Long> collect = obj.getTaxons().stream().map(Taxon::getId).collect(Collectors.toList());
            List<Long> userTaxons = learningObjectServiceCache.getUserTaxonWithChildren(user.getId());
            return userTaxons != null && collect.stream().anyMatch(userTaxons::contains);
        }
        return false;
    }

    public List<LearningObject> getAllByCreator(User creator) {
        return learningObjectDao.findAllByCreator(creator);
    }

    public void setTaxonPosition(LearningObject learningobject) {
        if (isEmpty(learningobject.getTaxons())) {
            return;
        }
        List<TaxonPosition> taxonPosition = learningobject.getTaxons()
                .stream()
                .map(taxonPositionDao::findByTaxon)
                .filter(Objects::nonNull)
                .collect(toList());

        List<TaxonPositionDTO> taxonPositionDTOList = new ArrayList<>();
        taxonPosition.forEach(tp -> {
            Taxon educationalContext = tp.getEducationalContext();
            if (educationalContext != null) {
                TaxonPositionDTO tpdEduContext = new TaxonPositionDTO();
                tpdEduContext.setTaxonLevelId(educationalContext.getId());
                tpdEduContext.setTaxonLevelName(educationalContext.getName());
                tpdEduContext.setTaxonLevel(TaxonLevel.EDUCATIONAL_CONTEXT);
                if (tp.getDomain() != null) {
                    TaxonPositionDTO tpdDomain = new TaxonPositionDTO();
                    tpdDomain.setTaxonLevelId(tp.getDomain().getId());
                    tpdDomain.setTaxonLevelName(tp.getDomain().getName());
                    tpdDomain.setTaxonLevel(TaxonLevel.DOMAIN);
                    taxonPositionDTOList.add(tpdEduContext);
                    taxonPositionDTOList.add(tpdDomain);
                }
                if (tp.getSubject() != null) {
                    TaxonPositionDTO tpdSubjet = new TaxonPositionDTO();
                    tpdSubjet.setTaxonLevelId(tp.getSubject().getId());
                    tpdSubjet.setTaxonLevelName(tp.getSubject().getName());
                    tpdSubjet.setTaxonLevel(TaxonLevel.SUBJECT);
                    taxonPositionDTOList.add(tpdSubjet);
                }
                if (tp.getTopic() != null) {
                    TaxonPositionDTO tpdTopic = new TaxonPositionDTO();
                    tpdTopic.setTaxonLevelId(tp.getTopic().getId());
                    tpdTopic.setTaxonLevelName(tp.getTopic().getName());
                    tpdTopic.setTaxonLevel(TaxonLevel.TOPIC);
                    taxonPositionDTOList.add(tpdTopic);
                }
                if (tp.getSubtopic() != null) {
                    TaxonPositionDTO tpdSubTopic = new TaxonPositionDTO();
                    tpdSubTopic.setTaxonLevelId(tp.getSubtopic().getId());
                    tpdSubTopic.setTaxonLevelName(tp.getSubtopic().getName());
                    tpdSubTopic.setTaxonLevel(TaxonLevel.SUBTOPIC);
                    taxonPositionDTOList.add(tpdSubTopic);
                }
                if (tp.getModule() != null) {
                    TaxonPositionDTO tpdModule = new TaxonPositionDTO();
                    tpdModule.setTaxonLevelId(tp.getModule().getId());
                    tpdModule.setTaxonLevelName(tp.getModule().getName());
                    tpdModule.setTaxonLevel(TaxonLevel.MODULE);
                    taxonPositionDTOList.add(tpdModule);
                }
                if (tp.getSpecialization() != null) {
                    TaxonPositionDTO tpdSpecialization = new TaxonPositionDTO();
                    tpdSpecialization.setTaxonLevelId(tp.getSpecialization().getId());
                    tpdSpecialization.setTaxonLevelName(tp.getSpecialization().getName());
                    tpdSpecialization.setTaxonLevel(TaxonLevel.SPECIALIZATION);
                    taxonPositionDTOList.add(tpdSpecialization);
                }
            }
        });

        learningobject.setTaxonPositionDto(taxonPositionDTOList);
    }

    public boolean learningObjectHasUnAcceptableLicence(LearningObject lo) {
        if (lo instanceof Portfolio) {
            return !portfolioService.portfolioHasAcceptableLicenses(lo.getId());
        }
        return lo.getLicenseType() == null || !lo.getLicenseType().getName().equals(CCBYSA30.name());
    }

    public List<ReducedLearningObject> getReducedLOsByCreator(User creator, User loggedInUser, int start, int maxResults) {
        List<ReducedLearningObject> reducedLOsByCreator = getReducedLOsAccordingToRole(creator, loggedInUser, start, maxResults);
        reducedLOsByCreator
                .forEach(lo -> {
                    if (lo.getCreator() != null) {
                        lo.setCreator(reducedUserService.getMapper().convertValue(lo.getCreator(), User.class));
                    }
                });
        return reducedLOsByCreator;
    }

    private List <ReducedLearningObject> getReducedLOsAccordingToRole(User creator, User loggedInUser, int start, int maxResults) {
        if (loggedInUser == null) {
            return reducedLearningObjectDao.findReducedLOSByCreatorNotPrivate(creator, start, maxResults);
        } else if (UserUtil.isAdmin(loggedInUser) || creator.getId().equals(loggedInUser.getId())) {
            return reducedLearningObjectDao.findReducedLOSByCreator(creator, start, maxResults);
        } else {
            return reducedLearningObjectDao.findReducedLOSByCreatorNotPrivate(creator, start, maxResults);
        }
    }
}
