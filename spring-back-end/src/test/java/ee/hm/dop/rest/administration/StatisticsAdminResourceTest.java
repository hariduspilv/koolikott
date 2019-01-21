package ee.hm.dop.rest.administration;

import com.google.common.collect.Lists;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.dto.FileFormat;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsResult;
import org.apache.commons.io.FileUtils;
import java.time.LocalDateTime;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

import static ee.hm.dop.rest.useractions.UserResourceTest.GET_TAXON_URL;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StatisticsAdminResourceTest extends ResourceIntegrationTestBase {
    private static final String SEARCH_STATISTICS = "admin/statistics/";
    private static final String EXPORT_STATISTICS = "admin/statistics/export";
    private static final String EXPORT_STATISTICS_DOWNLOAD = "admin/statistics/export/download/";
    public static final LocalDateTime FROM = LocalDateTime.of(2000, 10, 10, 10, 10);
    public static final LocalDateTime TO = LocalDateTime.of(2020, 10, 10, 10, 10);

    @Test
    public void anonymous_user_can_not_search_statistics() throws Exception {
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), doPost(SEARCH_STATISTICS).getStatus());
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
    public void only_admin_can_search_statistics_but_you_need_proper_request() throws Exception {
        login(USER_ADMIN);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), doPost(SEARCH_STATISTICS).getStatus());
    }

    @Test
    public void admin_from_only_results_in_bad_request() throws Exception {
        login(USER_ADMIN);
        StatisticsFilterDto dto = new StatisticsFilterDto();
        dto.setFrom(FROM);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), doPost(SEARCH_STATISTICS, dto).getStatus());
    }

    @Test
    public void admin_to_only_results_in_bad_request() throws Exception {
        login(USER_ADMIN);
        StatisticsFilterDto dto = new StatisticsFilterDto();
        dto.setTo(TO);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), doPost(SEARCH_STATISTICS, dto).getStatus());
    }

    @Test
    public void admin_taxon_search_doesnt_fail() throws Exception {
        login(USER_ADMIN);
        StatisticsFilterDto dto = new StatisticsFilterDto();
        Domain taxon = (Domain) doGet(String.format(GET_TAXON_URL, TAXON_MATHEMATICS_DOMAIN.id), Taxon.class);
        dto.setTaxons(Lists.newArrayList(taxon));
        NewStatisticsResult result = doPost(SEARCH_STATISTICS, dto, NewStatisticsResult.class);
        assertEquals(result.getFilter().getFrom(), dto.getFrom());
        assertEquals(result.getFilter().getTo(), dto.getTo());
        if (isNotEmpty(dto.getTaxons())) {
            assertEquals(result.getFilter().getTaxons(), dto.getTaxons());
        }
        if (dto.getUsers() != null) {
            assertEquals(result.getFilter().getUsers().get(0).getUsername(), dto.getUsers().get(0).getUsername());
        } else {
            assertEquals(result.getFilter().getUsers(), dto.getUsers());
        }
        assertTrue(isNotEmpty(result.getRows()));
        assertTrue(result.getSum() != null);
    }

    @Test
    public void admin_user_search_doesnt_fail() throws Exception {
        login(USER_ADMIN);
        StatisticsFilterDto dto = new StatisticsFilterDto();
        dto.setUsers(Lists.newArrayList(getUser(USER_MODERATOR)));
        NewStatisticsResult result = doPost(SEARCH_STATISTICS, dto, NewStatisticsResult.class);
        assertEquals(result.getFilter().getFrom(), dto.getFrom());
        assertEquals(result.getFilter().getTo(), dto.getTo());
        if (isNotEmpty(dto.getTaxons())) {
            assertEquals(result.getFilter().getTaxons(), dto.getTaxons());
        }
        if (dto.getUsers() != null) {
            assertEquals(result.getFilter().getUsers().get(0).getUsername(), dto.getUsers().get(0).getUsername());
        } else {
            assertEquals(result.getFilter().getUsers(), dto.getUsers());
        }
        assertTrue(isNotEmpty(result.getRows()));
        assertTrue(result.getSum() != null);
    }

    @Test
    public void admin_full_search_results_in_bad_request() throws Exception {
        login(USER_ADMIN);
        StatisticsFilterDto dto = new StatisticsFilterDto();
        dto.setFrom(FROM);
        dto.setTo(TO);
        Domain taxon = (Domain) doGet(String.format(GET_TAXON_URL, TAXON_MATHEMATICS_DOMAIN.id), Taxon.class);
        dto.setTaxons(Lists.newArrayList(taxon));
        dto.setUsers(Lists.newArrayList(getUser(USER_MODERATOR)));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), doPost(SEARCH_STATISTICS, dto).getStatus());
    }

    @Test
    public void admin_can_generate_excel_97_user() throws Exception {
        generateAndDownload(FileFormat.xls, "xls", true);
    }

    @Test
    public void admin_can_generate_excel_97_taxon() throws Exception {
        generateAndDownload(FileFormat.xls, "xls", false);
    }

    @Test
    public void admin_can_generate_excel_2003plus_user() throws Exception {
        generateAndDownload(FileFormat.xlsx, "xlsx", true);
    }

    @Test
    public void admin_can_generate_excel_2003plus_taxon() throws Exception {
        generateAndDownload(FileFormat.xlsx, "xlsx", false);
    }

    @Test
    public void admin_can_generate_csv_user() throws Exception {
        generateAndDownload(FileFormat.csv, "csv", true);
    }

    @Test
    public void admin_can_generate_csv_taxon() throws Exception {
        generateAndDownload(FileFormat.csv, "csv", false);
    }

    private void generateAndDownload(FileFormat format, String filename, boolean userSearch) throws IOException {
        login(USER_ADMIN);
        StatisticsFilterDto dto = new StatisticsFilterDto();
        dto.setFrom(FROM);
        dto.setTo(TO);
        if (userSearch) {
            dto.setUsers(Lists.newArrayList(getUser(USER_MODERATOR)));
        } else {
            Domain taxon = (Domain) doGet(String.format(GET_TAXON_URL, TAXON_MATHEMATICS_DOMAIN.id), Taxon.class);
            dto.setTaxons(Lists.newArrayList(taxon));
        }
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