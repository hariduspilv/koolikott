package ee.hm.dop.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.*;
import ee.hm.dop.service.content.enums.GetMaterialStrategy;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.joda.time.DateTime.now;

public class MaterialDao extends AbstractDao<Material> {
    public static final String HTTP = "http://";
    public static final String HTTP_WWW = HTTP + "www.";
    public static final String HTTPS = "https://";
    public static final String HTTPS_WWW = HTTPS + "www.";

    @Inject
    private LearningObjectDao learningObjectDao;

    @Override
    public Class<Material> entity() {
        return Material.class;
    }

    public void delete(LearningObject learningObject) {
        learningObjectDao.delete(learningObject);
    }

    public void restore(LearningObject learningObject) {
        learningObjectDao.restore(learningObject);
    }

    public Material findByIdNotDeleted(long objectId) {
        TypedQuery<Material> findByCode = getEntityManager()
                .createQuery("SELECT lo FROM Material lo " +
                        "WHERE lo.id = :id AND lo.deleted = false", entity())
                .setParameter("id", objectId);

        return getSingleResult(findByCode);
    }

    public Material findById(long objectId) {
        TypedQuery<Material> findByCode = getEntityManager()
                .createQuery("SELECT lo FROM Material lo " +
                        "WHERE lo.id = :id", entity())
                .setParameter("id", objectId);

        return getSingleResult(findByCode);
    }

    public List<Material> findDeletedMaterials() {
        return getEntityManager()
                .createQuery("SELECT m FROM Material m WHERE m.deleted = true", entity())
                .getResultList();
    }

    public Long findDeletedMaterialsCount() {
        return (Long) getEntityManager()
                .createQuery("SELECT count(m) FROM Material m WHERE m.deleted = true")
                .getSingleResult();
    }

    public List<LearningObject> findAllById(List<Long> idList) {
        if (isEmpty(idList)){
            return new ArrayList<>();
        }
        return getEntityManager()
                .createQuery("SELECT lo FROM LearningObject lo" +
                        " WHERE type(lo) = Material " +
                        "AND lo.deleted = false " +
                        "AND lo.id in :idList", LearningObject.class)
                .setParameter("idList", idList)
                .getResultList();
    }

    public Material findByRepository(Repository repository, String repositoryIdentifier) {
        String select = "SELECT m FROM Material m WHERE m.repository.id = :repositoryId"
                + " AND m.repositoryIdentifier = :repositoryIdentifier";
        TypedQuery<Material> query = getEntityManager()
                .createQuery(select, entity())
                .setParameter("repositoryId", repository.getId())
                .setParameter("repositoryIdentifier", repositoryIdentifier);
        return getSingleResult(query);
    }

    public List<Material> findBySource(String materialSource, GetMaterialStrategy getMaterialStrategy) {
        TypedQuery<Material> materialTypedQuery = getMaterialTypedQuery(materialSource, getMaterialStrategy);
        return materialTypedQuery.getResultList();
    }

    public Material findAnyBySource(String materialSource, GetMaterialStrategy getMaterialStrategy) {
        TypedQuery<Material> materialTypedQuery = getMaterialTypedQuery(materialSource, getMaterialStrategy);
        List<Material> list = materialTypedQuery.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public Material findOneBySource(String materialSource, GetMaterialStrategy getMaterialStrategy) {
        TypedQuery<Material> materialTypedQuery = getMaterialTypedQuery(materialSource, getMaterialStrategy);
        return getSingleResult(materialTypedQuery);
    }

    private TypedQuery<Material> getMaterialTypedQuery(String materialSource, GetMaterialStrategy getMaterialStrategy) {
        String ms1 = HTTP_WWW + materialSource;
        String ms2 = HTTPS_WWW + materialSource;
        String ms3 = HTTP + materialSource;
        String ms4 = HTTPS + materialSource;

        String query = "FROM Material m " +
                "WHERE (m.deleted = false OR m.deleted = :deleted) " +
                "AND m.source = :ms OR m.source = :ms1 OR m.source = :ms2 OR m.source = :ms3 OR m.source = :ms4";

        return getEntityManager().createQuery(query, entity())
                .setParameter("ms", materialSource)
                .setParameter("ms1", ms1)
                .setParameter("ms2", ms2)
                .setParameter("ms3", ms3)
                .setParameter("ms4", ms4)
                .setParameter("deleted", getMaterialStrategy.isDeleted());
    }

    public List<Language> findLanguagesUsedInMaterials() {
        return getEntityManager().createQuery("SELECT DISTINCT m.language FROM Material m WHERE m.deleted = false", Language.class).getResultList();
    }

    public long findByCreatorSize(User creator) {
        BigInteger query = (BigInteger) getEntityManager()
                .createNativeQuery("SELECT Count(lo.id) FROM LearningObject lo " +
                        "INNER JOIN Material m ON lo.id=m.id " +
                        "WHERE lo.creator = :creator AND lo.deleted = FALSE")
                .setParameter("creator", creator)
                .getSingleResult();
        return query.longValue();
    }

    public List<Material> findNewestMaterials(int numberOfMaterials, int startPosition) {
        return getEntityManager()
                .createQuery("FROM Material mat WHERE mat.deleted = false ORDER BY added DESC, id DESC", entity())
                .setFirstResult(startPosition).setMaxResults(numberOfMaterials)
                .getResultList();
    }

    public List<String> getRelatedPortfolios(long materialId) {
        String s = "class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"" + materialId + "\"";
//        String s = "class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"121\" class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"12221\"";
        Pattern pattern = Pattern.compile("class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"");
//        Pattern pattern = Pattern.compile("class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"");
        Matcher matcher = pattern.matcher(s);
        List<String> s123 = new ArrayList<>();
        while (matcher.find()) {
            s123.add(matcher.group());
        }
        return s123;
    }

    @Override
    public Material createOrUpdate(Material entity) {
        entity.setLastInteraction(now());
        return super.createOrUpdate(entity);
    }
}
