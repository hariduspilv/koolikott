package ee.hm.dop.service.content;

import ee.hm.dop.dao.OriginalPictureDao;
import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Portfolio;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class PortfolioConverter {

    @Inject
    private OriginalPictureDao originalPictureDao;

    public Portfolio setFieldsToNewPortfolio(Portfolio portfolio) {
        Portfolio safePortfolio = new Portfolio();
        return setCommonFields(safePortfolio, portfolio);
    }

    public Portfolio setFieldsToExistingPortfolio(Portfolio to, Portfolio from) {
        setCommonFields(to, from);
        setUpdateFields(to, from);
        return to;
    }

    private void setUpdateFields(Portfolio to, Portfolio from) {
        if (changesToPublic(to, from)) to.setPublishedAt(DateTime.now());
        to.setVisibility(from.getVisibility());
        to.setPublicationConfirmed(from.isPublicationConfirmed());
    }

    private boolean changesToPublic(Portfolio to, Portfolio from) {
        return from.getVisibility().isPublic() && to.getVisibility().isNotPublic();
    }

    private Portfolio setCommonFields(Portfolio to, Portfolio from) {
        to.setTitle(from.getTitle());
        to.setSummary(from.getSummary());
        to.setTags(from.getTags());
        to.setTargetGroups(from.getTargetGroups());
        to.setTaxons(from.getTaxons());
        to.setChapters(from.getChapters());
        to.setPicture(from.getPicture());
        to.setLicenseType(from.getLicenseType());
        if (from.getPicture() != null) {
            if (from.getPicture().getId() == null) {
                to.setPicture(null);
            } else {
                OriginalPicture originalPicture = originalPictureDao.findById(from.getPicture().getId());
                to.getPicture().setData(originalPicture.getData());
                to.getPicture().setName(originalPicture.getName());
            }
        }
        return to;
    }
}
