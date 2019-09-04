package ee.hm.dop.service.content;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.PortfolioLogDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.dao.TaxonPositionDao;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.PortfolioLog;
import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.TaxonLevel;
import ee.hm.dop.model.taxon.TaxonPosition;
import ee.hm.dop.model.taxon.TaxonPositionDTO;
import ee.hm.dop.service.permission.PortfolioPermission;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class PortfolioGetter {

    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private ReducedLearningObjectDao reducedLearningObjectDao;
    @Inject
    private PortfolioPermission portfolioPermission;
    @Inject
    private PortfolioLogDao portfolioLogDao;
    @Inject
    private TaxonPositionDao taxonPositionDao;

    public Portfolio get(Long portfolioId, User loggedInUser) {
        if (UserUtil.isAdminOrModerator(loggedInUser)) {
            setTaxonPosition(portfolioDao.findById(portfolioId));

            return portfolioDao.findById(portfolioId);
        }
        Portfolio portfolio = portfolioDao.findByIdNotDeleted(portfolioId);
        setTaxonPosition(portfolio);
        if (!portfolioPermission.canView(loggedInUser, portfolio)) {
            throw ValidatorUtil.permissionError();
        }
        return portfolio;
    }

    public SearchResult getByCreatorResult(User creator, User loggedInUser, int start, int maxResults) {
        List<Searchable> searchables = new ArrayList<>(getByCreator(creator, loggedInUser, start, maxResults));
        Long size = getCountByCreator(creator);
        return new SearchResult(searchables, size, start);
    }

    public List<PortfolioLog> getPortfolioHistoryAll(Long portfolioId) {
        return portfolioLogDao.findAllPortfolioLogsByLoId(portfolioId);
    }

    public List<PortfolioLog> findByIdAndCreatorAllPortfolioLogs(Long portfolioId, User user) {
        return portfolioLogDao.findPortfolioLogsByLoIdAndUserId(portfolioId, user);
    }

    public PortfolioLog getPortfolioHistory(long portfolioHistoryId) {
        return portfolioLogDao.findById(portfolioHistoryId);
    }

    public List<ReducedLearningObject> getByCreator(User creator, User loggedInUser, int start, int maxResults) {
        return reducedLearningObjectDao.findPortfolioByCreator(creator, start, maxResults).stream()
                .filter(p -> portfolioPermission.canInteract(loggedInUser, p))
                .collect(toList());
    }

    public Long getCountByCreator(User creator) {
        return portfolioDao.findCountByCreator(creator);
    }

    public Portfolio findValid(Portfolio portfolio) {
        return ValidatorUtil.findValid(portfolio, (Function<Long, Portfolio>) portfolioDao::findByIdNotDeleted);
    }

    private void setTaxonPosition(Portfolio portfolio) {
        List<TaxonPosition> taxonPosition = portfolio.getTaxons()
                .stream()
                .map(taxonPositionDao::findByTaxon)
                .filter(Objects::nonNull)
                .collect(toList());

        List<TaxonPositionDTO> taxonPositionDTOList = new ArrayList<>();
        taxonPosition.forEach(tp -> {
            Taxon educationalContext = tp.getEducationalContext();
            if (educationalContext != null) {
                TaxonPositionDTO tpdEduContext = new TaxonPositionDTO();
                tpdEduContext.setTaxonLevelId(educationalContext.getId());
                tpdEduContext.setTaxonLevelName(educationalContext.getName());
                tpdEduContext.setTaxonLevel(TaxonLevel.EDUCATIONAL_CONTEXT);
                if (tp.getDomain() != null) {
                    TaxonPositionDTO tpdDomain = new TaxonPositionDTO();
                    tpdDomain.setTaxonLevelId(tp.getDomain().getId());
                    tpdDomain.setTaxonLevelName(tp.getDomain().getName());
                    tpdDomain.setTaxonLevel(TaxonLevel.DOMAIN);
                    taxonPositionDTOList.add(tpdEduContext);
                    taxonPositionDTOList.add(tpdDomain);
                }
            }
        });

        portfolio.setTaxonPositionDto(taxonPositionDTOList);
    }
}
