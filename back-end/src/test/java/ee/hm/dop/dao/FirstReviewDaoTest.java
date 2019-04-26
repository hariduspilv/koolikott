package ee.hm.dop.dao;

import com.google.inject.Inject;
import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.dao.firstreview.FirstReviewDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.administration.PageableQueryUnreviewed;
import ee.hm.dop.model.administration.Sort;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
@Ignore//TODO
public class FirstReviewDaoTest extends DatabaseTestBase{

    @Inject
    private FirstReviewDao firstReviewDao;
    @Inject
    private UserDao userDao;

    @Test
    public void query_does_not_fail() {
        PageableQueryUnreviewed pageableQuery = new PageableQueryUnreviewed();
        User moderator = userDao.findById(USER_MODERATOR.id);
        pageableQuery.setUsers(Arrays.asList(moderator.getId()));
        pageableQuery.setMaterialType("Material");
        pageableQuery.setItemSortedBy("byCreatedAt");
        pageableQuery.setLang(1);
        pageableQuery.setTaxons(Collections.singletonList(1L));
        pageableQuery.setSort(Sort.DESC);
        firstReviewDao.findAllUnreviewed(pageableQuery);
    }
}