package ee.hm.dop.dao.firstreview;

import ee.hm.dop.dao.AbstractDao;
import ee.hm.dop.dao.AdminLearningObjectDao;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.User;
import ee.hm.dop.model.administration.PageableQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FirstReviewDao extends AbstractDao<FirstReview> {

    public static final String JOIN_USER = " LEFT JOIN User u ON lo.creator = u.id\n";
    public static final String JOIN_MATERIAL = " LEFT JOIN Material m ON lo.id = m.id\n";
    public static final String JOIN_MATERIAL_TYPE_PORTFOLIO = " INNER JOIN Portfolio m ON lo.id = m.id\n";
    public static final String JOIN_MATERIAL_TYPE_MATERIAL = " INNER JOIN Material m ON lo.id = m.id\n";
    private final Logger logger = LoggerFactory.getLogger(FirstReviewDao.class);

    public static final String TITLE_SEARCH_CONDITION = " AND lo.id IN (SELECT LO.id\n" +
            "FROM LearningObject LO\n" +
            "       JOIN Portfolio P ON LO.id = P.id\n" +
            "WHERE LOWER(P.title) LIKE :title\n" +
            "UNION ALL\n" +
            "SELECT LO.id\n" +
            "FROM LearningObject LO\n" +
            "       JOIN Material M ON LO.id = M.id\n" +
            "       JOIN Material_Title MT ON M.id = MT.material\n" +
            "       JOIN LanguageString LS ON MT.title = LS.id\n" +
            "WHERE LS.lang = :transgroup\n" +
            "AND LOWER(LS.textValue) LIKE :title )";
    public static final String JOIN_LO_TAXON = " JOIN LearningObject_Taxon lt ON lt.learningObject = lo.id\n";
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
    public static final String FIRST_REVIEW_WHERE = " WHERE r.reviewed = 0\n" +
            "      AND lo.deleted = 0\n" +
            "      AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
            "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
            "                        FROM ImproperContent ic\n" +
            "                        WHERE ic.learningObject = lo.id\n" +
            "                              AND ic.reviewed = 0)\n";
    public static final String GROUP_BY_LO_ID = " GROUP BY lo.id\n";
    public static final String LT_TAXON_USER_CONDITION = "  and lt.taxon in (:usertaxons)\n";
    public static final String SELECT_LO_ID_B = "SELECT lo.id\n" +
            "FROM LearningObject lo\n" +
            "       JOIN FirstReview r ON r.learningObject = lo.id\n";
    public static final String JOIN_TAXON_TRANSLATIONS = "" +
            "       left join TaxonPosition tp on lt.taxon = tp.taxon\n" +
            "       left JOIN Taxon t ON t.id = tp.domain\n" +
            "       left JOIN Translation tr ON t.translationKey = tr.translationKey and tr.translationGroup = :transgroup\n" +
            "left JOIN Taxon t2 ON t2.id = tp.subject\n" +
            "       left JOIN Translation tr2 ON t2.translationKey = tr2.translationKey and tr2.translationGroup = :transgroup\n";
    @Inject
    private AdminLearningObjectDao adminLearningObjectDao;
    @Inject
    private TaxonDao taxonDao;

    public List<AdminLearningObject> findAllUnreviewed(PageableQuery params) {
        String sqlString2 = "\n" +
                SELECT_LO_ID_B +
                (params.hasFilterByTypeMaterial() ? JOIN_MATERIAL_TYPE_MATERIAL : "") +
                (params.hasFilterByTypePortfolio() ? JOIN_MATERIAL_TYPE_PORTFOLIO: "") +
                (params.hasOrderByType() ? JOIN_MATERIAL : "") +
                (params.hasCreatorOrder() ? JOIN_USER : "") +
                (params.hasTaxonsOrUsers() || params.hasSubjectOrder() ? JOIN_LO_TAXON : "") +
                (params.hasSubjectOrder() ? JOIN_TAXON_TRANSLATIONS : "") +
                FIRST_REVIEW_WHERE +
                (params.hasTaxons() ? LT_TAXON_IN : "") +
                (params.hasUsers() ? LT_TAXON_USER_CONDITION : "") +
                (params.hasSearch() ? TITLE_SEARCH_CONDITION : "") +
                GROUP_BY_LO_ID +
                params.order();

        logger.info(sqlString2);

        Query query2 = getEntityManager()
                .createNativeQuery(sqlString2)
                .setFirstResult(params.getOffset())
                .setMaxResults(params.getSize());
        query2 = params.hasSearch() ? addTitle(params, query2) : query2;
        query2 = params.hasUsers() ? addUserTaxons(params, query2) : query2;
        query2 = params.hasTaxons() ? addTaxons(params, query2) : query2;
        query2 = params.hasSearch() ? addLanguageGroup(params, query2) : query2;
        query2 = params.hasSubjectOrder() || params.hasSearch() ? addLanguageGroup(params, query2) : query2;

        List<Long> orderedLongs = toLongs(query2.getResultList());
        List<AdminLearningObject> learningObjects = adminLearningObjectDao.findById(orderedLongs);
        learningObjects.sort(Comparator.comparing(v -> orderedLongs.indexOf(v.getId())));
        return learningObjects;
    }

    public Long findCoundOfAllUnreviewed(PageableQuery params) {
        String sqlString2 = " select count(a.id) from (\n" +
                SELECT_LO_ID_B +
                (params.hasFilterByTypeMaterial() ? JOIN_MATERIAL_TYPE_MATERIAL : "") +
                (params.hasFilterByTypePortfolio() ? JOIN_MATERIAL_TYPE_PORTFOLIO: "") +
                (params.hasTaxonsOrUsers() || params.hasSubjectOrder() ? JOIN_LO_TAXON : "") +
                FIRST_REVIEW_WHERE +
                (params.hasTaxons() ? LT_TAXON_IN : "") +
                (params.hasUsers() ? LT_TAXON_USER_CONDITION : "") +
                (params.hasSearch() ? TITLE_SEARCH_CONDITION : "") +
                GROUP_BY_LO_ID + " ) a";

        Query query2 = getEntityManager()
                .createNativeQuery(sqlString2);
        query2 = params.hasSearch() ? addTitle(params, query2) : query2;
        query2 = params.hasUsers() ? addUserTaxons(params, query2) : query2;
        query2 = params.hasTaxons() ? addTaxons(params, query2) : query2;
        query2 = params.hasSearch() ? addLanguageGroup(params, query2) : query2;

        return ((BigInteger) query2.getSingleResult()).longValue();
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

    private List<Long> toLongs(List<BigInteger> resultList) {
        return resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
    }

    private Query addUserTaxons(PageableQuery params, Query query) {
        return query.setParameter("usertaxons", taxonDao.getUserTaxonWithChildren(params.getUsers()));
    }

    private Query addTaxons(PageableQuery params, Query query) {
        return query.setParameter("taxons", params.getTaxons());
    }

    private Query addLanguageGroup(PageableQuery params, Query query) {
        return query.setParameter("transgroup", params.getLang());
    }

    private Query addTitle(PageableQuery params, Query query) {
        return query.setParameter("title", "%" + params.getQuery() + "%");
    }
}
