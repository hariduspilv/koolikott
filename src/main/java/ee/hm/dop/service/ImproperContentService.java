package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;

import ee.hm.dop.dao.ImproperContentDAO;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.User;

/**
 * Created by mart on 9.02.16.
 */
public class ImproperContentService {

    @Inject
    private ImproperContentDAO improperContentDAO;

    public ImproperContent addImproper(ImproperContent improperContent, User loggedInUser) {
        improperContent.setCreator(loggedInUser);
        improperContent.setAdded(DateTime.now());

        return improperContentDAO.update(improperContent);
    }

    public List<ImproperContent> getImproperPortfolios() {
        return improperContentDAO.findImproperPortfolios();
    }

    public List<ImproperContent> getImproperMaterials() {
        return improperContentDAO.findImproperMaterials();
    }

    public Boolean hasSetImproperPortfolio(long portfolioId, User loggedInUser) {
        List<ImproperContent> improperContents;
        if (isUserAdmin(loggedInUser)) {
            improperContents = improperContentDAO.getByPortfolio(portfolioId);
        } else {
            improperContents = improperContentDAO.findByPortfolioAndUser(portfolioId, loggedInUser);
        }

        return improperContents.size() != 0;
    }

    public Boolean hasSetImproperMaterial(long materialId, User loggedInUser) {
        List<ImproperContent> improperContents;
        if (isUserAdmin(loggedInUser)) {
            improperContents = improperContentDAO.getByMaterial(materialId);
        } else {
            improperContents = improperContentDAO.findByMaterialAndUser(materialId, loggedInUser);
        }

        return improperContents.size() != 0;
    }

    public void deleteByMaterial(long materialId) {
        improperContentDAO.deleteImproperMaterials(materialId);

    }

    public void deleteByPortfolio(long portfolioId) {
        improperContentDAO.deleteImproperPortfolios(portfolioId);
    }

    private boolean isUserAdmin(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getRole() == Role.ADMIN;
    }
}
