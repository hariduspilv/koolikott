package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import ee.hm.dop.model.IssueDate;

/**
 * Created by mart on 2.12.15.
 */
public class IssueDateDAO {

    @Inject
    private EntityManager entityManager;

    public IssueDate create(IssueDate issueDate) {
        IssueDate merged = entityManager.merge(issueDate);
        entityManager.persist(merged);
        return merged;
    }
}
