package co.devskills.springbootboilerplate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import co.devskills.springbootboilerplate.exception.ApplicationException;
import co.devskills.springbootboilerplate.models.CreditData;
import co.devskills.springbootboilerplate.service.LookupService;

@RestController
public class LookupController {

	@Autowired
	private LookupService lookupService;

	@GetMapping(value = "/credit-data/{ssn}", produces = "application/json")
	public ResponseEntity<CreditData> getCreditData(
			@RequestHeader(value = "cache-control", required = false) String cacheValue,
			@PathVariable("ssn") String ssn) throws ApplicationException {
		return new ResponseEntity<CreditData>(lookupService.getCreditData(cacheValue, ssn), HttpStatus.OK);
	}

}
