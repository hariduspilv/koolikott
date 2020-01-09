package ee.hm.dop.rest.administration;

import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.files.UploadedFileService;
import ee.hm.dop.service.reviewmanagement.dto.FileFormat;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsResult;
import ee.hm.dop.service.statistics.NewStatisticsCsvExporter;
import ee.hm.dop.service.statistics.NewStatisticsExcelExporter;
import ee.hm.dop.service.statistics.NewStatisticsService;
import ee.hm.dop.utils.io.CsvUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;


@Slf4j
@RestController
@RequestMapping("admin/statistics")
public class StatisticsAdminResource extends BaseResource {

    private static final String TEMP_FOLDER = CsvUtil.TEMP_FOLDER;

    @Inject
    private NewStatisticsService statisticsService;
    @Inject
    private NewStatisticsExcelExporter statisticsExcelExporter;
    @Inject
    private NewStatisticsCsvExporter statisticsCsvExporter;
    @Inject
    private UploadedFileService uploadedFileService;

    @PostMapping
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public NewStatisticsResult newsearch(@RequestBody StatisticsFilterDto searchFilter) {
        if (searchFilter == null || !searchFilter.isValidSearch()) {
            throw badRequest("Search parameters invalid");
        }
        return statisticsService.statistics(searchFilter, getLoggedInUser());
    }

    @GetMapping(value = "export/download/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public String download(@PathVariable("filename") String filename) throws IOException {
        return buildResponse(filename);
    }

    @PostMapping(value = "export", produces = MediaType.TEXT_PLAIN_VALUE)
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public String searchExport(@RequestBody StatisticsFilterDto filter) {
        if (filter == null || !filter.isValidExportRequest()) {
            throw badRequest("Search parameters invalid");
        }

        String filename = CsvUtil.getUniqueFileName(filter.getFormat());
        NewStatisticsResult statistics = statisticsService.statistics(filter, getLoggedInUser());
        if (filter.getFormat() == FileFormat.xlsx) {
            statisticsExcelExporter.generateXlsx(filename, statistics);
        } else if (filter.getFormat() == FileFormat.xls) {
            statisticsExcelExporter.generateXls(filename, statistics);
        } else if (filter.getFormat() == FileFormat.csv) {
            statisticsCsvExporter.generate(filename, statistics);
        } else {
            throw new IllegalStateException("FileFormat is in unknown state: " + filter.getFormat());
        }
        return new File(filename).getName();
    }

    private String buildResponse(String filename) throws IOException {
        byte[] fileBytes = Files.readAllBytes(Paths.get(TEMP_FOLDER + "/" + filename));
        return Base64.getEncoder().encodeToString(fileBytes);
    }
}
