package co.devskills.springbootboilerplate.exception;

import java.util.Date;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.KebabCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(value = KebabCaseStrategy.class)
public class ErrorDetails {

	private int errorCode;

	private String errorMessage;

	private Date timeStamp;

	private String uri;

}
