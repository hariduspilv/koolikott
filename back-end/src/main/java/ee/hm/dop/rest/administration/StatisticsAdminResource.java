package ee.hm.dop.rest.administration;

import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.statistics.StatisticsCsvExporter;
import ee.hm.dop.service.statistics.StatisticsExcelExporter;
import ee.hm.dop.service.reviewmanagement.StatisticsService;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsResult;
import ee.hm.dop.utils.io.CsvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;


@Path("admin/statistics/")
public class StatisticsAdminResource extends BaseResource {

    public static final String DOWNLOAD_PATH = "/export/files";
    public static final String FILENAME = "?filename=";
    private static final String TEMP_FOLDER = CsvUtil.TEMP_FOLDER;
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Inject
    private StatisticsService statisticsService;
    @Inject
    private StatisticsExcelExporter statisticsExcelExporter;
    @Inject
    private StatisticsCsvExporter statisticsCsvExporter;

    @POST
    @RolesAllowed({RoleString.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public StatisticsResult search(StatisticsFilterDto searchFilter) {
        return statisticsService.statistics(nvl(searchFilter), getLoggedInUser());
    }

//    @GET
//    @Path("export/download/{filename}")
//    @RolesAllowed({RoleString.ADMIN})
//    public void download(HttpServletResponse response, String filename) {
//        try {
//            downloadInner(response, filename);
//        } catch (Exception e){
//
//        }
//    }

    @POST
    @Path("export")
    @RolesAllowed({RoleString.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String searchExport(StatisticsFilterDto searchFilter) {
        StatisticsFilterDto filter = nvl(searchFilter);
        if (filter.getFormat() == null) {
            throw badRequest("format is needed");
        }

        String fileName = CsvUtil.getUniqueFileName(filter.getFormat());
        StatisticsResult statistics = statisticsService.statistics(filter, getLoggedInUser());
        if (filter.getFormat().isExcel()) {
            statisticsExcelExporter.generate(fileName, statistics);
        } else {
            statisticsCsvExporter.generate(fileName, statistics);
        }

        return getFileUrl(fileName);
    }

    public String getFileUrl(String fileName) {
        return DOWNLOAD_PATH + FILENAME + new File(fileName).getName();
    }

    private StatisticsFilterDto nvl(StatisticsFilterDto searchFilter) {
        return searchFilter != null ? searchFilter : new StatisticsFilterDto();
    }

    private void downloadInner(HttpServletResponse response, String filename) throws IOException {
        LOG.debug("Download, filename: {}", filename);

        File file = new File(TEMP_FOLDER + filename);

        // set content attributes for the response
        response.setContentType("text/csv");
        response.setContentLength((int) file.length());

        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", "export.csv");
        response.setHeader(headerKey, headerValue);

        // get output stream of the response
        OutputStream outStream = response.getOutputStream();

        int BUFFER_SIZE = 1024;

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        InputStream is = new FileInputStream(file);

        // write bytes read from the input stream into the output stream
        while ((bytesRead = is.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        is.close();
        outStream.close();
        file.delete();
    }
}
