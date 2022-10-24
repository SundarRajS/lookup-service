package co.devskills.springbootboilerplate.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditData {

	private String first_name;

	private String last_name;

	private int assessed_income;

	private int balance_of_debt;

	private boolean complaints;
}
