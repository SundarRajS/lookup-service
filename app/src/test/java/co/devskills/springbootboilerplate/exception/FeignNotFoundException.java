package co.devskills.springbootboilerplate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import feign.FeignException;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FeignNotFoundException extends FeignException {

	private static final long serialVersionUID = -7970565508058799843L;

	public FeignNotFoundException(int status, String message) {
		super(status, message);
		// TODO Auto-generated constructor stub
	}

}