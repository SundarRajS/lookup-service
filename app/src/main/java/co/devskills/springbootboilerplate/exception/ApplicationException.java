package co.devskills.springbootboilerplate.exception;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApplicationException extends Exception {

	private static final long serialVersionUID = 1L;

	private int errorCode;

	private String errorMessage;

}
