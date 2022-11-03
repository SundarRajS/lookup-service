package co.devskills.springbootboilerplate.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PERSONAL_DETAILS_CACHE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDetailsCache {

	@Id
	private String ssn;

	@Column
	private String apiResponse;

	@Column
	private String lastFetchedDate;
	
	@Column
	private int maxAge;
	
	@Column
	private String cacheControlResponseHeader;
	
	
}
