package ee.hm.dop.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.dao.LanguageDAO;
import ee.hm.dop.model.Page;
import ee.hm.dop.service.PageService;

@Path("page")
public class PageResource {

    @Inject
    private PageService pageService;

    @Inject
    LanguageDAO languageDao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Page get(@QueryParam("pageName") String pageName, @QueryParam("pageLanguage") String languageCode) {
        return pageService.getPage(pageName, languageDao.findByCode(languageCode));
    }
}
