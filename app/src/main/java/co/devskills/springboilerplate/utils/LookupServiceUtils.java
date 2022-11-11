package co.devskills.springboilerplate.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import co.devskills.springbootboilerplate.constants.ApplicationConstants;
import co.devskills.springbootboilerplate.entity.PersonalDetailsCache;

public interface LookupServiceUtils {

	static boolean isExpiredResponse(Optional<PersonalDetailsCache> personalDetailsCache) throws ParseException {
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

	static int findMaxAge(String maxAge) {
		List<String> data = Arrays.asList(maxAge.split(",")).stream().filter(s -> s.contains("max-age"))
				.collect(Collectors.toList());
		if (data.size() > 0) {
			return Integer.parseUnsignedInt(data.get(0).split("=")[1]);
		}
		return 0;
	}

	static Map<String, Object> generateAPIRequestHeader(String cacheHeader) {
		Map<String, Object> headers = new HashMap<>();
		headers.put("cache-control", cacheHeader);
		return headers;
	}

}
