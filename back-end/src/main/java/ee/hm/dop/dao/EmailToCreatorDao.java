package ee.hm.dop.dao;

import ee.hm.dop.model.EmailToCreator;
import ee.hm.dop.model.User;
import ee.hm.dop.model.administration.PageableQuery;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

public class EmailToCreatorDao extends AbstractDao<EmailToCreator> {

    private static final String MAIN_SQL_1 = "SELECT * FROM EmailToCreator e";
    private static final String MAIN_COUNT_SQL_1 = "SELECT count(*) FROM EmailToCreator e";
    private static final String MAIN_SQL_2 = " WHERE e.senderId= :user ";
    private static final String SEARCH_CONDITION_1 = " JOIN User U ON e.user = U.id";
    private static final String SEARCH_CONDITION_2 = " AND (LOWER(U.userName) LIKE :searchObject";
    private static final String GROUP_BY_ETC_ID = " GROUP BY e.id ";
    private static final String SEARCH_CONDITION_3 = " OR e.learningObjectId IN (" +
            "SELECT LO.id FROM LearningObject LO\n " +
            " JOIN Portfolio P ON LO.id = P.id\n" +
            " WHERE LOWER(P.title) LIKE :searchObject\n" +
            " UNION ALL\n" +
            " SELECT LO.id\n" +
            " FROM LearningObject LO\n" +
            " JOIN Material M ON LO.id = M.id\n" +
            " JOIN Material_Title MT ON M.id = MT.material\n" +
            " JOIN LanguageString LS ON MT.title = LS.id\n" +
            " WHERE LS.lang = :transgroup\n" +
            " AND LOWER(LS.textValue) LIKE :searchObject))";

    private static final String SORT_BY_TITLE = " JOIN LearningObject lob ON e.learningObjectId = lob.id\n" +
            "WHERE e.senderId= :user" +
            " AND e.learningObjectId IN\n" +
            "(SELECT LO.id\n" +
            "FROM LearningObject LO\n" +
            "JOIN Portfolio P ON LO.id = P.id\n" +
            "UNION ALL\n" +
            "SELECT LO.id\n" +
            "FROM LearningObject LO\n" +
            "JOIN Material M ON LO.id = M.id\n" +
            "JOIN Material_Title MT ON M.id = MT.material\n" +
            "JOIN LanguageString LS ON MT.title = LS.id\n" +
            "WHERE LS.lang = :transgroup)\n";

    public EmailToCreator findBySenderId(User user) {
        return findByField("senderId", user);
    }

    public List<EmailToCreator> getSenderSentEmails(User user, PageableQuery params) {
        String sqlString = MAIN_SQL_1 +
                (params.hasSearch() ? SEARCH_CONDITION_1 : "") +
                (params.hasEmailReceiverOrder() && !params.hasSearch()  ? SEARCH_CONDITION_1 : "") +
                (params.hasOrderByTitle() ? SORT_BY_TITLE : "") +
                (params.hasOrderByTitle() ? "" : MAIN_SQL_2) +
                (params.hasSearch() ? SEARCH_CONDITION_2 + SEARCH_CONDITION_3 : "") +
                GROUP_BY_ETC_ID +
                params.order();

        Query query = getEntityManager()
                .createNativeQuery(sqlString, EmailToCreator.class)
                .setFirstResult(params.getOffset())
                .setMaxResults(params.getSize())
                .setParameter("user", user);

        query = params.hasSearch() ? addSearchObject(params, query) : query;
        query = params.hasSearch() ? addLanguageGroup(params, query) : query;
        query = params.hasOrderByTitle() ? addLanguageGroup(params, query) : query;

        return query.getResultList();
    }

    public Long getSenderSentEmailsCount(User user) {
        return (Long) getEntityManager()
                .createQuery("SELECT count(*) FROM EmailToCreator e WHERE sender = :user")
                .setParameter("user", user)
                .getSingleResult();
    }

    public Long getSenderSentEmailCount(User user, PageableQuery params) {
        String sqlString = MAIN_COUNT_SQL_1 +
                (params.hasSearch() ? SEARCH_CONDITION_1 : "") +
                MAIN_SQL_2 +
                (params.hasSearch() ? SEARCH_CONDITION_2 + SEARCH_CONDITION_3 : "");

        Query query = getEntityManager()
                .createNativeQuery(sqlString)
                .setParameter("user", user);

        query = params.hasSearch() ? addSearchObject(params, query) : query;
        query = params.hasSearch() ? addLanguageGroup(params, query) : query;
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    private Query addSearchObject(PageableQuery params, Query query) {
        return query.setParameter("searchObject", "%" + params.getQuery() + "%");
    }

    private Query addLanguageGroup(PageableQuery params, Query query) {
        return query.setParameter("transgroup", params.getLang());
    }
}
