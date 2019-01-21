package ee.hm.dop.rest;

import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.enums.RoleString;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("role")
public class RoleResource extends BaseResource {

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    @Secured({RoleString.ADMIN})
    public Role[] getAll() {
        return Role.values();
    }
}
