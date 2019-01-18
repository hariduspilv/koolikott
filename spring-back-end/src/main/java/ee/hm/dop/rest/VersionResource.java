package ee.hm.dop.rest;

import ee.hm.dop.model.enums.RoleString;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/version")
public class VersionResource {

    @GetMapping
    public String getVersion() {
        return "1.28.1";
    }

    @GetMapping("user")
    @Secured(RoleString.USER)
    public String getVersi2on() {
        return "1.28.1";
    }
}
