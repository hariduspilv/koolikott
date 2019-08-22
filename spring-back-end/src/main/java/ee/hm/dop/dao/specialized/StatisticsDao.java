package ee.hm.dop.dao.specialized;

import ee.hm.dop.service.reviewmanagement.dto.StatisticsQuery;
import ee.hm.dop.service.statistics.StatisticsUtil;
import org.apache.commons.collections.CollectionUtils;
import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class StatisticsDao {
    @Inject
    private EntityManager entityManager;

    public List<StatisticsQuery> reviewedLOCount(LocalDateTime from, LocalDateTime to, List<Long> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  r.reviewedBy,\n" +
                "  count(DISTINCT lo.id) AS c\n" +
                "FROM FirstReview r\n" +
                "  JOIN LearningObject lo ON r.learningObject = lo.id\n";
        String where = "WHERE r.reviewed = 1\n" +
                "      AND r.reviewedBy IN (:users)\n";
        String groupBy = "GROUP BY r.reviewedBy";
        return query(select, where, groupBy, from, to, users, taxons, "r", "reviewedAt");
    }

    public List<StatisticsQuery> approvedReportedLOCount(LocalDateTime from, LocalDateTime to, List<Long> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  r.reviewedBy,\n" +
                "  count(DISTINCT lo.id) AS c\n" +
                "FROM ImproperContent r\n" +
                "  JOIN LearningObject lo ON r.learningObject = lo.id\n";
        String where = "WHERE r.reviewed = 1\n" +
                "      AND r.reviewedBy IN (:users)\n" +
                "      AND r.status IN ('ACCEPTED')\n";
        String groupBy = "GROUP BY r.reviewedBy";
        return query(select, where, groupBy, from, to, users, taxons, "r", "reviewedAt");
    }

    public List<StatisticsQuery> rejectedReportedLOCount(LocalDateTime from, LocalDateTime to, List<Long> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  r.reviewedBy,\n" +
                "  count(DISTINCT lo.id) AS c\n" +
                "FROM ImproperContent r\n" +
                "  JOIN LearningObject lo ON r.learningObject = lo.id\n";
        String where = "WHERE r.reviewed = 1\n" +
                "      AND r.reviewedBy IN (:users)\n" +
                "      AND lo.deleted = 1\n" +
                "      AND r.status IN ('REJECTED', 'DELETED')\n";
        String groupBy = "GROUP BY r.reviewedBy";
        return query(select, where, groupBy, from, to, users, taxons, "r", "reviewedAt");
    }

    public List<StatisticsQuery> acceptedChangedLOCount(LocalDateTime from, LocalDateTime to, List<Long> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  r.reviewedBy,\n" +
                "  count(DISTINCT lo.id) AS c\n" +
                "FROM LearningObject lo\n" +
                "  JOIN ReviewableChange r ON r.learningObject = lo.id\n";
        String where = "WHERE r.reviewed = 1\n" +
                "      AND r.reviewedBy IN (:users)\n" +
                "      AND r.status IN ('ACCEPTED')\n";
        String groupBy = "GROUP BY r.reviewedBy";
        return query(select, where, groupBy, from, to, users, taxons, "r", "reviewedAt");
    }


    public List<StatisticsQuery> rejectedChangedLOCount(LocalDateTime from, LocalDateTime to, List<Long> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  r.reviewedBy,\n" +
                "  count(DISTINCT lo.id) AS c\n" +
                "FROM LearningObject lo\n" +
                "  JOIN ReviewableChange r ON r.learningObject = lo.id\n";
        String where = "WHERE r.reviewed = 1\n" +
                "      AND r.reviewedBy IN (:users)\n" +
                "      AND r.status IN ('REJECTED')\n" ;
        String groupBy = "GROUP BY r.reviewedBy";
        return query(select, where, groupBy, from, to, users, taxons, "r", "reviewedAt");
    }

    public List<StatisticsQuery> createdPortfolioCount(LocalDateTime from, LocalDateTime to, List<Long> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  lo.creator,\n" +
                "  count(lo.id) AS c\n" +
                "FROM LearningObject lo\n" +
                "  JOIN Portfolio p ON lo.id = p.id\n";
        String where = "WHERE lo.creator IN (:users)\n";
        String groupBy = "GROUP BY lo.creator";
        return query(select, where, groupBy, from, to, users, taxons, "lo", "added");
    }

    public List<StatisticsQuery> createdPublicPortfolioCount(LocalDateTime from, LocalDateTime to, List<Long> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  lo.creator,\n" +
                "  count(lo.id) AS c\n" +
                "FROM LearningObject lo\n" +
                "  JOIN Portfolio p ON lo.id = p.id\n";
        String where = "WHERE lo.creator IN (:users)\n" +
                "  AND lo.visibility = 'PUBLIC'\n";
        String groupBy = "GROUP BY lo.creator";
        return query(select, where, groupBy, from, to, users, taxons, "lo", "added");
    }

    public List<StatisticsQuery> createdMaterialCount(LocalDateTime from, LocalDateTime to, List<Long> users, List<Long> taxons) {
        String select = "SELECT\n" +
                "  lo.creator,\n" +
                "  count(lo.id) AS c\n" +
                "FROM LearningObject lo\n" +
                "  JOIN Material m ON lo.id = m.id\n";
        String where = "WHERE lo.creator IN (:users)\n";
        String groupBy = "GROUP BY lo.creator";
        return query(select, where, groupBy, from, to, users, taxons, "lo", "added");
    }

    private List<StatisticsQuery> query(String initialSelect, String initialWhere, String groupBy, LocalDateTime from, LocalDateTime to, List<Long> users, List<Long> taxons, String prefix, String date) {
        String select = modifySelectWithTaxons(taxons, initialSelect);
        String where = setWhere(initialWhere, from, to, taxons, prefix, date);
        Query query = entityManager.createNativeQuery(select + where + groupBy);
        List<Object[]> users1 = setParams(query, from, to, taxons, users).getResultList();
        return StatisticsUtil.convertToStatisticsQuery(users1);
    }

    private String modifySelectWithTaxons(List<Long> taxons, String select) {
        return CollectionUtils.isEmpty(taxons) ? select : select + "  JOIN LearningObject_Taxon lt ON lt.learningObject = lo.id\n";
    }

    private String setWhere(String where, LocalDateTime from, LocalDateTime to, List<Long> taxons, String prefix, String reviewedAt) {
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

    private Query setParams(Query query, LocalDateTime from, LocalDateTime to, List<Long> taxons, List<Long> users) {
        if (CollectionUtils.isNotEmpty(taxons)) {
            query = query.setParameter("taxons", taxons);
        }
        if (CollectionUtils.isNotEmpty(users)) {
            query = query.setParameter("users", users);
        }
        if (from != null) {
            query = query.setParameter("from", from.toString());
        }
        if (to != null) {
            query = query.setParameter("to", to.toString());
        }
        return query;
    }

    private String firstReviewInner(LocalDateTime from, LocalDateTime to) {
        String fr = "SELECT ic.learningObject\n" +
                "                        FROM FirstReview ic\n" +
                "                        WHERE ic.learningObject = lo.id\n" +
                "                              AND ic.reviewed = 1\n";
        if (from != null) {
            fr = fr + " AND ic.reviewedAt > :from";
        }
        if (to != null) {
            fr = fr + " AND ic.reviewedAt < :to";
        }
        return fr;
    }

    private String improperContentInner(LocalDateTime from, LocalDateTime to) {
        String ic = "SELECT ic.learningObject\n" +
                "                        FROM ImproperContent ic\n" +
                "                        WHERE ic.learningObject = lo.id\n" +
                "                              AND ic.reviewed = 1\n";
        if (from != null) {
            ic = ic + " AND ic.reviewedAt > :from";
        }
        if (to != null) {
            ic = ic + " AND ic.reviewedAt < :to";
        }
        return ic;
    }
}
