package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import ee.hm.dop.model.taxon.Taxon;

public class TaxonSerializer extends JsonSerializer<Taxon> {

    @Override
    public void serialize(Taxon taxon, JsonGenerator generator, SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {
        generator.writeNumberField("id", taxon.getId());
    }

    @Override
    public void serializeWithType(Taxon taxon, JsonGenerator generator, SerializerProvider serializerProvider,
            TypeSerializer typeSerializer) throws IOException {
        typeSerializer.writeTypePrefixForScalar(taxon, generator);
        serialize(taxon, generator, serializerProvider);
        typeSerializer.writeTypeSuffixForScalar(taxon, generator);
    }
}
