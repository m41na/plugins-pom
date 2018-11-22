package works.graphql.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {

	@Column(name="street_name")
	public String street;
	@Column(name="unit_num")
	public String unit;
	@Column(name="addr_city")
	public String city;
	@Column(name="addr_state")
	public String state;
	@Column(name="zip_code")
	public String zipCode;
}
