package co.devskills.springbootboilerplate.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import co.devskills.springbootboilerplate.exception.ApplicationException;
import co.devskills.springbootboilerplate.exception.FeignNotFoundException;
import co.devskills.springbootboilerplate.models.CreditData;
import co.devskills.springbootboilerplate.service.LookupService;
import feign.FeignException;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class LookupControllerTests {

	@InjectMocks
	private LookupController controller;

	@Mock
	private LookupService service;

	@Test
	public void testGetCreditDataValidSSN() throws ApplicationException {
		CreditData testAggregation = getMockCreditData();
		String cacheHeader = null;
		String ssn = "424-11-9237";
		when(service.getCreditData(cacheHeader, ssn)).thenReturn(testAggregation);
		ResponseEntity<CreditData> responseEntity = controller.getCreditData(cacheHeader, ssn);
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
		CreditData creditDataResponse = responseEntity.getBody();
		assertEquals(testAggregation.getAddress(), creditDataResponse.getAddress());
		assertEquals(testAggregation.getFirst_name(), creditDataResponse.getFirst_name());
		assertEquals(testAggregation.getLast_name(), creditDataResponse.getLast_name());
		assertEquals(testAggregation.getBalance_of_debt(), creditDataResponse.getBalance_of_debt());
		assertEquals(testAggregation.isComplaints(), creditDataResponse.isComplaints());
	}

	@Test
	public void testGetCreditDataInValidSSN() throws ApplicationException {
		String cacheHeader = null;
		String ssn = "000-00-0000";
		when(service.getCreditData(cacheHeader, ssn)).thenThrow(new FeignNotFoundException(404,"personal data not found"));
		FeignException thrown = assertThrows(FeignException.class , () -> {
			controller.getCreditData(cacheHeader, ssn);
		});
		assertThat(thrown.getMessage()).isEqualTo("personal data not found");
	}

	private CreditData getMockCreditData() {
		CreditData creditData = new CreditData();
		creditData.setAddress("7823 Westend Terrace");
		creditData.setFirst_name("Emma");
		creditData.setLast_name("Gautrey");
		creditData.setAssessed_income(60668);
		creditData.setBalance_of_debt(11585);
		creditData.setComplaints(true);
		return creditData;
	}

}
