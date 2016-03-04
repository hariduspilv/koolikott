package ee.hm.dop.rest.jackson.map;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;

@RunWith(EasyMockRunner.class)
public class TaxonSerializerTest {

    @TestSubject
    private TaxonSerializer serializer = new TaxonSerializer();

    @Mock
    private JsonGenerator generator;

    @Mock
    private SerializerProvider serializerProvider;

    @Mock
    private TypeSerializer typeSerializer;

    @Test
    public void serializeTaxon() {
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(3L);

        serialize(educationalContext);
    }

    private void serialize(Taxon taxon) {
        try {
            typeSerializer.writeTypePrefixForScalar(taxon, generator);
            generator.writeNumberField("id", taxon.getId());
            typeSerializer.writeTypeSuffixForScalar(taxon, generator);
        } catch (IOException e) {
            // ignore
        }
        replay(generator, serializerProvider, typeSerializer);

        try {
            serializer.serializeWithType(taxon, generator, serializerProvider, typeSerializer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        verify(generator, serializerProvider, typeSerializer);
    }

}
