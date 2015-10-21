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
            @QueryParam("title") @DefaultValue(value = "") String title,
            @QueryParam("author") @DefaultValue(value = "") String author,
            @QueryParam("combined_description") @DefaultValue(value = "") String combinedDescription) {

        subject = subject.isEmpty() ? null : subject;
        resourceType = resourceType.isEmpty() ? null : resourceType;
        educationalContext = educationalContext.isEmpty() ? null : educationalContext;
        licenseType = licenseType.isEmpty() ? null : licenseType;
        title = title.isEmpty() ? null : title;
        author = author.isEmpty() ? null : author;
        combinedDescription = combinedDescription.isEmpty() ? null : combinedDescription;

        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject(subject);
        searchFilter.setResourceType(resourceType);
        searchFilter.setEducationalContext(educationalContext);
        searchFilter.setLicenseType(licenseType);
        searchFilter.setTitle(title);
        searchFilter.setAuthor(author);
        searchFilter.setCombinedDescription(combinedDescription);

        if (start == null) {
            return searchService.search(query, searchFilter);
        } else {
            return searchService.search(query, start, searchFilter);
        }
    }

}
