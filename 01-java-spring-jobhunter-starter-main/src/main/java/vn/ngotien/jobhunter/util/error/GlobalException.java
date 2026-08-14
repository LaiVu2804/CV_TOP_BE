package vn.ngotien.jobhunter.util.error;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import vn.ngotien.jobhunter.domain.RestResponse;

@RestControllerAdvice
public class GlobalException {

  @ExceptionHandler(value = { //khối code nếu nhập sai tk,mk đn
      UsernameNotFoundException.class,
      BadCredentialsException.class,
      IdInvalidException.class,
  })

  public ResponseEntity<RestResponse<Object>> handleException(Exception ex) {
    RestResponse<Object> restResponse = new RestResponse<Object>();
    restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
    restResponse.setError(ex.getMessage());
    restResponse.setMessage("IdInvalidException");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
  }

  @ExceptionHandler(value = {NoSuchFieldException.class})
  public ResponseEntity<RestResponse<Object>> handleNotFoundExceptionHandler(
      NoSuchFieldException ex) {
    RestResponse<Object> res = new RestResponse<Object>();
    res.setStatusCode(HttpStatus.NOT_FOUND.value());
    res.setError(ex.getMessage());
    res.setMessage("404 Not Found. URL may not exist...");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }

  public ResponseEntity<RestResponse<Object>> methodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    BindingResult result = ex.getBindingResult();
    List<FieldError> fieldErrors = result.getFieldErrors();

    RestResponse<Object> res = new RestResponse<Object>();
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setError(ex.getBody().getDetail());

    List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage())
        .collect(Collectors.toUnmodifiableList());
    res.setMessage(errors.size() > 1 ? errors : errors.get(0));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }
}
