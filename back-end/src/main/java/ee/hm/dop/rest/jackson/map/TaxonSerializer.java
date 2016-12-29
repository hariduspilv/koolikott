package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.TaxonDTO;

/**
 * Created by mart on 9.12.16.
 */
public class TaxonSerializer extends JsonSerializer<Taxon> {

    @Override
    public void serialize(Taxon value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        throw new RuntimeException("Taxons should be serialized with type as they are with a polymorphic structure");
    }

    @Override
    public void serializeWithType(Taxon value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        gen.writeObject(new TaxonDTO(value.getId(), value.getName()));
    }
}
