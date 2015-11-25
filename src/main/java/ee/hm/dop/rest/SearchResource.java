package ee.hm.dop.rest;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.Taxon;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.SearchService;
import ee.hm.dop.service.TaxonService;

@Path("search")
public class SearchResource {

    @Inject
    private SearchService searchService;

    @Inject
    private TaxonService taxonService;

    @Inject
    private LanguageService languageService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult search(@QueryParam("q") String query, @QueryParam("start") Long start, //
            @QueryParam("taxon") Long taxonId, //
            @QueryParam("paid") @DefaultValue(value = "true") Boolean paid, //
            @QueryParam("type") @DefaultValue(value = "") String type, //
            @QueryParam("language") @DefaultValue(value = "") String languageCode, //
            @QueryParam("targetGroup") @DefaultValue(value = "") String targetGroupName) {

        Taxon taxon = taxonService.getTaxonById(taxonId);
        Language language = languageService.getLanguage(languageCode);
        type = type.isEmpty() ? null : type;
        TargetGroup targetGroup = targetGroupName.isEmpty() ? null : TargetGroup.valueOf(targetGroupName.toUpperCase());

        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxon(taxon);
        searchFilter.setPaid(paid);
        searchFilter.setType(type);
        searchFilter.setLanguage(language);
        searchFilter.setTargetGroup(targetGroup);

        if (start == null) {
            return searchService.search(query, searchFilter);
        } else {
            return searchService.search(query, start, searchFilter);
        }
    }

}
