package co.devskills.springbootboilerplate.advice;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import co.devskills.springbootboilerplate.exception.ApplicationException;
import co.devskills.springbootboilerplate.exception.ErrorDetails;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<ErrorDetails> applicationExceptionHandler(ApplicationException applicationException,
			WebRequest webRequest) {
		ErrorDetails error = new ErrorDetails(applicationException.getErrorCode(),
				applicationException.getErrorMessage(), new Date(), webRequest.getDescription(false));
		return new ResponseEntity<ErrorDetails>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
