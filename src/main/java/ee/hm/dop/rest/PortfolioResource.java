package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.MaterialService;
import ee.hm.dop.service.PortfolioService;
import ee.hm.dop.service.TaxonService;
import ee.hm.dop.service.UserService;

@Path("portfolio")
public class PortfolioResource extends BaseResource {

    @Inject
    private PortfolioService portfolioService;

    @Inject
    private UserService userService;

    @Inject
    private TaxonService taxonService;
    @Inject
    private MaterialService materialService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Portfolio get(@QueryParam("id") long portfolioId) {
        return portfolioService.get(portfolioId);
    }

    @GET
    @Path("getByCreator")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Portfolio> getByCreator(@QueryParam("username") String username) {
        if (isBlank(username)) {
            throwBadRequestException("Username parameter is mandatory");
        }

        User creator = userService.getUserByUsername(username);
        if (creator == null) {
            throwBadRequestException("Invalid request");
        }

        return portfolioService.getByCreator(creator);
    }

    @GET
    @Path("/getPicture")
    @Produces("image/png")
    public Response getPictureById(@QueryParam("portfolioId") long id) {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(id);
        byte[] pictureData = portfolioService.getPortfolioPicture(portfolio);

        if (pictureData != null) {
            return Response.ok(pictureData).build();
        } else {
            return Response.status(HttpURLConnection.HTTP_NOT_FOUND).build();
        }
    }

    private void throwBadRequestException(String message) {
        throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(message).build());
    }

    @POST
    @Path("increaseViewCount")
    public void increaseViewCount(Portfolio portfolio) {
        portfolioService.incrementViewCount(portfolio);
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("USER")
    public Portfolio create(CreatePortfolioForm createPortfolioForm) {
        Portfolio portfolio = createPortfolioForm.getPortfolio();

        if (createPortfolioForm.getTaxonId() != null) {
            setTaxon(createPortfolioForm.getTaxonId(), portfolio);
        }

        return portfolioService.create(portfolio, getLoggedInUser());
    }

    private void setTaxon(Long taxonId, Portfolio portfolio) {
        Taxon taxon = taxonService.getTaxonById(taxonId);

        if (taxon == null) {
            throw new BadRequestException("Taxon does not exist.");
        }

        portfolio.setTaxon(taxon);
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("USER")
    public Portfolio update(UpdatePortfolioForm updatePortfolioForm) {
        Portfolio portfolio = updatePortfolioForm.portfolio;

        if (updatePortfolioForm.getTaxonId() != null) {
            setTaxon(updatePortfolioForm.getTaxonId(), portfolio);
        }

        List<Chapter> chapters = parseChapters(updatePortfolioForm.chapters);
        portfolio.setChapters(chapters);

        return portfolioService.update(portfolio, getLoggedInUser());
    }

    private List<Chapter> parseChapters(List<ChapterForm> chapterForms) {
        List<Chapter> chapters = new ArrayList<>();

        if (chapterForms != null) {
            for (ChapterForm chapterForm : chapterForms) {
                chapters.add(chapterForm.chapter);

                List<Material> materials = parseMaterials(chapterForm.getMaterials());
                chapterForm.chapter.setMaterials(materials);

                List<Chapter> subchapters = parseChapters(chapterForm.subchapters);
                chapterForm.chapter.setSubchapters(subchapters);
            }
        }

        return chapters;
    }

    private List<Material> parseMaterials(List<Long> materialIds) {
        List<Material> materials = new ArrayList<>();
        if (materialIds != null) {
            for (Long materialId : materialIds) {
                Material material = materialService.get(materialId);
                if (material == null) {
                    throw new BadRequestException(format("Material [id=%s] does not exist.", materialId));
                }

                materials.add(material);
            }
        }

        return materials;
    }

    /**
     * This is an workaround to bypass JSOG/Jackson problem:
     * https://github.com/jsog/jsog-jackson/pull/8
     */
    public static class CreatePortfolioForm {

        private Long taxonId;
        private Portfolio portfolio;

        public Long getTaxonId() {
            return taxonId;
        }

        public void setTaxonId(Long taxon) {
            this.taxonId = taxon;
        }

        public Portfolio getPortfolio() {
            return portfolio;
        }

        public void setPortfolio(Portfolio portfolio) {
            this.portfolio = portfolio;
        }
    }

    /**
     * This is an workaround to bypass JSOG/Jackson problem:
     * https://github.com/jsog/jsog-jackson/pull/8
     */
    public static class UpdatePortfolioForm {

        private Portfolio portfolio;
        private Long taxonId;
        private List<ChapterForm> chapters;

        public Portfolio getPortfolio() {
            return portfolio;
        }

        public void setPortfolio(Portfolio portfolio) {
            this.portfolio = portfolio;
        }

        public Long getTaxonId() {
            return taxonId;
        }

        public void setTaxonId(Long taxonId) {
            this.taxonId = taxonId;
        }

        public List<ChapterForm> getChapters() {
            return chapters;
        }

        public void setChapters(List<ChapterForm> chapters) {
            this.chapters = chapters;
        }

    }

    /**
     * This is an workaround to bypass JSOG/Jackson problem:
     * https://github.com/jsog/jsog-jackson/pull/8
     */
    public static class ChapterForm {
        private Chapter chapter;
        private List<Long> materials;
        private List<ChapterForm> subchapters;

        public void setMaterials(List<Long> materials) {
            this.materials = materials;
        }

        public void setChapter(Chapter chapter) {
            this.chapter = chapter;
        }

        public List<Long> getMaterials() {
            return materials;
        }

        public Chapter getChapter() {
            return chapter;
        }

        public List<ChapterForm> getSubchapters() {
            return subchapters;
        }

        public void setSubchapters(List<ChapterForm> subchapters) {
            this.subchapters = subchapters;
        }

    }
}
