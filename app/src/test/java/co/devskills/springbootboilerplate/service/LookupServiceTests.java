package co.devskills.springbootboilerplate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.devskills.springboilerplate.utils.LookupServiceUtils;
import co.devskills.springbootboilerplate.entity.PersonalDetailsCache;
import co.devskills.springbootboilerplate.exception.ApplicationException;
import co.devskills.springbootboilerplate.models.CreditData;
import co.devskills.springbootboilerplate.models.DebtDetails;
import co.devskills.springbootboilerplate.models.IncomeDetails;
import co.devskills.springbootboilerplate.models.PersonalDetails;
import co.devskills.springbootboilerplate.repository.PersonalDetailsCacheRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class LookupServiceTests {

	@InjectMocks
	private LookupServiceImpl lookupService;

	@MockBean
	private CreditDataAPIClient creditDataAPIClient;

	@MockBean
	private PersonalDetailsCacheRepository personalDetailsCacheRepository;

	@Test
	@DisplayName("Test the application behaviour when the no-store is passed in the header")
	public void testRequestHeaderCacheNoStore() throws ApplicationException {
		String cacheHeader = "no-store";
		String ssn = "424-11-9237";
		Map<String, Object> headers = LookupServiceUtils.generateAPIRequestHeader(cacheHeader);
		when(creditDataAPIClient.getPersonalDetails(headers, ssn)).thenReturn(getMockPersonalDetailsNoHeader());
		when(creditDataAPIClient.getIncomeDetails(ssn)).thenReturn(getMockAssessedIncome());
		when(creditDataAPIClient.getDebtDetails(ssn)).thenReturn(getMockDebtDetails());
		CreditData expected = getMockCreditData1();
		CreditData creditDataResponse = lookupService.getCreditData(cacheHeader, ssn);
		assertEquals(expected.getAddress(), creditDataResponse.getAddress());
		assertEquals(expected.getFirst_name(), creditDataResponse.getFirst_name());
		assertEquals(expected.getLast_name(), creditDataResponse.getLast_name());
		assertEquals(expected.getBalance_of_debt(), creditDataResponse.getBalance_of_debt());
		assertEquals(expected.isComplaints(), creditDataResponse.isComplaints());
	}

	@Test
	@DisplayName("Test the application behaviour when the max age is passed in the header")
	public void testRequestHeaderMaxAge() throws ApplicationException {
		String cacheHeader = "max-age";
		String ssn = "424-11-9237";
		Map<String, Object> headers = LookupServiceUtils.generateAPIRequestHeader(cacheHeader);
		when(creditDataAPIClient.getPersonalDetails(headers, ssn)).thenReturn(getMockPersonalDetailsNoHeader());
		when(creditDataAPIClient.getIncomeDetails(ssn)).thenReturn(getMockAssessedIncome());
		when(creditDataAPIClient.getDebtDetails(ssn)).thenReturn(getMockDebtDetails());
		CreditData expected = getMockCreditData2();
		CreditData creditDataResponse = lookupService.getCreditData(cacheHeader, ssn);
		assertNotEquals(expected.getAddress(), creditDataResponse.getAddress());
		assertEquals(expected.getFirst_name(), creditDataResponse.getFirst_name());
		assertEquals(expected.getLast_name(), creditDataResponse.getLast_name());
		assertEquals(expected.getBalance_of_debt(), creditDataResponse.getBalance_of_debt());
		assertEquals(expected.isComplaints(), creditDataResponse.isComplaints());
	}

	private CreditData getMockCreditData1() {
		CreditData creditData = new CreditData();
		creditData.setAddress("7823 Westend Terrace");
		creditData.setFirst_name("Emma");
		creditData.setLast_name("Gautrey");
		creditData.setAssessed_income(60668);
		creditData.setBalance_of_debt(11585);
		creditData.setComplaints(true);
		return creditData;
	}

	private CreditData getMockCreditData2() {
		CreditData creditData = new CreditData();
		creditData.setAddress("1234 Westend Terrace");
		creditData.setFirst_name("Emma");
		creditData.setLast_name("Gautrey");
		creditData.setAssessed_income(60668);
		creditData.setBalance_of_debt(11585);
		creditData.setComplaints(true);
		return creditData;
	}

	private IncomeDetails getMockAssessedIncome() {
		IncomeDetails incomeDetails = new IncomeDetails();
		incomeDetails.setAssessed_income(60668);
		return incomeDetails;
	}

	private DebtDetails getMockDebtDetails() {
		DebtDetails debtDetails = new DebtDetails();
		debtDetails.setBalance_of_debt(11585);
		debtDetails.setComplaints(true);
		return debtDetails;
	}

	private ResponseEntity<PersonalDetails> getMockPersonalDetailsNoHeader() {
		PersonalDetails personalDetails = getPersonalDetails();
		return new ResponseEntity<PersonalDetails>(personalDetails, HttpStatus.OK);
	}

	private ResponseEntity<PersonalDetails> getMockPersonalDetailsNoStoreResponseHeader() {
		PersonalDetails personalDetails = getPersonalDetails();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("cache-control", "no-store");
		responseHeaders.set("date", (new Date()).toString());
		ResponseEntity<PersonalDetails> personalDetailsResponse = new ResponseEntity<PersonalDetails>(personalDetails,
				responseHeaders, HttpStatus.OK);
		return personalDetailsResponse;
	}

	private PersonalDetailsCache getPersonalDetailsCache(String ssn) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ResponseEntity<PersonalDetails> personalDetailsResponse = getMockPersonalDetailsNoStoreResponseHeader();
		PersonalDetailsCache personalDetailsCache = new PersonalDetailsCache(ssn,
				mapper.writeValueAsString(personalDetailsResponse.getBody()),
				personalDetailsResponse.getHeaders().get("date").get(0),
				LookupServiceUtils.findMaxAge(personalDetailsResponse.getHeaders().getCacheControl()),
				personalDetailsResponse.getHeaders().getCacheControl());
		return personalDetailsCache;
	}

	private PersonalDetails getPersonalDetails() {
		PersonalDetails personalDetails = new PersonalDetails();
		personalDetails.setAddress("7823 Westend Terrace");
		personalDetails.setFirst_name("Emma");
		personalDetails.setLast_name("Gautrey");
		return personalDetails;
	}

}
