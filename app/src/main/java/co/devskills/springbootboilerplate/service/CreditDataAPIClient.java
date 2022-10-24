package co.devskills.springbootboilerplate.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import co.devskills.springbootboilerplate.constants.ApplicationConstants;
import co.devskills.springbootboilerplate.models.DebtDetails;
import co.devskills.springbootboilerplate.models.IncomeDetails;
import co.devskills.springbootboilerplate.models.PersonalDetails;

@FeignClient(name = "credit-data-api", url = ApplicationConstants.BASE_URL)
public interface CreditDataAPIClient {

	@GetMapping("/personal-details/{ssn}")
	public PersonalDetails getPersonalDetails(@PathVariable("ssn") String ssn);

	@GetMapping("/assessed-income/{ssn}")
	public IncomeDetails getIncomeDetails(@PathVariable("ssn") String ssn);

	@GetMapping("/debt/{ssn}")
	public DebtDetails getDebtDetails(@PathVariable("ssn") String ssn);

}
