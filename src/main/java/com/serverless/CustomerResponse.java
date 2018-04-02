package com.serverless;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import data.Customer;

public class CustomerResponse extends Response {
	private final List<Customer> customers;
	
	public CustomerResponse() {
		super("", new HashMap<String, Object>());
		customers = new ArrayList<>();
	}
	
	public CustomerResponse(String message, Map<String, Object> input, List<Customer> customers) {
		super(message, input);
		this.customers = customers;
	}
	
	public List<Customer> getCustomers() {
		return this.customers;
	}
}
