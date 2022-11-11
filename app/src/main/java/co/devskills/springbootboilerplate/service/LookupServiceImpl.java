package co.devskills.springbootboilerplate.service;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.devskills.springboilerplate.utils.LookupServiceUtils;
import co.devskills.springbootboilerplate.entity.PersonalDetailsCache;
import co.devskills.springbootboilerplate.exception.ApplicationException;
import co.devskills.springbootboilerplate.models.CreditData;
import co.devskills.springbootboilerplate.models.DebtDetails;
import co.devskills.springbootboilerplate.models.IncomeDetails;
import co.devskills.springbootboilerplate.models.PersonalDetails;
import co.devskills.springbootboilerplate.repository.PersonalDetailsCacheRepository;
import feign.FeignException;

@Service
public class LookupServiceImpl implements LookupService {

	public static final Logger logger = LoggerFactory.getLogger(LookupServiceImpl.class);

	@Autowired
	private CreditDataAPIClient creditDataAPIClient;

	@Autowired
	private PersonalDetailsCacheRepository personalDetailsCacheRepository;

	@Override
	public CreditData getCreditData(String cacheHeader, String ssn) throws ApplicationException {
		try {
			IncomeDetails incomeDetails = creditDataAPIClient.getIncomeDetails(ssn);
			DebtDetails debtDetails = creditDataAPIClient.getDebtDetails(ssn);
			CreditData creditData = new CreditData();
			creditData.setBalance_of_debt(debtDetails.getBalance_of_debt());
			creditData.setComplaints(debtDetails.isComplaints());
			creditData.setAssessed_income(incomeDetails.getAssessed_income());
			PersonalDetails personalDetails = null;
			Map<String, Object> headers = LookupServiceUtils.generateAPIRequestHeader(cacheHeader);
			if (cacheHeader != null && cacheHeader.contains("max-age")) {
				personalDetails = getPersonalDetailsFromApi(ssn, headers);
			} else if (cacheHeader != null && cacheHeader.contains("no-store")) {
				personalDetails = getPersonalDetailsFromApi(ssn, headers);
			} else {
				personalDetails = fetchFromAPIOrCache(ssn, cacheHeader);
			}
			if (personalDetails != null) {
				creditData.setFirst_name(personalDetails.getFirst_name());
				creditData.setLast_name(personalDetails.getLast_name());
				creditData.setAddress(personalDetails.getAddress());
			}
			return creditData;
		} catch (

		FeignException e) {
			logger.error("Exception occurred in fetching the API response:" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Exception occurred in fetching the API response:" + e.getMessage());
			e.printStackTrace();
			throw new ApplicationException(500, e.getMessage());
		}
	}

	private PersonalDetails getPersonalDetailsFromApi(String ssn, Map<String, Object> headers) {
		ResponseEntity<PersonalDetails> personalDetailsResponse = creditDataAPIClient.getPersonalDetails(headers, ssn);
		return (personalDetailsResponse != null) ? personalDetailsResponse.getBody() : null;
	}

	private PersonalDetails fetchFromAPIOrCache(String ssn, String cacheHeader)
			throws JsonProcessingException, JsonMappingException, ParseException {
		ResponseEntity<PersonalDetails> personalDetailsResponse = null;
		Optional<PersonalDetailsCache> personalDetailsCache = personalDetailsCacheRepository.findById(ssn);
		ObjectMapper mapper = new ObjectMapper();
		boolean isExpired = LookupServiceUtils.isExpiredResponse(personalDetailsCache);
		if (!personalDetailsCache.isPresent() || isExpired
				|| personalDetailsCache.get().getCacheControlResponseHeader().contains("no-store")) {
			Map<String, Object> headers = LookupServiceUtils.generateAPIRequestHeader(cacheHeader);
			personalDetailsResponse = creditDataAPIClient.getPersonalDetails(headers, ssn);
			PersonalDetailsCache cachedElement = new PersonalDetailsCache(ssn,
					mapper.writeValueAsString(personalDetailsResponse.getBody()),
					personalDetailsResponse.getHeaders().get("date").get(0),
					LookupServiceUtils.findMaxAge(personalDetailsResponse.getHeaders().getCacheControl()),
					personalDetailsResponse.getHeaders().getCacheControl());
			personalDetailsCache = Optional.of(personalDetailsCacheRepository.save(cachedElement));
		}
		PersonalDetails personalDetails = mapper.readValue(personalDetailsCache.get().getApiResponse(),
				PersonalDetails.class);
		return personalDetails;
	}

}
