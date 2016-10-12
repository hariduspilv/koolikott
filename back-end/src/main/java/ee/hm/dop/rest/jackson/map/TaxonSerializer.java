package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.TaxonDTO;

public class TaxonSerializer extends JsonSerializer<Taxon> {

    @Override
    public void serialize(Taxon taxon, JsonGenerator generator, SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {
        generator.writeNumberField("id", taxon.getId());
    }

    @Override
    public void serializeWithType(Taxon taxon, JsonGenerator generator, SerializerProvider serializerProvider,
                                  TypeSerializer typeSerializer) throws IOException {
        // typeSerializer.writeTypePrefixForScalar(taxon, generator);
        //serialize(taxon, generator, serializerProvider);
        // typeSerializer.writeTypeSuffixForScalar(taxon, generator);
        TaxonSmallDTO taxonDTO = new TaxonSmallDTO(taxon);
        generator.writeObject(taxonDTO);
    }

    private class TaxonSmallDTO {
        private Long id;
        private String level;
        private String name;

        public TaxonSmallDTO(Taxon taxon) {
            id = taxon.getId();
            level = "." + taxon.getClass().getSimpleName();
            name = taxon.getName();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
