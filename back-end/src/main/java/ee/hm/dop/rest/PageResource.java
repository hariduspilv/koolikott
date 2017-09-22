package ee.hm.dop.rest;

import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.Page;
import ee.hm.dop.service.metadata.LanguageService;
import ee.hm.dop.service.content.PageService;

@Path("page")
public class PageResource extends BaseResource {

    @Inject
    private PageService pageService;
    @Inject
    private LanguageService languageService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Page get(@QueryParam("name") String name, @QueryParam("language") String languageCode) {
        if (isBlank(name)) {
            throw badRequest("name parameter is mandatory");
        }

        if (isBlank(languageCode)) {
            throw badRequest("language parameter is mandatory");
        }

        Language language = languageService.getLanguage(languageCode);
        if (language == null) {
            throw badRequest("language not supported");
        }

        return pageService.getPage(name, language);
    }
}
