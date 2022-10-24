package co.devskills.springbootboilerplate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.devskills.springbootboilerplate.exception.ApplicationException;
import co.devskills.springbootboilerplate.models.CreditData;
import co.devskills.springbootboilerplate.models.DebtDetails;
import co.devskills.springbootboilerplate.models.IncomeDetails;
import co.devskills.springbootboilerplate.models.PersonalDetails;

@Service
public class LookupServiceImpl implements LookupService {

	public static final Logger logger = LoggerFactory.getLogger(LookupServiceImpl.class);

	@Autowired
	private CreditDataAPIClient creditDataAPIClient;

	@Override
	public CreditData getCreditData(String ssn) throws ApplicationException {
		try {
			PersonalDetails personalDetails = creditDataAPIClient.getPersonalDetails(ssn);
			IncomeDetails incomeDetails = creditDataAPIClient.getIncomeDetails(ssn);
			DebtDetails debtDetails = creditDataAPIClient.getDebtDetails(ssn);
			CreditData creditData = new CreditData();
			creditData.setBalance_of_debt(debtDetails.getBalance_of_debt());
			creditData.setComplaints(debtDetails.isComplaints());
			creditData.setAssessed_income(incomeDetails.getAssessed_income());
			creditData.setFirst_name(personalDetails.getFirst_name());
			creditData.setLast_name(personalDetails.getLast_name());
			return creditData;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception occurred in fetching the API response:" + e.getMessage());
			throw new ApplicationException(500, e.getMessage());
		}
	}

}
