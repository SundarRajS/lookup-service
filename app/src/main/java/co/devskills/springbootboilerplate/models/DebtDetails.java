package co.devskills.springbootboilerplate.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebtDetails {

	private int balance_of_debt;

	private boolean complaints;
}
