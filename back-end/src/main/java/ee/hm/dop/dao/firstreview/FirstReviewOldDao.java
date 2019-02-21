package ee.hm.dop.dao.firstreview;

import ee.hm.dop.dao.AbstractDao;
import ee.hm.dop.dao.AdminLearningObjectDao;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.User;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public class FirstReviewOldDao  extends AbstractDao<FirstReview> {

    @Inject
    private TaxonDao taxonDao;
    @Inject
    private AdminLearningObjectDao adminLearningObjectDao;

    @Deprecated
    public List<AdminLearningObject> findAllUnreviewed() {
        List<BigInteger> resultList = getEntityManager()
                .createNativeQuery("SELECT\n" +
                        "  lo.id\n" +
                        "FROM LearningObject lo\n" +
                        "  JOIN FirstReview r ON r.learningObject = lo.id\n" +
                        "WHERE r.reviewed = 0\n" +
                        "      AND lo.deleted = 0\n" +
                        "      AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                        "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                        "                        FROM ImproperContent ic\n" +
                        "                        WHERE ic.learningObject = lo.id\n" +
                        "                              AND ic.reviewed = 0)\n" +
                        "GROUP BY lo.id\n" +
                        "ORDER BY min(r.createdAt) asc")
                .setMaxResults(200)
                .getResultList();
        List<Long> collect = resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
        return adminLearningObjectDao.findById(collect);
    }

    @Deprecated
    public List<AdminLearningObject> findAllUnreviewed(User user) {
        List<BigInteger> resultList = getEntityManager()
                .createNativeQuery("SELECT\n" +
                        "  lo.id\n" +
                        "FROM LearningObject lo\n" +
                        "  JOIN FirstReview r ON r.learningObject = lo.id\n" +
                        "  JOIN LearningObject_Taxon lt on lt.learningObject = lo.id\n" +
                        "WHERE r.reviewed = 0\n" +
                        "      AND lo.deleted = 0\n" +
                        "      AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                        "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                        "                        FROM ImproperContent ic\n" +
                        "                        WHERE ic.learningObject = lo.id\n" +
                        "                              AND ic.reviewed = 0)\n" +
                        "      AND lt.taxon in (:taxonIds)\n" +
                        " GROUP BY lo.id\n" +
                        "ORDER BY min(r.createdAt) asc")
                .setParameter("taxonIds", taxonDao.getUserTaxonsWithChildren(user))
                .setMaxResults(200)
                .getResultList();
        List<Long> collect = resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
        return adminLearningObjectDao.findById(collect);
    }

    public long findCountOfUnreviewed() {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(DISTINCT lo.id) AS c\n" +
                        "FROM FirstReview f\n" +
                        "   JOIN LearningObject lo ON f.learningObject = lo.id\n" +
                        "WHERE f.reviewed = 0\n" +
                        "   AND lo.deleted = 0\n" +
                        "   AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                        "   AND lo.id NOT IN (SELECT ic.learningObject\n" +
                        "                        FROM ImproperContent ic\n" +
                        "                        WHERE ic.learningObject = lo.id\n" +
                        "                              AND ic.reviewed = 0)\n"
                )
                .getSingleResult()).longValue();
    }

    public long findCountOfUnreviewed(User user) {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(DISTINCT lo.id) AS c\n" +
                        "FROM LearningObject lo\n" +
                        "   JOIN LearningObject_Taxon lt ON lt.learningObject = lo.id\n" +
                        "   JOIN FirstReview r on r.learningObject = lo.id " +
                        "WHERE (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                        "  AND r.reviewed = 0\n" +
                        "  AND lo.deleted = 0\n" +
                        "  AND lo.id NOT IN (SELECT ic.learningObject\n" +
                        "                        FROM ImproperContent ic\n" +
                        "                        WHERE ic.learningObject = lo.id\n" +
                        "                              AND ic.reviewed = 0)\n" +
                        "  AND lt.taxon IN (:taxonIds)")
                .setParameter("taxonIds", taxonDao.getUserTaxonWithChildren(Arrays.asList(user.getId())))
                .getSingleResult()).longValue();
    }
}
