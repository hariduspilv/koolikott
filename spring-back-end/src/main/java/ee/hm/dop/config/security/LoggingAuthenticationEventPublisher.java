package ee.hm.dop.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Slf4j
public class LoggingAuthenticationEventPublisher implements AuthenticationEventPublisher {

  @Override
  public void publishAuthenticationSuccess(Authentication authentication) {
  }

  @Override
  public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
    log.error("Authentication failed!", exception);
  }
}