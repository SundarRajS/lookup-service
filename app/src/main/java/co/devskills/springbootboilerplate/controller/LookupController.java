package co.devskills.springbootboilerplate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import co.devskills.springbootboilerplate.exception.ApplicationException;
import co.devskills.springbootboilerplate.models.CreditData;
import co.devskills.springbootboilerplate.service.LookupService;

@RestController
public class LookupController {

	@Autowired
	private LookupService lookupService;

	@GetMapping(value = "/credit-data/{ssn}", produces = "application/json")
	public CreditData getCreditData(@PathVariable("ssn") String ssn) throws ApplicationException {
		return lookupService.getCreditData(ssn);
	}

}
