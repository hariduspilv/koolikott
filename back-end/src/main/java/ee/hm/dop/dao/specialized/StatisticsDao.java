package ee.hm.dop.dao.specialized;

import ee.hm.dop.model.User;
import ee.hm.dop.service.statistics.StatisticsUtil;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsQuery;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class StatisticsDao {
    @Inject
    private EntityManager entityManager;

    public List<StatisticsQuery> reviewedLOCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  f.reviewedBy,\n" +
                "  count(DISTINCT lo.id) AS c\n" +
                "FROM FirstReview f\n" +
                "  JOIN LearningObject lo ON f.learningObject = lo.id\n";
        String where = "WHERE f.reviewed = 1\n" +
                "      AND f.reviewedBy IN (:users)\n" +
                "      AND lo.deleted = 0\n" +
                "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                "                        FROM ImproperContent ic\n" +
                "                        WHERE ic.learningObject = lo.id\n" +
                "                              AND ic.reviewed = 1)\n";
        String groupBy = "GROUP BY f.reviewedBy";
        return query(select, where, groupBy, from, to, users, taxons, "f", "reviewedAt");
    }

    public List<StatisticsQuery> approvedReportedLOCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  f.reviewedBy,\n" +
                "  count(DISTINCT lo.id) AS c\n" +
                "FROM ImproperContent f\n" +
                "  JOIN LearningObject lo ON f.learningObject = lo.id\n";
        String where = "WHERE f.reviewed = 1\n" +
                "      AND f.reviewedBy IN (:users)\n" +
                "      AND lo.deleted = 0\n" +
                "      AND f.status IN ('ACCEPTED')\n";
        String groupBy = "GROUP BY f.reviewedBy";
        return query(select, where, groupBy, from, to, users, taxons, "f", "reviewedAt");
    }

    public List<StatisticsQuery> rejectedReportedLOCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  f.reviewedBy,\n" +
                "  count(DISTINCT lo.id) AS c\n" +
                "FROM ImproperContent f\n" +
                "  JOIN LearningObject lo ON f.learningObject = lo.id\n";
        String where = "WHERE f.reviewed = 1\n" +
                "      AND f.reviewedBy IN (:users)\n" +
                "      AND lo.deleted = 1\n" +
                "      AND f.status IN ('REJECTED', 'DELETED')\n";
        String groupBy = "GROUP BY f.reviewedBy";
        return query(select, where, groupBy, from, to, users, taxons, "f", "reviewedAt");
    }

    public List<StatisticsQuery> acceptedChangedLOCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  r.reviewedBy,\n" +
                "  count(DISTINCT lo.id) AS c\n" +
                "FROM LearningObject lo\n" +
                "  JOIN ReviewableChange r ON r.learningObject = lo.id\n";
        String where = "WHERE r.reviewed = 1\n" +
                "      AND r.reviewedBy IN (:users)\n" +
                "      AND r.status IN ('ACCEPTED')\n" +
                "      AND lo.deleted = 0\n" +
                "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                "                        FROM ImproperContent ic\n" +
                "                        WHERE ic.learningObject = lo.id\n" +
                "                              AND ic.reviewed = 1)\n" +
                "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                "                        FROM FirstReview ic\n" +
                "                        WHERE ic.learningObject = lo.id\n" +
                "                              AND ic.reviewed = 1)\n";
        String groupBy = "GROUP BY r.reviewedBy";
        return query(select, where, groupBy, from, to, users, taxons, "r", "reviewedAt");
    }


    public List<StatisticsQuery> rejectedChangedLOCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  r.reviewedBy,\n" +
                "  count(DISTINCT lo.id) AS c\n" +
                "FROM LearningObject lo\n" +
                "  JOIN ReviewableChange r ON r.learningObject = lo.id\n";
        String where = "WHERE r.reviewed = 1\n" +
                "      AND r.reviewedBy IN (:users)\n" +
                "      AND r.status IN ('REJECTED')\n" +
                "      AND lo.deleted = 0\n" +
                "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                "                        FROM ImproperContent ic\n" +
                "                        WHERE ic.learningObject = lo.id\n" +
                "                              AND ic.reviewed = 1)\n" +
                "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                "                        FROM FirstReview ic\n" +
                "                        WHERE ic.learningObject = lo.id\n" +
                "                              AND ic.reviewed = 1)\n";
        String groupBy = "GROUP BY r.reviewedBy";
        return query(select, where, groupBy, from, to, users, taxons, "r", "reviewedAt");
    }

    public List<StatisticsQuery> reportedLOCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  f.createdBy,\n" +
                "  count(DISTINCT lo.id) AS c\n" +
                "FROM ImproperContent f\n" +
                "  JOIN LearningObject lo ON f.learningObject = lo.id\n";
        String where = "WHERE f.createdBy IN (:users)\n";
        String groupBy = "GROUP BY f.createdBy";
        return query(select, where, groupBy, from, to, users, taxons, "f", "createdAt");
    }

    public List<StatisticsQuery> createdPortfolioCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  lo.creator,\n" +
                "  count(lo.id) AS c\n" +
                "FROM LearningObject lo\n" +
                "  JOIN Portfolio p ON lo.id = p.id\n";
        String where = "WHERE lo.creator IN (:users)\n" +
                "  AND lo.deleted = 0\n";
        String groupBy = "GROUP BY lo.creator";
        return query(select, where, groupBy, from, to, users, taxons, "lo", "added");
    }

    public List<StatisticsQuery> createdPublicPortfolioCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  lo.creator,\n" +
                "  count(lo.id) AS c\n" +
                "FROM LearningObject lo\n" +
                "  JOIN Portfolio p ON lo.id = p.id\n";
        String where = "WHERE lo.creator IN (:users)\n" +
                "  AND lo.visibility = 'PUBLIC'\n" +
                "  AND lo.deleted = 0\n";
        String groupBy = "GROUP BY lo.creator";
        return query(select, where, groupBy, from, to, users, taxons, "lo", "added");
    }

    public List<StatisticsQuery> createdMaterialCount(DateTime from, DateTime to, List<User> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  lo.creator,\n" +
                "  count(lo.id) AS c\n" +
                "FROM LearningObject lo\n" +
                "  JOIN Material m ON lo.id = m.id\n";
        String where = "WHERE lo.creator IN (:users)\n" +
                " AND lo.deleted = 0\n";
        String groupBy = "GROUP BY lo.creator";
        return query(select, where, groupBy, from, to, users, taxons, "lo", "added");
    }

    private List<StatisticsQuery> query(String initialSelect, String initialWhere, String groupBy, DateTime from, DateTime to, List<User> users, List<Long> taxons, String prefix, String date) {
        String select = modifySelectWithTaxons(taxons, initialSelect);
        String where = setWhere(initialWhere, from, to, taxons, prefix, date);
        Query query = entityManager.createNativeQuery(select + where + groupBy).setParameter("users", users);
        List<Object[]> users1 = setParams(query, from, to, taxons).getResultList();
        return StatisticsUtil.convertToStatisticsQuery(users1);
    }

    private String modifySelectWithTaxons(List<Long> taxons, String select) {
        return CollectionUtils.isEmpty(taxons) ? select : select + "  JOIN LearningObject_Taxon lt ON lt.learningObject = lo.id\n";
    }

    private String setWhere(String where, DateTime from, DateTime to, List<Long> taxons, String prefix, String reviewedAt) {
        if (CollectionUtils.isNotEmpty(taxons)) {
            where += "  AND lt.taxon IN (:taxons) \n";
        }
        if (from != null) {
            where += "  AND " + prefix + "." + reviewedAt + " > :from \n";
        }
        if (to != null) {
            where += "  AND " + prefix + "." + reviewedAt + " < :to \n";
        }
        return where;
    }

    private Query setParams(Query query, DateTime from, DateTime to, List<Long> taxons) {
        if (CollectionUtils.isNotEmpty(taxons)) {
            query = query.setParameter("taxons", taxons);
        }
        if (from != null) {
            query = query.setParameter("from", from);
        }
        if (to != null) {
            query = query.setParameter("to", to);
        }
        return query;
    }
}
