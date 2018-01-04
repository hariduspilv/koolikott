package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.TaxonDTO;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsResult;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static ee.hm.dop.rest.useractions.UserResourceTest.GET_TAXON_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StatisticsAdminResourceTest extends ResourceIntegrationTestBase{
    private static final String SEARCH_STATISTICS = "admin/statistics/";

    @Test
    public void anonymous_user_can_not_search_statistics() throws Exception {
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), doPost(SEARCH_STATISTICS).getStatus());
    }

    @Test
    public void regular_user_can_not_search_statistics() throws Exception {
        login(USER_PEETER);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), doPost(SEARCH_STATISTICS).getStatus());
    }

    @Test
    public void moderator_can_not_search_statistics() throws Exception {
        login(USER_MODERATOR);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), doPost(SEARCH_STATISTICS).getStatus());
    }

    @Test
    public void only_admin_can_search_statistics() throws Exception {
        login(USER_ADMIN);
        assertEquals(Response.Status.OK.getStatusCode(), doPost(SEARCH_STATISTICS).getStatus());
    }


    @Test
    public void only_admin_can_sea2rch_statistics() throws Exception {
        login(USER_ADMIN);
        StatisticsFilterDto dto = new StatisticsFilterDto();
        dto.setFrom(new DateTime(2000, 10, 10, 10, 10));
        dto.setTo(new DateTime(2020, 10, 10, 10, 10));
        Domain taxon = (Domain) doGet(String.format(GET_TAXON_URL, TAXON_MATHEMATICS_DOMAIN.id), Taxon.class);
        dto.setTaxon(taxon);
        dto.setUser(getUser(USER_MODERATOR));
        StatisticsResult result = doPost(SEARCH_STATISTICS, dto, StatisticsResult.class);
        assertEquals(result.getFilter().getFrom(), dto.getFrom());
        assertEquals(result.getFilter().getTo(), dto.getTo());
        assertEquals(result.getFilter().getTaxon(), dto.getTaxon());
        assertEquals(result.getFilter().getUser().getUsername(), dto.getUser().getUsername());
        assertTrue(CollectionUtils.isNotEmpty(result.getRows()));
        assertTrue(result.getSum() != null);
    }
}