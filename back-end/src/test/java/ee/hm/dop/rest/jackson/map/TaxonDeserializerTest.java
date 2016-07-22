package ee.hm.dop.rest.jackson.map;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.TaxonService;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class TaxonDeserializerTest {

    @TestSubject
    private TaxonDeserializer deserializer = new PartialMockTaxonDeserializer();

    @Mock
    private JsonParser parser;

    @Mock
    private DeserializationContext context;

    @Mock
    private TypeDeserializer typeDeserializer;

    @Mock
    private TaxonService taxonService;

    @Test
    public void deserializeDomain() {
        Domain domain = new Domain();
        domain.setId(101L);
        List<Object> values = Arrays.asList(".Domain", "id", 101);
        deserialize("[\".Domain\", \"id\", 101]", values, domain);
    }

    private void deserialize(String taxonString, List<Object> expectedValues, Taxon expected) {
        try {
            expect(parser.readValueAs(List.class)).andReturn(expectedValues);
            Long taxonId = Long.valueOf(expectedValues.get(2).toString());
            expect(taxonService.getTaxonById(taxonId)).andReturn(expected);
        } catch (IOException e) {
        }

        replay(parser, context, typeDeserializer, taxonService);

        Taxon result = null;

        try {
            result = deserializer.deserializeWithType(parser, context, typeDeserializer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        verify(parser, context, typeDeserializer, taxonService);

        assertEquals(expected, result);
    }

    private class PartialMockTaxonDeserializer extends TaxonDeserializer {

        @Override
        protected TaxonService getTaxonService() {
            return taxonService;
        }

    }

}
