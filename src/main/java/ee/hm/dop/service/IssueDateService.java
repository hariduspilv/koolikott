package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.IssueDateDAO;
import ee.hm.dop.model.IssueDate;

/**
 * Created by mart on 2.12.15.
 */
public class IssueDateService {

    @Inject
    private IssueDateDAO issueDateDAO;

    public IssueDate createIssueDate(IssueDate issueDate) {
        return issueDateDAO.create(issueDate);
    }
}
