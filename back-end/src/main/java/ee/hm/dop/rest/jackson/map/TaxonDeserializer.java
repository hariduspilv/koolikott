package ee.hm.dop.rest.jackson.map;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import ee.hm.dop.guice.GuiceInjector;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.TaxonService;

public class TaxonDeserializer extends JsonDeserializer<Taxon> {

    @Override
    public Taxon deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        TaxonService taxonService = getTaxonService();

        List values = parser.readValueAs(List.class);
        String idString = values.get(2).toString();
        Long id = Long.valueOf(idString);

        return taxonService.getTaxonById(id);
    }

    @Override
    public Taxon deserializeWithType(JsonParser jp, DeserializationContext context, TypeDeserializer typeDeserializer)
            throws IOException {
        return deserialize(jp, context);
    }

    protected TaxonService getTaxonService() {
        return GuiceInjector.getInjector().getInstance(TaxonService.class);
    }
}
