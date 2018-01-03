package ee.hm.dop.dao.specialized;

import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.model.User;
import ee.hm.dop.service.reviewmanagement.StatisticsUtil;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsQuery;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

public class StatisticsDao {
    @Inject
    private EntityManager entityManager;
    @Inject
    private TaxonDao taxonDao;

    public List<StatisticsQuery> reviewedLOCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        List<Object[]> users1 = entityManager.createNativeQuery(
                "SELECT\n" +
                        "  f.reviewedBy,\n" +
                        "  count(DISTINCT lo.id) AS c\n" +
                        "FROM FirstReview f\n" +
                        "  JOIN LearningObject lo ON f.learningObject = lo.id\n" +
                        "WHERE f.reviewed = 1\n" +
                        "      AND f.reviewedBy IS NOT NULL\n" +
                        "      AND lo.deleted = 0\n" +
                        "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                        "                        FROM ImproperContent ic\n" +
                        "                        WHERE ic.learningObject = lo.id\n" +
                        "                              AND ic.reviewed = 1)\n" +
                        "GROUP BY f.reviewedBy;")
//                .setParameter("users", users)
                .getResultList();
        return StatisticsUtil.convertToStatisticsQuery(users1);
    }

    public List<StatisticsQuery> approvedReportedLOCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        List<Object[]> users1 = entityManager.createNativeQuery(
                "SELECT\n" +
                        "  f.reviewedBy,\n" +
                        "  count(DISTINCT lo.id) AS c\n" +
                        "FROM ImproperContent f\n" +
                        "  JOIN LearningObject lo ON f.learningObject = lo.id\n" +
                        "WHERE f.reviewed = 1\n" +
                        "      AND f.reviewedBy IN (:users)\n" +
                        "      AND lo.deleted = 0\n" +
                        "      AND f.status IN ('ACCEPTED')\n" +
                        "GROUP BY f.reviewedBy")
                .setParameter("users", users)
                .getResultList();
        return StatisticsUtil.convertToStatisticsQuery(users1);
    }

    public List<StatisticsQuery> rejectedReportedLOCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        List<Object[]> users1 = entityManager.createNativeQuery(
                "SELECT\n" +
                        "  f.reviewedBy,\n" +
                        "  count(DISTINCT lo.id) AS c\n" +
                        "FROM ImproperContent f\n" +
                        "  JOIN LearningObject lo ON f.learningObject = lo.id\n" +
                        "WHERE f.reviewed = 1\n" +
                        "      AND f.reviewedBy IS NOT NULL\n" +
                        "      AND lo.deleted = 1\n" +
                        "      AND f.status IN ('REJECTED', 'DELETED')\n" +
                        "GROUP BY f.reviewedBy")
//                .setParameter("users", users)
                .getResultList();
        return StatisticsUtil.convertToStatisticsQuery(users1);
    }

    public List<StatisticsQuery> acceptedChangedLOCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        List<Object[]> users1 = entityManager.createNativeQuery(
                "SELECT\n" +
                        "  r.reviewedBy,\n" +
                        "  count(DISTINCT lo.id) AS c\n" +
                        "FROM LearningObject lo\n" +
                        "  JOIN ReviewableChange r ON r.learningObject = lo.id\n" +
                        "WHERE r.reviewed = 1\n" +
                        "      AND r.reviewedBy IS NOT NULL\n" +
                        "      AND r.status IN ('ACCEPTED')\n" +
                        "      AND lo.deleted = 0\n" +
                        "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                        "                        FROM ImproperContent ic\n" +
                        "                        WHERE ic.learningObject = lo.id\n" +
                        "                              AND ic.reviewed = 1)\n" +
                        "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                        "                        FROM FirstReview ic\n" +
                        "                        WHERE ic.learningObject = lo.id\n" +
                        "                              AND ic.reviewed = 1)\n" +
                        "GROUP BY r.reviewedBy")
//                .setParameter("users", users)
                .getResultList();
        return StatisticsUtil.convertToStatisticsQuery(users1);
    }


    public List<StatisticsQuery> rejectedChangedLOCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        List<Object[]> users1 = entityManager.createNativeQuery(
                "SELECT\n" +
                        "  r.reviewedBy,\n" +
                        "  count(DISTINCT lo.id) AS c\n" +
                        "FROM LearningObject lo\n" +
                        "  JOIN ReviewableChange r ON r.learningObject = lo.id\n" +
                        "WHERE r.reviewed = 1\n" +
                        "      AND r.reviewedBy IS NOT NULL\n" +
                        "      AND r.status IN ('REJECTED')\n" +
                        "      AND lo.deleted = 0\n" +
                        "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                        "                        FROM ImproperContent ic\n" +
                        "                        WHERE ic.learningObject = lo.id\n" +
                        "                              AND ic.reviewed = 1)\n" +
                        "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                        "                        FROM FirstReview ic\n" +
                        "                        WHERE ic.learningObject = lo.id\n" +
                        "                              AND ic.reviewed = 1)\n" +
                        "GROUP BY r.reviewedBy")
//                .setParameter("users", users)
                .getResultList();
        return StatisticsUtil.convertToStatisticsQuery(users1);
    }

    public List<StatisticsQuery> reportedLOCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        List<Object[]> users1 = entityManager.createNativeQuery(
                "SELECT\n" +
                        "  f.createdBy,\n" +
                        "  count(DISTINCT lo.id) AS c\n" +
                        "FROM ImproperContent f\n" +
                        "  JOIN LearningObject lo ON f.learningObject = lo.id\n" +
                        "  JOIN User u ON f.createdBy = u.id\n" +
                        "WHERE u.role = 'MODERATOR'\n" +
                        "GROUP BY f.createdBy")
//                .setParameter("users", users)
                .getResultList();
        return StatisticsUtil.convertToStatisticsQuery(users1);
    }

    public List<StatisticsQuery> createdPortfolioCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        List<Object[]> users1 = entityManager.createNativeQuery(
                "SELECT\n" +
                        "  lo.creator,\n" +
                        "  count(lo.id) AS c\n" +
                        "FROM LearningObject lo\n" +
                        "  JOIN User u ON lo.creator = u.id\n" +
                        "  JOIN Portfolio p ON lo.id = p.id\n" +
                        "WHERE u.role = 'MODERATOR'\n" +
                        "GROUP BY lo.creator")
//                .setParameter("users", users)
                .getResultList();
        return StatisticsUtil.convertToStatisticsQuery(users1);
    }

    public List<StatisticsQuery> createdPublicPortfolioCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        List<Object[]> users1 = entityManager.createNativeQuery(
                "SELECT\n" +
                        "  lo.creator,\n" +
                        "  count(lo.id) AS c\n" +
                        "FROM LearningObject lo\n" +
                        "  JOIN User u ON lo.creator = u.id\n" +
                        "  JOIN Portfolio p ON lo.id = p.id\n" +
                        "WHERE u.role = 'MODERATOR'\n" +
                        "  AND lo.visibility = 'PUBLIC'\n" +
                        "GROUP BY lo.creator")
//                .setParameter("users", users)
                .getResultList();
        return StatisticsUtil.convertToStatisticsQuery(users1);
    }

    public List<StatisticsQuery> createdMaterialCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        List<Object[]> users1 = entityManager.createNativeQuery(
                "SELECT\n" +
                        "  lo.creator,\n" +
                        "  count(lo.id) AS c\n" +
                        "FROM LearningObject lo\n" +
                        "  JOIN User u ON lo.creator = u.id\n" +
                        "  JOIN Material m ON lo.id = m.id\n" +
                        "WHERE u.role = 'MODERATOR'\n" +
                        "GROUP BY lo.creator")
//                .setParameter("users", users)
                .getResultList();
        return StatisticsUtil.convertToStatisticsQuery(users1);
    }

}
