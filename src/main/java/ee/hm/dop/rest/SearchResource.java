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
            @QueryParam("license_type") @DefaultValue(value = "") String licenseType,
            @QueryParam("combined_description") @DefaultValue(value = "") String combinedDescription,
            @QueryParam("paid") @DefaultValue(value = "true") Boolean paid,
            @QueryParam("type") @DefaultValue(value = "") String type) {

        subject = subject.isEmpty() ? null : subject;
        resourceType = resourceType.isEmpty() ? null : resourceType;
        educationalContext = educationalContext.isEmpty() ? null : educationalContext;
        licenseType = licenseType.isEmpty() ? null : licenseType;
        combinedDescription = combinedDescription.isEmpty() ? null : combinedDescription;
        type = type.isEmpty() ? null : type;

        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject(subject);
        searchFilter.setResourceType(resourceType);
        searchFilter.setEducationalContext(educationalContext);
        searchFilter.setLicenseType(licenseType);
        searchFilter.setCombinedDescription(combinedDescription);
        searchFilter.setPaid(paid);
        searchFilter.setType(type);

        if (start == null) {
            return searchService.search(query, searchFilter);
        } else {
            return searchService.search(query, start, searchFilter);
        }
    }

}
