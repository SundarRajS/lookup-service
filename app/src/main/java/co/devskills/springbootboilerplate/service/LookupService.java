package co.devskills.springbootboilerplate.service;

import co.devskills.springbootboilerplate.exception.ApplicationException;
import co.devskills.springbootboilerplate.models.CreditData;

public interface LookupService {

	CreditData getCreditData(String ssn) throws ApplicationException;

}
