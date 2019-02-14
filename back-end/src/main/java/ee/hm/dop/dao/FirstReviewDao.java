package ee.hm.dop.dao;

import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.User;
import ee.hm.dop.model.administration.PageableQuery;

import javax.inject.Inject;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class FirstReviewDao extends AbstractDao<FirstReview> {

    @Inject
    private TaxonDao taxonDao;
    @Inject
    private AdminLearningObjectDao adminLearningObjectDao;

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

    public List<AdminLearningObject> findAllUnreviewed(String sortingOrder) {
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
                        "ORDER BY min(r.createdAt) " + sortingOrder)
                .setMaxResults(200)
                .getResultList();
        List<Long> collect = resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
        return adminLearningObjectDao.findById(collect);
    }

    public List<AdminLearningObject> findAllUnreviewed(PageableQuery params) {

        String titleQuery = params.existsQuery() ?
                " AND lo.id IN (select LO.id\n" +
                "from LearningObject LO\n" +
                "       join Portfolio P on LO.id = P.id\n" +
                "where P.title like :title \n" +
                "union all\n" +
                "select LO.id\n" +
                "from LearningObject LO\n" +
                "       join Material M on LO.id = M.id\n" +
                "       join Material_Title MT on M.id = MT.material\n" +
                "       join LanguageString LS on MT.title = LS.id\n" +
                "where LS.lang = 1\n" +
                "and LS.textValue like :title )" : "";

        String fromListToString = String.join("",params.getTaxons());
        String taxonsQuery1 = params.existsTaxons() ? "  JOIN LearningObject_Taxon lt on lt.learningObject = lo.id\n" : "";
        String taxonsQuery2= params.existsTaxons() ? "  AND lt.taxon in (" + fromListToString + ") " : "";

        String sortByDomain1 = params.getItemSortedBy().equals("bySubject") || params.getItemSortedBy().equals("-bySubject") ?
                " JOIN LearningObject_Taxon lt on lt.learningObject = lo.id\n" +
                " JOIN TaxonPosition tp on tp.taxon = lt.taxon\n" +
                " JOIN Taxon t ON t.id = tp.domain\n" +
                " JOIN Translation tr ON t.translationKey = tr.translationKey" : "";
        String sortByDomain2 = params.getItemSortedBy().equals("bySubject") || params.getItemSortedBy().equals("-bySubject") ? " AND tr.translationGroup = 1" : "";

        String sqlString = "SELECT\n" +
                "  lo.id\n" +
                "FROM LearningObject lo\n" +
                "  JOIN FirstReview r ON r.learningObject = lo.id\n" +
                taxonsQuery1 +
                sortByDomain1 +
                " WHERE r.reviewed = 0\n" +
                "      AND lo.deleted = 0\n" +
                "      AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                "                        FROM ImproperContent ic\n" +
                "                        WHERE ic.learningObject = lo.id\n" +
                "                              AND ic.reviewed = 0)\n" +
                sortByDomain2 +
                taxonsQuery2 +
                titleQuery +
                " GROUP BY lo.id\n" +
                order(params);

        Query query = getEntityManager()
                .createNativeQuery(sqlString)
                .setFirstResult(params.getOffset())
                .setMaxResults(params.getSize());

        if (params.existsQuery()){
            query = query.setParameter("title", "%" + params.getQuery() + "%");
        }

        List<BigInteger> resultList = query.getResultList();

        List<Long> collect = resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
        return adminLearningObjectDao.findById(collect);
    }

    private String order(PageableQuery pageableQuery) {
        String sortedBy = pageableQuery.getItemSortedBy();
        if (sortedBy.equals("byCreatedAt")) {
            return "ORDER BY min(r.createdAt)" + pageableQuery.getSort().name();
        } else if (sortedBy.equals("-byCreatedAt")) {
            return "ORDER BY max(r.createdAt)" + pageableQuery.getSort().name();
        } else if (sortedBy.equals("byCreatedBy")) {
            return "ORDER BY min(lo.creator)" + pageableQuery.getSort().name();
        } else if (sortedBy.equals("-byCreatedBy")) {
            return "ORDER BY max(lo.creator)" + pageableQuery.getSort().name();
        }else if (sortedBy.equals("bySubject")) {
            return "ORDER BY min(tr.translation) " + pageableQuery.getSort().name();
        }else if (sortedBy.equals("-bySubject")) {
            return "ORDER BY max(tr.translation) " + pageableQuery.getSort().name();
        } else {
            throw new UnsupportedOperationException("unknown sort");
        }
    }

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

    public List<AdminLearningObject> findAllUnreviewed(User user, PageableQuery params) {

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
                        "GROUP BY lo.id\n" + "")
//                        itemToSortedBy + pageableQuery.getDirection())
                .setParameter("taxonIds", taxonDao.getUserTaxonsWithChildren(user))
                .setFirstResult(params.getOffset())
                .setMaxResults(params.getSize())
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
                .setParameter("taxonIds", taxonDao.getUserTaxonsWithChildren(user))
                .getSingleResult()).longValue();
    }
}
