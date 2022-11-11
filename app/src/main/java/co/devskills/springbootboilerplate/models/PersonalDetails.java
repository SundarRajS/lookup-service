package co.devskills.springbootboilerplate.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDetails {

	private String first_name;

	private String last_name;

	private String address;
}
