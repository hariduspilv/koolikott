package ee.hm.dop.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import ee.hm.dop.dao.ResourceTypeDAO;
import ee.hm.dop.model.ResourceType;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class ResourceTypeServiceTest {

    @TestSubject
    private ResourceTypeService resourceTypeService = new ResourceTypeService();

    @Mock
    private ResourceTypeDAO resourceTypeDAO;

    @Test
    public void get() {
        String name = "audio";
        ResourceType resourceType = new ResourceType();
        resourceType.setId(123L);
        resourceType.setName(name);

        expect(resourceTypeDAO.findResourceTypeByName(name)).andReturn(resourceType);

        replay(resourceTypeDAO);

        ResourceType result = resourceTypeService.getResourceTypeByName(name);

        verify(resourceTypeDAO);

        assertEquals(resourceType, result);
    }

}
