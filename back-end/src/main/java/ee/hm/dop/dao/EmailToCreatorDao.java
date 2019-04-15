package ee.hm.dop.dao;

import ee.hm.dop.model.EmailToCreator;
import ee.hm.dop.model.User;
import ee.hm.dop.model.administration.PageableQuery;

import java.util.List;

public class EmailToCreatorDao extends AbstractDao<EmailToCreator> {

    public EmailToCreator findBySenderId(User user) {
        return findByField("senderId", user);
    }

    public List<EmailToCreator> getSenderSentEmails(User user, PageableQuery pageableQuery) {
        return getEntityManager().createQuery("" +
                "   SELECT e FROM EmailToCreator e WHERE e.sender=:user ORDER BY e.sentAt DESC", entity())
                .setParameter("user", user)
                .getResultList();
    }

    public Long getSentEmailsCount(User user) {
        return (Long) getEntityManager()
                .createQuery("SELECT count(*) FROM EmailToCreator e WHERE sender = :user")
                .setParameter("user", user)
                .getSingleResult();
    }
}
