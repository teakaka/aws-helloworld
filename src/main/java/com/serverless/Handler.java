package com.serverless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import data.Customer;
import data.CustomerUtil;

/*
 * AWS Lambda Request handler class for hello world and Customer add/query/delete REST API
 */
public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = Logger.getLogger(Handler.class);

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		BasicConfigurator.configure();
		LOG.info("received: " + input);

		Response responseBody = new Response("Hello, go serverless!", input);
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Powered-By", "AWS Lambda & Serverless");
		headers.put("Content-Type", "application/json");
		return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(responseBody).setHeaders(headers).build();
	}

	public ApiGatewayResponse getCustomer(Map<String, Object> input, Context context) {
		BasicConfigurator.configure();
		LOG.info("received: " + input);

		Map<String, String> headers = new HashMap<>();
		headers.put("X-Powered-By", "AWS Lambda & Serverless");
		headers.put("Content-Type", "application/json");

		Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
		if (pathParameters == null) {
			LOG.info("path parameter is empty.");
			return ApiGatewayResponse.builder().setStatusCode(500)
					.setObjectBody(new Response("Please provide the customer name for query.", input))
					.setHeaders(headers).build();
		}
		String name = pathParameters.get("name");

		Customer customer = null;
		if (name == null || name.isEmpty()) {
			return ApiGatewayResponse.builder().setStatusCode(500)
					.setObjectBody(new Response("Please provide the customer name for query.", input))
					.setHeaders(headers).build();
		} else if (name != null) {
			customer = CustomerUtil.findCustomerByName(name, context);
		}

		int statusCode = 200;
		String message = null;
		CustomerResponse successResponse = null;
		if (customer == null) {
			statusCode = 404;
			message = "No result found";
		} else {
			message = "Found customer";
			List<Customer> result = new ArrayList<Customer>();
			result.add(customer);
			successResponse = new CustomerResponse(message, input, result);
		}
				
		return ApiGatewayResponse.builder().setStatusCode(statusCode)
				.setObjectBody(customer == null ? new Response(message, input) : successResponse).setHeaders(headers).build();
	}

	public ApiGatewayResponse addCustomer(Map<String, Object> input, Context context) {
		BasicConfigurator.configure();
		LOG.info("received: " + input);

		Map<String, String> headers = new HashMap<>();
		headers.put("X-Powered-By", "AWS Lambda & Serverless");
		headers.put("Content-Type", "application/json");

		if(input.get("body") == null) {
			return ApiGatewayResponse.builder().setStatusCode(500)
					.setObjectBody("Please provide the customer name for creation.").setHeaders(headers).build();
		}
		
		String name = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			LOG.info("body string=" + (String) input.get("body"));
			JsonNode body = mapper.readTree((String) input.get("body"));
			name = body.get("name").asText();
		} catch (Exception e) {
			LOG.error(e, e);
			Response responseBody = new Response("Failure adding customer", input);
			return ApiGatewayResponse.builder().setStatusCode(500).setObjectBody(responseBody).setHeaders(headers)
					.build();
		}

		if (name == null) {
			LOG.info("No name provided.");
			return ApiGatewayResponse.builder().setStatusCode(500)
					.setObjectBody("Please provide the customer name for creation.").setHeaders(headers).build();
		}

		Customer customer = CustomerUtil.addCustomer(name, context);

		String message = (customer == null ? "Failed adding customer" : "Added customer successfully");
		List<Customer> result = new ArrayList<Customer>();
		result.add(customer);
		return ApiGatewayResponse.builder().setStatusCode(200)
				.setObjectBody(new CustomerResponse(message, input, result)).setHeaders(headers).build();
	}

	public ApiGatewayResponse deleteCustomer(Map<String, Object> input, Context context) {
		BasicConfigurator.configure();
		LOG.info("received: " + input);

		Map<String, String> headers = new HashMap<>();
		headers.put("X-Powered-By", "AWS Lambda & Serverless");
		headers.put("Content-Type", "application/json");

		Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
		String name = pathParameters.get("name");

		if (name == null || name.isEmpty()) {
			LOG.info("No name provided.");
			Response responseBody = new Response("Please provide the customer name for query", input);
			return ApiGatewayResponse.builder().setStatusCode(500).setObjectBody(responseBody).setHeaders(headers)
					.build();
		}

		int statusCode = 200;

		String message = CustomerUtil.deleteCustomer(name, context);
		if (!message.equals("SUCCESS")) {
			statusCode = 500;
		}
		
		return ApiGatewayResponse.builder()
				.setStatusCode(statusCode)
				.setObjectBody(new Response(message, input))
				.setHeaders(headers)
				.build();
	}

}
