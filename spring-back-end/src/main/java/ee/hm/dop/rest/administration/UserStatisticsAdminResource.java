package ee.hm.dop.rest.administration;

import ee.hm.dop.model.User;
import ee.hm.dop.model.administration.PageableQueryUsers;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.UserStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.File;

@Slf4j
@RestController
@RequestMapping("admin/userStatistics")
public class UserStatisticsAdminResource extends BaseResource {

    @Inject
    private UserStatisticsService userStatisticsService;

    @PostMapping(value = "export", produces = MediaType.TEXT_PLAIN_VALUE)
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public String searchExport(@RequestParam(value = "fileType") String fileType,
                               @RequestParam(value = "itemSortedBy", required = false) String itemSortedBy,
                               @RequestParam(value = "query", required = false) String query,
                               @RequestParam(value = "role", required = false) String role,
                               @RequestParam(value = "userRole", required = false) String userRole,
                               @RequestParam(value = "userEducationalContext", required = false) String userEducationalContext,
                               @RequestParam(value = "withEmail", required = false) boolean withEmail,
                               @RequestParam(value = "withoutEmail", required = false) boolean withoutEmail,
                               @RequestParam(value = "language", defaultValue = "est") String languageCode) {
        PageableQueryUsers pageableQuery = new PageableQueryUsers(1, itemSortedBy, query, role, userRole, userEducationalContext, withEmail, withoutEmail, languageCode, 50000);
        User loggedInUser = getLoggedInUser();
        String filename = userStatisticsService.generateFile(languageCode, fileType, pageableQuery, loggedInUser);
        return new File(filename).getName();
    }
}
