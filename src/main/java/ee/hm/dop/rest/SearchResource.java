package ee.hm.dop.rest;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.SearchResult;
import ee.hm.dop.service.SearchService;

@Path("search")
public class SearchResource {

    @Inject
    private SearchService searchService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult search(@QueryParam("q") String query, @QueryParam("start") Long start,
            @QueryParam("subject") @DefaultValue(value = "") String subject,
            @QueryParam("resource_type") @DefaultValue(value = "") String resourceType,
            @QueryParam("educational_context") @DefaultValue(value = "") String educationalContext,
            @QueryParam("license_type") @DefaultValue(value = "") String licenseType) {
        if (subject.isEmpty()) {
            subject = null;
        }

        if (resourceType.isEmpty()) {
            resourceType = null;
        }

        if (educationalContext.isEmpty()) {
            educationalContext = null;
        }

        if (licenseType.isEmpty()) {
            licenseType = null;
        }

        if (start == null) {
            return searchService.search(query, subject, resourceType, educationalContext, licenseType);
        } else {
            return searchService.search(query, start, subject, resourceType, educationalContext, licenseType);
        }
    }

}
