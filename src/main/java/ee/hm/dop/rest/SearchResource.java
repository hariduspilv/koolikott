package ee.hm.dop.rest;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Taxon;
import ee.hm.dop.service.SearchService;
import ee.hm.dop.service.TaxonService;

@Path("search")
public class SearchResource {

    @Inject
    private SearchService searchService;

    @Inject
    private TaxonService taxonService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult search(@QueryParam("q") String query, @QueryParam("start") Long start,
            @QueryParam("taxon") Long taxonId, @QueryParam("paid") @DefaultValue(value = "true") Boolean paid,
            @QueryParam("type") @DefaultValue(value = "") String type) {

        Taxon taxon = taxonService.getTaxonById(taxonId);
        type = type.isEmpty() ? null : type;

        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxon(taxon);
        searchFilter.setPaid(paid);
        searchFilter.setType(type);

        if (start == null) {
            return searchService.search(query, searchFilter);
        } else {
            return searchService.search(query, start, searchFilter);
        }
    }

}
