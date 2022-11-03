package co.devskills.springbootboilerplate.service;

import org.springframework.http.ResponseEntity;

import co.devskills.springbootboilerplate.exception.ApplicationException;
import co.devskills.springbootboilerplate.models.CreditData;

public interface LookupService {

	CreditData getCreditData(String cacheHeader, String ssn) throws ApplicationException;

}
