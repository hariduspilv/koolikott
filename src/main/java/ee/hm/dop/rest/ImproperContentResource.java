package ee.hm.dop.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.service.ImproperContentService;
import ee.hm.dop.service.MaterialService;
import ee.hm.dop.service.PortfolioService;

/**
 * Created by mart on 9.02.16.
 */
@Path("impropers")
public class ImproperContentResource extends BaseResource {

    public static final String MATERIAL = "MATERIAL";
    public static final String PORTFOLIO = "PORTFOLIO";
    @Inject
    private MaterialService materialService;

    @Inject
    private PortfolioService portfolioService;

    @Inject
    private ImproperContentService improperContentService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN", "PUBLISHER", "RESTRICTED"})
    public ImproperContent setImproper(ImproperContent improperContent) {
        if (improperContent.getMaterial() != null && improperContent.getMaterial().getId() != null) {
            Material material = materialService.get(improperContent.getMaterial().getId(), getLoggedInUser());
            if (material == null) {
                throw new RuntimeException("Material not found while adding improper material");
            }

            improperContent.setMaterial(material);
        } else if (improperContent.getPortfolio() != null && improperContent.getPortfolio().getId() != null) {
            Portfolio portfolio = portfolioService.get(improperContent.getPortfolio().getId(), getLoggedInUser());
            if (portfolio == null) {
                throw new RuntimeException("Portfolio not found while adding improper portfolio");
            }

            improperContent.setPortfolio(portfolio);
        } else {
            throwBadRequestException("Material or portfolio is mandatory");
        }

        return improperContentService.addImproper(improperContent, getLoggedInUser());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public List<ImproperContent> getImpropers(@QueryParam("with") String type) {
        List<ImproperContent> improperContents = null;

        if (type == null) {
            throw new RuntimeException("Missing 'with' parameter, must specify if portfolio or material.");
        }

        if (type.toUpperCase().equals(MATERIAL)) {
            improperContents = improperContentService.getImproperMaterials();

        } else if (type.toUpperCase().equals(PORTFOLIO)) {
            improperContents = improperContentService.getImproperPortfolios();
        }

        return improperContents;
    }


    @GET
    @Path("materials/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN", "PUBLISHER", "RESTRICTED"})
    public Boolean hasSetImproperMaterial(@PathParam("id") long id) {
        return improperContentService.hasSetImproperMaterial(id, getLoggedInUser());
    }

    @GET
    @Path("portfolios/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN", "PUBLISHER", "RESTRICTED"})
    public Boolean hasSetImproperPortfolio(@PathParam("id") long id) {
        return improperContentService.hasSetImproperPortfolio(id, getLoggedInUser());
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public void removeImproper(@QueryParam("material") Long materialId, @QueryParam("portfolio") Long portfolioId) {
        if (materialId != null) {
            improperContentService.deleteByMaterial(materialId);
        } else if (portfolioId != null) {
            improperContentService.deleteByPortfolio(portfolioId);
        } else {
            throw new RuntimeException("Material or portfolio ID missing when trying to delete improper content entry");
        }
    }
}
