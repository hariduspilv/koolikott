package ee.hm.dop.rest.errorhandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    //@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    //@ExceptionHandler(Exception.class)
    //public ResponseEntity<Object> badRequest(Exception exception) {
    //        log.error("Technical problem");
    //}
}