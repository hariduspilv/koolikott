package ee.hm.dop.oaipmh;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.easymock.EasyMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Repository;

/**
 * Created by mart.laus on 23.07.2015.
 */
@RunWith(EasyMockRunner.class)
public class MaterialIteratorTest {

    @Test
    public void connect() throws Exception {
        MaterialIterator materialIterator = createMock(MaterialIterator.class);
        Repository repository = createMock(Repository.class);
        Iterator iterator = createMock(Iterator.class);

        expect(materialIterator.connect(repository)).andReturn(iterator);

        replay(materialIterator, repository, iterator);

        Iterator<Material> returnedIterator = materialIterator.connect(repository);

        verify(materialIterator, repository, iterator);

        assertEquals(returnedIterator, iterator);
    }

    @Test
    public void next() {
        MaterialIterator materialIterator = createMock(MaterialIterator.class);
        Material material = createMock(Material.class);

        expect(materialIterator.next()).andReturn(material);

        replay(materialIterator, material);

        Material newMaterial = materialIterator.next();

        verify(materialIterator, material);

        assertEquals(material, newMaterial);
    }

    @Test(expected = Exception.class)
    public void nextErrorInGettingMaterial() throws Exception {
        MaterialIterator materialIterator = createMock(MaterialIterator.class);
        Material material = createMock(Material.class);
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);
        Repository repository = createMock(Repository.class);

        expect(materialIterator.next()).andReturn(material);
        expect(getMaterialConnector.getMaterial(repository, "id", "prefix")).andThrow(new Exception());

        replay(materialIterator, material, getMaterialConnector);

        Material newMaterial = materialIterator.next();
        getMaterialConnector.getMaterial(repository, "id", "prefix");

        verify(materialIterator, material, getMaterialConnector);

        assertEquals(material, newMaterial);
    }
}
