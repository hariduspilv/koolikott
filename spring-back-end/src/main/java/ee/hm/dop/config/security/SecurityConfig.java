package ee.hm.dop.config.security;

import ee.hm.dop.rest.filter.dto.DopPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import static java.util.Arrays.asList;

@Configuration
public class SecurityConfig {

  @Bean
  public AuthenticationManager authenticationManager(PrincipalClassBasedAuthProvider<DopPrincipal> jwtAuthProvider) {
    ProviderManager providerManager = new ProviderManager(asList(jwtAuthProvider));
    providerManager.setAuthenticationEventPublisher(new LoggingAuthenticationEventPublisher());
    return providerManager;
  }

  @Bean
  public PrincipalClassBasedAuthProvider<DopPrincipal> jwtAuthProvider(DbUserDetailsService dbUserDetailsService) {
    return new PrincipalClassBasedAuthProvider<>(DopPrincipal.class, dbUserDetailsService);
  }
}