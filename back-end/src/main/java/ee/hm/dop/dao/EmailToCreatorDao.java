package ee.hm.dop.dao;

import ee.hm.dop.model.EmailToCreator;
import ee.hm.dop.model.User;
import ee.hm.dop.model.administration.PageableQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;
import java.util.List;

public class EmailToCreatorDao extends AbstractDao<EmailToCreator> {

    public static final String MAIN_SQL_1 = "SELECT * FROM EmailToCreator e";
    public static final String MAIN_SQL_2 = " WHERE e.senderId= :user";
    public static final String SEARCH_CONDITION_1 = " JOIN User U ON e.user = U.id";
    public static final String SEARCH_CONDITION_2 = " AND LOWER(U.name) LIKE :searchObject OR LOWER(U.name) LIKE :searchObject ";
//    public static final String USER_SQL = " AND e.sender=:user ORDER BY e.sentAt DESC";

    private final Logger logger = LoggerFactory.getLogger(EmailToCreatorDao.class);

    public EmailToCreator findBySenderId(User user) {
        return findByField("senderId", user);
    }

    public List<EmailToCreator> getSenderSentEmails(User user, PageableQuery params) {

        String sqlString = MAIN_SQL_1 + (params.hasSearch() ? SEARCH_CONDITION_1 : "") + MAIN_SQL_2 + (params.hasSearch() ? SEARCH_CONDITION_2 : "");

        logger.info(sqlString);

        Query query = getEntityManager()
                .createNativeQuery(sqlString, EmailToCreator.class)
                .setFirstResult(params.getOffset())
                .setMaxResults(params.getSize())
                .setParameter("user", user);

        query = params.hasSearch() ? addSearchObject(params, query) : query;

        return query.getResultList();
    }

    private Query addSearchObject(PageableQuery params, Query query) {
        return query.setParameter("searchObject", "%" + params.getQuery() + "%");
    }

    public Long getSenderSentEmailsCount(User user) {
        return (Long) getEntityManager()
                .createQuery("SELECT count(*) FROM EmailToCreator e WHERE sender = :user")
                .setParameter("user", user)
                .getSingleResult();
    }
}
