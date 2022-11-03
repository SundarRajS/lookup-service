package co.devskills.springbootboilerplate.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.devskills.springbootboilerplate.constants.ApplicationConstants;
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
			Map<String, Object> headers = this.generateAPIRequestHeader(cacheHeader);
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
			throw new ApplicationException(500, e.getMessage());
		}
	}

	private PersonalDetails getPersonalDetailsFromApi(String ssn, Map<String, Object> headers) {
		ResponseEntity<PersonalDetails> personalDetailsResponse;
		personalDetailsResponse = creditDataAPIClient.getPersonalDetails(headers, ssn);
		return personalDetailsResponse.getBody();
	}

	private PersonalDetails fetchFromAPIOrCache(String ssn, String cacheHeader)
			throws JsonProcessingException, JsonMappingException, ParseException {
		ResponseEntity<PersonalDetails> personalDetailsResponse = null;
		Optional<PersonalDetailsCache> personalDetailsCache = personalDetailsCacheRepository.findById(ssn);
		ObjectMapper mapper = new ObjectMapper();
		boolean isExpired = isExpiredResponse(personalDetailsCache);
		if (!personalDetailsCache.isPresent() || isExpired
				|| personalDetailsCache.get().getCacheControlResponseHeader().contains("no-store")) {
			Map<String, Object> headers = this.generateAPIRequestHeader(cacheHeader);
			personalDetailsResponse = creditDataAPIClient.getPersonalDetails(headers, ssn);
			PersonalDetailsCache cachedElement = new PersonalDetailsCache(ssn,
					mapper.writeValueAsString(personalDetailsResponse.getBody()),
					personalDetailsResponse.getHeaders().get("date").get(0),
					findMaxAge(personalDetailsResponse.getHeaders().getCacheControl()),
					personalDetailsResponse.getHeaders().getCacheControl());
			personalDetailsCache = Optional.of(personalDetailsCacheRepository.save(cachedElement));
		}
		PersonalDetails personalDetails = mapper.readValue(personalDetailsCache.get().getApiResponse(),
				PersonalDetails.class);
		return personalDetails;
	}

	private boolean isExpiredResponse(Optional<PersonalDetailsCache> personalDetailsCache) throws ParseException {
		boolean isExpiredResponse = false;
		SimpleDateFormat obj = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT);
		if (personalDetailsCache.isPresent() && personalDetailsCache.get().getMaxAge() > 0) {
			Date date1 = obj.parse(personalDetailsCache.get().getLastFetchedDate());
			Date date2 = new Date();
			long difference = ((date2.getTime() - date1.getTime()) / 1000) % 60;
			isExpiredResponse = difference > personalDetailsCache.get().getMaxAge();
		}
		return isExpiredResponse;
	}

	private int findMaxAge(String maxAge) {
		List<String> data = Arrays.asList(maxAge.split(",")).stream().filter(s -> s.contains("max-age"))
				.collect(Collectors.toList());
		if (data.size() > 0) {
			return Integer.parseUnsignedInt(data.get(0).split("=")[1]);
		}
		return 0;
	}

	private Map<String, Object> generateAPIRequestHeader(String cacheHeader) {
		Map<String, Object> headers = new HashMap<>();
		headers.put("cache-control", cacheHeader);
		return headers;
	}

}
