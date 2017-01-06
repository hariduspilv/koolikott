package ee.hm.dop.service;

import ee.hm.dop.dao.UserTourDataDAO;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserTourData;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

@RunWith(EasyMockRunner.class)
public class UserTourDataServiceTest {

    @TestSubject
    private UserTourDataService userTourDataService = new UserTourDataService();

    @Mock
    private UserTourDataDAO userTourDataDAO;

    @Test
    public void getUserTourData() {
        User user = new User();
        user.setId(1L);
        UserTourData expected = new UserTourData();
        expected.setUser(user);
        expected.setGeneralTour(true);

        expect(userTourDataDAO.getUserTourData(user)).andReturn(expected);

        replay(userTourDataDAO);

        UserTourData userTourData = userTourDataDAO.getUserTourData(user);

        verify(userTourDataDAO);

        assertNotNull(userTourData);
        assertTrue(userTourData.isGeneralTour());
    }

    @Test
    public void getUserTourDataNull() {
        User user = new User();
        user.setId(2L);

        UserTourData newData = new UserTourData();
        newData.setUser(user);

        expect(userTourDataDAO.getUserTourData(user)).andReturn(null);
        expect(userTourDataDAO.getUserTourData(user)).andReturn(newData);

        replay(userTourDataDAO);

        assertNull(userTourDataDAO.getUserTourData(user));

        UserTourData userTourData = userTourDataDAO.getUserTourData(user);

        assertNotNull(userTourData);
        assertEquals(userTourData.getUser().getId(), user.getId());

        verify(userTourDataDAO);
    }

    @Test
    public void addUserTourData() {
        User user = new User();
        user.setId(3L);

        UserTourData newData = new UserTourData();
        newData.setUser(user);
        newData.setEditTour(true);
        newData.setGeneralTour(true);

        expect(userTourDataDAO.addUserTourData(newData)).andReturn(newData);
        expect(userTourDataDAO.getUserTourData(user)).andReturn(newData);

        replay(userTourDataDAO);

        UserTourData added = userTourDataDAO.addUserTourData(newData);
        UserTourData receivedData = userTourDataDAO.getUserTourData(user);

        verify(userTourDataDAO);

        assertNotNull(added);
        assertNotNull(receivedData);
        assertEquals(added.getId(), receivedData.getId());
        assertEquals(added.getUser().getId(), receivedData.getUser().getId());
        assertEquals(added.isGeneralTour(), receivedData.isGeneralTour());
        assertEquals(added.isEditTour(), receivedData.isEditTour());
    }
}
