package ee.hm.dop.dao;

import ee.hm.dop.model.EmailToCreator;
import ee.hm.dop.model.User;
import ee.hm.dop.model.administration.PageableQuery;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@Repository
public class EmailToCreatorDao extends AbstractDao<EmailToCreator> {


        private static final String MAIN_SQL_1_SELECT = "SELECT * FROM EmailToCreator e";
        private static final String MAIN_COUNT_SQL_1 = "SELECT count(*) FROM EmailToCreator e";
        private static final String MAIN_SQL_2_WHERE = " WHERE e.senderId= :user ";
        private static final String SEARCH_CONDITION_1_JOIN_USER = " JOIN User U ON e.user = U.id";
        private static final String SEARCH_CONDITION_2_BY_NAME = " AND (LOWER(U.userName) LIKE :searchObject";
        private static final String GROUP_BY_ETC_ID = " GROUP BY e.id ";
        private static final String SEARCH_CONDITION_3_BY_LO_TITLE = " OR e.learningObjectId IN (" +
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
            String sqlString = MAIN_SQL_1_SELECT +
                    (params.hasSearch() ? SEARCH_CONDITION_1_JOIN_USER : "") +
                    (params.hasEmailReceiverOrder() && !params.hasSearch()  ? SEARCH_CONDITION_1_JOIN_USER : "") +
                    (params.hasOrderByTitle() ? SORT_BY_TITLE : "") +
                    (params.hasOrderByTitle() ? "" : MAIN_SQL_2_WHERE) +
                    (params.hasSearch() ? SEARCH_CONDITION_2_BY_NAME + SEARCH_CONDITION_3_BY_LO_TITLE : "") +
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
                    (params.hasSearch() ? SEARCH_CONDITION_1_JOIN_USER : "") +
                    MAIN_SQL_2_WHERE +
                    (params.hasSearch() ? SEARCH_CONDITION_2_BY_NAME + SEARCH_CONDITION_3_BY_LO_TITLE : "");

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

