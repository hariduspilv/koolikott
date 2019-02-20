package ee.hm.dop.dao.firstreview;

import ee.hm.dop.dao.AbstractDao;
import ee.hm.dop.dao.AdminLearningObjectDao;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.User;
import ee.hm.dop.model.administration.PageableQuery;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Inject;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FirstReviewDao extends AbstractDao<FirstReview> {

    public static final String TITLE_SEARCH_CONDITION = " AND lo.id IN (SELECT LO.id\n" +
            "FROM LearningObject LO\n" +
            "       JOIN Portfolio P ON LO.id = P.id\n" +
            "WHERE P.title LIKE :title\n" +
            "UNION ALL\n" +
            "SELECT LO.id\n" +
            "FROM LearningObject LO\n" +
            "       JOIN Material M ON LO.id = M.id\n" +
            "       JOIN Material_Title MT ON M.id = MT.material\n" +
            "       JOIN LanguageString LS ON MT.title = LS.id\n" +
            "WHERE LS.lang = 1\n" + //todo lang =1
            "AND LS.textValue LIKE :title )";
    public static final String JOIN_LO_TAXON = " JOIN LearningObject_Taxon lt ON lt.learningObject = lo.id\n";
    public static final String LEFT_JOIN_TRANSLATION = "LEFT JOIN TaxonPosition tp ON lt.taxon = tp.taxon\n" +
            " LEFT JOIN Taxon t ON t.id = tp.domain\n" +
            " LEFT JOIN Translation tr ON t.translationKey = tr.translationKey AND tr.translationGroup =";
    public static final String JOIN_TRANSLATION = " JOIN TaxonPosition tp ON tp.taxon = lt.taxon\n" +
            " JOIN Taxon t ON t.id = tp.domain\n" +
            " JOIN Translation tr ON t.translationKey = tr.translationKey";
    public static final String LT_TAXON_IN = "  " +
            "AND lt.taxon IN (SELECT TP1.taxon\n" +
            "FROM LearningObject_Taxon lt1,TaxonPosition TP1\n" +
            "WHERE lt1.learningObject = lo.id\n" +
            "AND (TP1.educationalContext IN (:taxons) AND lt1.taxon = TP1.educationalContext\n" +
            "OR TP1.domain IN (:taxons) AND lt1.taxon = TP1.domain\n" +
            "OR TP1.subject IN (:taxons) AND lt1.taxon = TP1.subject\n" +
            "OR TP1.module IN (:taxons) AND lt1.taxon = TP1.module\n" +
            "OR TP1.specialization IN (:taxons) AND lt1.taxon = TP1.specialization\n" +
            "OR TP1.topic IN (:taxons) AND lt1.taxon = TP1.topic\n" +
            "OR TP1.subtopic IN (:taxons) AND lt1.taxon = TP1.subtopic)\n" +
            "GROUP BY taxon) " +
            "AND lt.taxon IN (:taxons)";
    public static final String SELECT_LO = "SELECT lo.id\n" +
            "FROM LearningObject lo\n" +
            "  JOIN FirstReview r ON r.learningObject = lo.id\n" +
            "  LEFT JOIN User u ON lo.creator = u.id\n";
    public static final String FIRST_REVIEW_WHERE = " WHERE r.reviewed = 0\n" +
            "      AND lo.deleted = 0\n" +
            "      AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
            "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
            "                        FROM ImproperContent ic\n" +
            "                        WHERE ic.learningObject = lo.id\n" +
            "                              AND ic.reviewed = 0)\n";
    public static final String GROUP_BY_LO_ID = " GROUP BY lo.id\n";
    @Inject
    private AdminLearningObjectDao adminLearningObjectDao;
    @Inject
    private TaxonDao taxonDao;

    public List<AdminLearningObject> findAllUnreviewed(PageableQuery params) {
        String sqlString = SELECT_LO +
                (params.existsTaxons() ? JOIN_LO_TAXON : "") +
                (params.isUserTaxon() ? LEFT_JOIN_TRANSLATION + params.getLang() : "") +
                (params.byAnySubject() ? JOIN_LO_TAXON + JOIN_TRANSLATION : "") +
                FIRST_REVIEW_WHERE +
                (params.byAnySubject() ? " AND tr.translationGroup =" + params.getLang() : "") +
                (params.existsTaxons() && !params.isUserTaxon() ? LT_TaxonIn(params) : "") +
                (params.existsTaxons() && params.isUserTaxon() ? LT_TAXON_IN : "") +
                (params.existsQuery() ? TITLE_SEARCH_CONDITION : "") +
                GROUP_BY_LO_ID +
                params.order();

        Query query = getEntityManager()
                .createNativeQuery(sqlString)
                .setFirstResult(params.getOffset())
                .setMaxResults(params.getSize());
        query = addTitle(params, query);
        query = addUserTaxons(params, query);

        List<BigInteger> resultList = query.getResultList();
        return adminLearningObjectDao.findById(toLongs(resultList));
    }

    private List<Long> toLongs(List<BigInteger> resultList) {
        return resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
    }

    private Query addUserTaxons(PageableQuery params, Query query) {
        if (params.getTaxons().size() > 0 && params.isUserTaxon()) {
            //todo why are taxons passed as strings
            List<Long> collect = params.getTaxons().stream().map(Long::valueOf).collect(Collectors.toList());
            List<Long> userTaxonIds = taxonDao.getUserTaxonWithChildren(collect);
            query = query.setParameter("taxons", userTaxonIds);
        }
        return query;
    }

    private Query addTitle(PageableQuery params, Query query) {
        if (params.existsQuery()) {
            return query.setParameter("title", "%" + params.getQuery() + "%");
        }
        return query;
    }

    private String LT_TaxonIn(PageableQuery params) {
        //todo why are taxons added to string
        return "  AND lt.taxon IN (" + String.join("", params.getTaxons()) + ") ";
    }

    /**
     * won't work, should use common method
     */
    @Deprecated
    public List<AdminLearningObject> findAllUnreviewed(User user, PageableQuery params) {
        String titleQuery = params.existsQuery() ?
                " AND lo.id IN (SELECT LO.id\n" +
                        "FROM LearningObject LO\n" +
                        "       JOIN Portfolio P ON LO.id = P.id\n" +
                        "WHERE P.title LIKE :title \n" +
                        "UNION ALL\n" +
                        "SELECT LO.id\n" +
                        "FROM LearningObject LO\n" +
                        "       JOIN Material M ON LO.id = M.id\n" +
                        "       JOIN Material_Title MT ON M.id = MT.material\n" +
                        "       JOIN LanguageString LS ON MT.title = LS.id\n" +
                        "WHERE LS.lang = 1\n" +
                        "AND LS.textValue LIKE :title )" : "";

        String taxonsQuery1 = params.existsTaxons() ? "  " + JOIN_LO_TAXON + "\n" : "";
        String taxonsQuery2 = params.existsTaxons() && !params.isUserTaxon() ? "  AND lt.taxon IN (" + user.getId() + ") " : "";

        String userTaxonQuery2 = params.isUserTaxon() ?
                "LEFT JOIN TaxonPosition tp ON lt.taxon = tp.taxon\n" +
                        " LEFT JOIN Taxon t ON t.id = tp.domain\n" +
                        " LEFT JOIN Translation tr ON t.translationKey = tr.translationKey AND tr.translationGroup =" + params.getLang() : "";

        String userTaxonQuery3 = params.existsTaxons() && params.isUserTaxon() ? LT_TAXON_IN : " ";

        String sortByDomain1 = params.bySubject() || params.byNSubject() ?
                " " + JOIN_LO_TAXON + "\n" +
                        " JOIN TaxonPosition tp ON tp.taxon = lt.taxon\n" +
                        " JOIN Taxon t ON t.id = tp.domain\n" +
                        " JOIN Translation tr ON t.translationKey = tr.translationKey" : "";
        String sortByDomain2 = params.bySubject() || params.byNSubject() ? " AND tr.translationGroup =" + params.getLang() : "";

        String sqlString = "SELECT lo.id\n" +
                "FROM LearningObject lo\n" +
                "  JOIN FirstReview r ON r.learningObject = lo.id\n" +
                "  LEFT JOIN User u ON lo.creator = u.id\n" +
                taxonsQuery1 +
                userTaxonQuery2 +
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
                userTaxonQuery3 +
                titleQuery +
                GROUP_BY_LO_ID +
                params.order();

        Query query = getEntityManager()
                .createNativeQuery(sqlString)
                .setFirstResult(params.getOffset())
                .setMaxResults(params.getSize());

        query = addTitle(params, query);
        if (params.getTaxons().size() > 0 && params.isUserTaxon()) {
            List<BigInteger> userTaxonIds = getEntityManager()
                    .createNativeQuery(FirstReviewQueryHelper.userTaxonQuery1)
                    .setParameter("userTaxonId", user.getId())
                    .getResultList();
            query = query.setParameter("taxons", userTaxonIds);
        }

        List<BigInteger> resultList = query.getResultList();
        return adminLearningObjectDao.findById(toLongs(resultList));

    }

    public long findCountOfUnreviewed(PageableQuery params) {
        if (CollectionUtils.isNotEmpty(params.getTaxons()) && params.isUserTaxon()) {
            return findCountOfUnreviewed(params.getTaxons());
        } else {
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
                    ).getSingleResult())
                    .longValue();
        }
    }

    public long findCountOfUnreviewed(List<String> list) {
        String sqlString = "SELECT count(a.id) as lo_count\n" +
                "FROM (SELECT lo.id\n" +
                "       FROM LearningObject lo\n" +
                "              JOIN FirstReview r ON r.learningObject = lo.id\n" +
                "              " + JOIN_LO_TAXON + "\n" +
                "              LEFT JOIN TaxonPosition tp ON lt.taxon = tp.taxon\n" +
                "              LEFT JOIN Taxon t ON t.id = tp.domain\n" +
                "              LEFT JOIN Translation tr ON t.translationKey = tr.translationKey AND tr.translationGroup = 1\n" +
                "       WHERE r.reviewed = 0\n" +
                "         AND lo.deleted = 0\n" +
                "         AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                "         AND lo.id NOT IN (SELECT ic.learningObject\n" +
                "                           FROM ImproperContent ic\n" +
                "                           WHERE ic.learningObject = lo.id\n" +
                "                             AND ic.reviewed = 0)\n" +
                "         AND lt.taxon IN (SELECT TP1.taxon\n" +
                "                          FROM LearningObject_Taxon lt1,\n" +
                "                               TaxonPosition TP1\n" +
                "                          WHERE lt1.learningObject = lo.id\n" +
                "                            AND (TP1.educationalContext IN (:taxons) AND lt1.taxon = TP1.educationalContext\n" +
                "                            OR TP1.domain IN (:taxons) AND lt1.taxon = TP1.domain\n" +
                "                            OR TP1.subject IN (:taxons) AND lt1.taxon = TP1.subject\n" +
                "                            OR TP1.module IN (:taxons) AND lt1.taxon = TP1.module\n" +
                "                            OR TP1.specialization IN (:taxons) AND lt1.taxon = TP1.specialization\n" +
                "                            OR TP1.topic IN (:taxons) AND lt1.taxon = TP1.topic\n" +
                "                            OR TP1.subtopic IN (:taxons) AND lt1.taxon = TP1.subtopic)\n" +
                "                          GROUP BY taxon)\n" +
                "         AND lt.taxon IN (:taxons)\n" +
                "       GROUP BY lo.id) a;";

        List<Long> collect = list.stream().map(Long::valueOf).collect(Collectors.toList());
        List<Long> taxon = taxonDao.getUserTaxonWithChildren(collect);
        return ((BigInteger) getEntityManager()
                .createNativeQuery(sqlString)
                .setParameter("taxons", taxon)
                .getSingleResult())
                .longValue();
    }
}
