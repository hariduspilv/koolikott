package ee.hm.dop.service.metadata;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import ee.hm.dop.dao.ResourceTypeDao;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.service.metadata.ResourceTypeService;
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
    private ResourceTypeDao resourceTypeDao;

    @Test
    public void get() {
        String name = "audio";
        ResourceType resourceType = new ResourceType();
        resourceType.setId(123L);
        resourceType.setName(name);

        expect(resourceTypeDao.findByName(name)).andReturn(resourceType);

        replay(resourceTypeDao);

        ResourceType result = resourceTypeService.getResourceTypeByName(name);

        verify(resourceTypeDao);

        assertEquals(resourceType, result);
    }

}
