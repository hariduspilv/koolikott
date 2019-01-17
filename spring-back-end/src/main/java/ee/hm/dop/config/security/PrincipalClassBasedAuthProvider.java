package ee.hm.dop.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class PrincipalClassBasedAuthProvider<T> extends PreAuthenticatedAuthenticationProvider {

  private Class<T> principalClass;

  public PrincipalClassBasedAuthProvider(Class<T> principalClass, AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> uds) {
    this.principalClass = principalClass;
    setPreAuthenticatedUserDetailsService(uds);
  }

  @Override
  public Authentication authenticate(Authentication authentication) {
    if (authentication.getPrincipal().getClass().isAssignableFrom(principalClass)) {
      return super.authenticate(authentication);
    }
    return null;
  }
}