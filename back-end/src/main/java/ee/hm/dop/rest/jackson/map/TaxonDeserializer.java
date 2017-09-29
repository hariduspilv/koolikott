package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.metadata.TaxonService;

public class TaxonDeserializer extends JsonDeserializer<Taxon> {

    @Override
    public Taxon deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        TaxonService taxonService = getTaxonService();

        Taxon taxon = parser.readValueAs(Taxon.class);
        if(taxon != null) {
            return taxonService.getTaxonById(taxon.getId());
        }

        return null;
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
