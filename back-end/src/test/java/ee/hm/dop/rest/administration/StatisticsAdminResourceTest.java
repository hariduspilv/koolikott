package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.TaxonDTO;
import ee.hm.dop.service.reviewmanagement.dto.FileFormat;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import static ee.hm.dop.rest.useractions.UserResourceTest.GET_TAXON_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StatisticsAdminResourceTest extends ResourceIntegrationTestBase {
    private static final String SEARCH_STATISTICS = "admin/statistics/";
    private static final String EXPORT_STATISTICS = "admin/statistics/export";
    private static final String EXPORT_STATISTICS_DOWNLOAD = "admin/statistics/export/download/";
    public static final DateTime FROM = new DateTime(2000, 10, 10, 10, 10);
    public static final DateTime TO = new DateTime(2020, 10, 10, 10, 10);

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
    public void admin_from_only_doesnt_fail() throws Exception {
        login(USER_ADMIN);
        StatisticsFilterDto dto = new StatisticsFilterDto();
        dto.setFrom(FROM);
        StatisticsResult result = doPost(SEARCH_STATISTICS, dto, StatisticsResult.class);
        assertEquals(result.getFilter().getFrom(), dto.getFrom());
        assertEquals(result.getFilter().getTo(), dto.getTo());
        assertEquals(result.getFilter().getTaxon(), dto.getTaxon());
        if (dto.getUser() != null) {
            assertEquals(result.getFilter().getUser().getUsername(), dto.getUser().getUsername());
        } else {
            assertEquals(result.getFilter().getUser(), dto.getUser());
        }
        assertTrue(CollectionUtils.isNotEmpty(result.getRows()));
        assertTrue(result.getSum() != null);
    }

    @Test
    public void admin_to_only_doesnt_fail() throws Exception {
        login(USER_ADMIN);
        StatisticsFilterDto dto = new StatisticsFilterDto();
        dto.setTo(TO);
        StatisticsResult result = doPost(SEARCH_STATISTICS, dto, StatisticsResult.class);
        assertEquals(result.getFilter().getFrom(), dto.getFrom());
        assertEquals(result.getFilter().getTo(), dto.getTo());
        assertEquals(result.getFilter().getTaxon(), dto.getTaxon());
        if (dto.getUser() != null) {
            assertEquals(result.getFilter().getUser().getUsername(), dto.getUser().getUsername());
        } else {
            assertEquals(result.getFilter().getUser(), dto.getUser());
        }
        assertTrue(CollectionUtils.isNotEmpty(result.getRows()));
        assertTrue(result.getSum() != null);
    }

    @Test
    public void admin_taxon_search_doesnt_fail() throws Exception {
        login(USER_ADMIN);
        StatisticsFilterDto dto = new StatisticsFilterDto();
        Domain taxon = (Domain) doGet(String.format(GET_TAXON_URL, TAXON_MATHEMATICS_DOMAIN.id), Taxon.class);
        dto.setTaxon(taxon);
        StatisticsResult result = doPost(SEARCH_STATISTICS, dto, StatisticsResult.class);
        assertEquals(result.getFilter().getFrom(), dto.getFrom());
        assertEquals(result.getFilter().getTo(), dto.getTo());
        assertEquals(result.getFilter().getTaxon(), dto.getTaxon());
        if (dto.getUser() != null) {
            assertEquals(result.getFilter().getUser().getUsername(), dto.getUser().getUsername());
        } else {
            assertEquals(result.getFilter().getUser(), dto.getUser());
        }
        assertTrue(CollectionUtils.isNotEmpty(result.getRows()));
        assertTrue(result.getSum() != null);
    }

    @Test
    public void admin_user_search_doesnt_fail() throws Exception {
        login(USER_ADMIN);
        StatisticsFilterDto dto = new StatisticsFilterDto();
        dto.setUser(getUser(USER_MODERATOR));
        StatisticsResult result = doPost(SEARCH_STATISTICS, dto, StatisticsResult.class);
        assertEquals(result.getFilter().getFrom(), dto.getFrom());
        assertEquals(result.getFilter().getTo(), dto.getTo());
        assertEquals(result.getFilter().getTaxon(), dto.getTaxon());
        if (dto.getUser() != null) {
            assertEquals(result.getFilter().getUser().getUsername(), dto.getUser().getUsername());
        } else {
            assertEquals(result.getFilter().getUser(), dto.getUser());
        }
        assertTrue(CollectionUtils.isNotEmpty(result.getRows()));
        assertTrue(result.getSum() != null);
    }

    @Test
    public void admin_full_search_doesnt_fail() throws Exception {
        login(USER_ADMIN);
        StatisticsFilterDto dto = new StatisticsFilterDto();
        dto.setFrom(FROM);
        dto.setTo(TO);
        Domain taxon = (Domain) doGet(String.format(GET_TAXON_URL, TAXON_MATHEMATICS_DOMAIN.id), Taxon.class);
        dto.setTaxon(taxon);
        dto.setUser(getUser(USER_MODERATOR));
        StatisticsResult result = doPost(SEARCH_STATISTICS, dto, StatisticsResult.class);
        assertEquals(result.getFilter().getFrom(), dto.getFrom());
        assertEquals(result.getFilter().getTo(), dto.getTo());
        assertEquals(result.getFilter().getTaxon(), dto.getTaxon());
        if (dto.getUser() != null) {
            assertEquals(result.getFilter().getUser().getUsername(), dto.getUser().getUsername());
        } else {
            assertEquals(result.getFilter().getUser(), dto.getUser());
        }
        assertTrue(CollectionUtils.isNotEmpty(result.getRows()));
        assertTrue(result.getSum() != null);
    }

    @Test
    public void admin_can_generate_excel_97() throws Exception {
        generateAndDownload(FileFormat.xls, "xls");
    }

    @Test
    public void admin_can_generate_excel_2003plus() throws Exception {
        generateAndDownload(FileFormat.xlsx, "xlsx");
    }

    @Test
    public void admin_can_generate_csv() throws Exception {
        generateAndDownload(FileFormat.csv, "csv");
    }

    private void generateAndDownload(FileFormat format, String filename) throws IOException {
        login(USER_ADMIN);
        StatisticsFilterDto dto = new StatisticsFilterDto();
        dto.setFrom(FROM);
        dto.setTo(TO);
        Domain taxon = (Domain) doGet(String.format(GET_TAXON_URL, TAXON_MATHEMATICS_DOMAIN.id), Taxon.class);
        dto.setTaxon(taxon);
        dto.setUser(getUser(USER_MODERATOR));
        dto.setFormat(format);
        Response response = doPost(EXPORT_STATISTICS, dto);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String fileName = response.readEntity(String.class);
        Response response2 = doGet(EXPORT_STATISTICS_DOWNLOAD + fileName, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        assertEquals(Response.Status.OK.getStatusCode(), response2.getStatus());

        assertEquals("\"statistika_aruanne." + filename + "\"", response2.getHeaderString("Content-Disposition").split("filename=")[1]);

        byte[] bytes = response2.readEntity(byte[].class);
        File file = new File(filename);
        FileUtils.writeByteArrayToFile(file, bytes);
        assertTrue(file.length() > 0);
        file.deleteOnExit();
    }
}