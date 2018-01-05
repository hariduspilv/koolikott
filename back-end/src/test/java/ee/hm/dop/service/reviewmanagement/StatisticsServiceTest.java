package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.dao.specialized.StatisticsDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsQuery;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsResult;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsRow;
import ee.hm.dop.service.statistics.StatisticsService;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(EasyMockRunner.class)
public class StatisticsServiceTest {

    @TestSubject
    private StatisticsService statisticsService = new StatisticsService();
    @Mock
    private StatisticsDao statisticsDao;
    @Mock
    private UserDao userDao;
    @Mock
    private TaxonDao taxonDao;

    @Test
    public void ask_for_statistics_1_moderator() throws Exception {
        StatisticsQuery query = query(1L, 1L);
        User user = user(1L);

        expect(userDao.getUsersByRole(Role.MODERATOR)).andReturn(newArrayList(user));
        expect(statisticsDao.reviewedLOCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query));
        expect(statisticsDao.approvedReportedLOCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query));
        expect(statisticsDao.rejectedReportedLOCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query));
        expect(statisticsDao.acceptedChangedLOCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query));
        expect(statisticsDao.rejectedChangedLOCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query));
        expect(statisticsDao.reportedLOCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query));
        expect(statisticsDao.createdPortfolioCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query));
        expect(statisticsDao.createdPublicPortfolioCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query));
        expect(statisticsDao.createdMaterialCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query));
        expect(taxonDao.getUserTaxons(user)).andReturn(new ArrayList<>());

        replayAll();

        StatisticsResult statistics = statisticsService.statistics(new StatisticsFilterDto(), admin());
        assertEquals(1, statistics.getRows().size());
        StatisticsRow firstRow = userRow(user, statistics);
        assertEquals(1L, firstRow.getReviewedLOCount().longValue());
        assertEquals(1L, firstRow.getApprovedReportedLOCount().longValue());
        assertEquals(1L, firstRow.getDeletedReportedLOCount().longValue());
        assertEquals(1L, firstRow.getAcceptedChangedLOCount().longValue());
        assertEquals(1L, firstRow.getRejectedChangedLOCount().longValue());
        assertEquals(1L, firstRow.getReportedLOCount().longValue());
        assertEquals(1L, firstRow.getPortfolioCount().longValue());
        assertEquals(1L, firstRow.getPublicPortfolioCount().longValue());
        assertEquals(1L, firstRow.getMaterialCount().longValue());

        StatisticsRow sumRow = statistics.getSum();
        assertNull(sumRow.getUser());
        assertNull(sumRow.getUsertaxons());
        assertEquals(1L, sumRow.getReviewedLOCount().longValue());
        assertEquals(1L, sumRow.getApprovedReportedLOCount().longValue());
        assertEquals(1L, sumRow.getDeletedReportedLOCount().longValue());
        assertEquals(1L, sumRow.getAcceptedChangedLOCount().longValue());
        assertEquals(1L, sumRow.getRejectedChangedLOCount().longValue());
        assertEquals(1L, sumRow.getReportedLOCount().longValue());
        assertEquals(1L, sumRow.getPortfolioCount().longValue());
        assertEquals(1L, sumRow.getPublicPortfolioCount().longValue());
        assertEquals(1L, sumRow.getMaterialCount().longValue());

        verifyAll();
    }

    @Test
    public void ask_for_statistics_2_moderators() throws Exception {
        StatisticsQuery query1 = query(1L, 1L);
        StatisticsQuery query2 = query(2L, 2L);
        StatisticsQuery query3 = query(3L, 3L);

        User user1 = user(1L);
        User user2 = user(2L);
        expect(userDao.getUsersByRole(Role.MODERATOR)).andReturn(newArrayList(user1, user2));
        expect(statisticsDao.reviewedLOCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query1, query2, query3));
        expect(statisticsDao.approvedReportedLOCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query1, query2));
        expect(statisticsDao.rejectedReportedLOCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query1, query2));
        expect(statisticsDao.acceptedChangedLOCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query1));
        expect(statisticsDao.rejectedChangedLOCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query1));
        expect(statisticsDao.reportedLOCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query1));
        expect(statisticsDao.createdPortfolioCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query2));
        expect(statisticsDao.createdPublicPortfolioCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query2));
        expect(statisticsDao.createdMaterialCount(anyObject(), anyObject(), anyObject(), anyObject())).andReturn(newArrayList(query2));
        expect(taxonDao.getUserTaxons(user1)).andReturn(new ArrayList<>());
        expect(taxonDao.getUserTaxons(user2)).andReturn(new ArrayList<>());

        replayAll();

        StatisticsResult statistics = statisticsService.statistics(new StatisticsFilterDto(), admin());
        assertEquals(2, statistics.getRows().size());
        StatisticsRow firstRow = userRow(user1, statistics);
        assertEquals(1L, firstRow.getReviewedLOCount().longValue());
        assertEquals(1L, firstRow.getApprovedReportedLOCount().longValue());
        assertEquals(1L, firstRow.getDeletedReportedLOCount().longValue());
        assertEquals(1L, firstRow.getAcceptedChangedLOCount().longValue());
        assertEquals(1L, firstRow.getRejectedChangedLOCount().longValue());
        assertEquals(1L, firstRow.getReportedLOCount().longValue());
        assertEquals(0L, firstRow.getPortfolioCount().longValue());
        assertEquals(0L, firstRow.getPublicPortfolioCount().longValue());
        assertEquals(0L, firstRow.getMaterialCount().longValue());

        StatisticsRow secondRow = userRow(user2, statistics);
        assertEquals(2L, secondRow.getReviewedLOCount().longValue());
        assertEquals(2L, secondRow.getApprovedReportedLOCount().longValue());
        assertEquals(2L, secondRow.getDeletedReportedLOCount().longValue());
        assertEquals(0L, secondRow.getAcceptedChangedLOCount().longValue());
        assertEquals(0L, secondRow.getRejectedChangedLOCount().longValue());
        assertEquals(0L, secondRow.getReportedLOCount().longValue());
        assertEquals(2L, secondRow.getPortfolioCount().longValue());
        assertEquals(2L, secondRow.getPublicPortfolioCount().longValue());
        assertEquals(2L, secondRow.getMaterialCount().longValue());

        StatisticsRow sumRow = statistics.getSum();
        assertNull(sumRow.getUser());
        assertNull(sumRow.getUsertaxons());
        assertEquals(3L, sumRow.getReviewedLOCount().longValue());
        assertEquals(3L, sumRow.getApprovedReportedLOCount().longValue());
        assertEquals(3L, sumRow.getDeletedReportedLOCount().longValue());
        assertEquals(1L, sumRow.getAcceptedChangedLOCount().longValue());
        assertEquals(1L, sumRow.getRejectedChangedLOCount().longValue());
        assertEquals(1L, sumRow.getReportedLOCount().longValue());
        assertEquals(2L, sumRow.getPortfolioCount().longValue());
        assertEquals(2L, sumRow.getPublicPortfolioCount().longValue());
        assertEquals(2L, sumRow.getMaterialCount().longValue());

        verifyAll();
    }

    private StatisticsRow userRow(User user, StatisticsResult statistics) {
        return statistics.getRows().stream().filter(r -> r.getUser().getId().equals(user.getId())).findAny().orElseThrow(RuntimeException::new);
    }

    private User user(long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private StatisticsQuery query(long userId, long count) {
        StatisticsQuery query = new StatisticsQuery();
        query.setCount(count);
        query.setUserId(userId);
        return query;
    }

    private User admin() {
        User loggedInUser = new User();
        loggedInUser.setRole(Role.ADMIN);
        return loggedInUser;
    }

    private void replayAll(Object... mocks) {
        replay(statisticsDao, userDao, taxonDao);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(statisticsDao, userDao, taxonDao);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}