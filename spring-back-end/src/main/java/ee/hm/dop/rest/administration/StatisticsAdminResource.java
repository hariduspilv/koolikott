package ee.hm.dop.rest.administration;

import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.reviewmanagement.dto.FileFormat;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsResult;
import ee.hm.dop.service.statistics.NewStatisticsCsvExporter;
import ee.hm.dop.service.statistics.NewStatisticsExcelExporter;
import ee.hm.dop.service.statistics.NewStatisticsService;
import ee.hm.dop.utils.DOPFileUtils;
import ee.hm.dop.utils.DopConstants;
import ee.hm.dop.utils.io.CsvUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;


@Slf4j
@RestController
@RequestMapping("admin/statistics/")
public class StatisticsAdminResource extends BaseResource {

    private static final String TEMP_FOLDER = CsvUtil.TEMP_FOLDER;

    @Inject
    private NewStatisticsService statisticsService;
    @Inject
    private NewStatisticsExcelExporter statisticsExcelExporter;
    @Inject
    private NewStatisticsCsvExporter statisticsCsvExporter;

    @PostMapping
    @Secured({RoleString.ADMIN})
    public NewStatisticsResult newsearch(@RequestBody StatisticsFilterDto searchFilter) {
        if (searchFilter == null || !searchFilter.isValidSearch()) {
            throw badRequest("Search parameters invalid");
        }
        return statisticsService.statistics(searchFilter, getLoggedInUser());
    }

    @GetMapping(value = "export/download/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> download(@PathVariable("filename") String filename) {
        String[] split = filename.split("\\.");
        return buildResponse(filename, FileFormat.valueOf(split[1]));
    }

    @PostMapping("export")
    @Secured({RoleString.ADMIN})
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

    private ResponseEntity<InputStreamResource> buildResponse(String filename, FileFormat format) {
        try {
            String mediaType = DOPFileUtils.probeForMediaType(filename);
            String fileName = "statistika_aruanne." + format.name();
            File file = FileUtils.getFile(TEMP_FOLDER + "/" + filename);

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.add(DopConstants.CONTENT_DISPOSITION, "Attachment; filename*=\"UTF-8''" + DOPFileUtils.encode(fileName) + "\"; filename=\"" + fileName + "\"");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(mediaType))
                    .body(resource);
        } catch (Exception e) {
            log.info("Downloading file failed: {}", e.getMessage(), e);
            return null;
        }
    }
}
