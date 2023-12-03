package lv.gstg.javademo.transactions.config;

import lv.gstg.javademo.transactions.exceptions.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class WebResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({BadRequestException.class})
    public final ResponseEntity<Object> handleBadRequestException(Exception ex) {
        return response(HttpStatus.NOT_FOUND, ex);
    }

    private ResponseEntity<Object> response(HttpStatus httpStatus, Exception ex) {
        return new ResponseEntity<>(Map.of("message", ex.getMessage()), new HttpHeaders(), httpStatus);
    }

}
