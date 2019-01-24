package ee.hm.dop.config.security;

import ee.hm.dop.model.AuthenticatedUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DopPrincipal {

    private AuthenticatedUser authenticatedUser;
}
