package ee.hm.dop.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.enums.RoleString;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("role")
public class RoleResource extends BaseResource {
    @GetMapping
    @Secured(RoleString.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Role[] getAll() {
        return Role.values();
    }
}
