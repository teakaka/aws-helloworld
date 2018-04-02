package com.serverless;

import java.util.Map;
import java.util.List;
import data.Customer;
import data.ExternalUser;

public class QueryExternalResponse extends Response {
	private final List<ExternalUser> users;
	
	public QueryExternalResponse(String message, Map<String, Object> input, List<ExternalUser> users) {
		super(message, input);
		this.users = users;
	}
	
	public List<ExternalUser> getUsers() {
		return this.users;
	}
}
