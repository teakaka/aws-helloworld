package com.serverless;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import data.ExternalUser;

public class QueryExternalHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
	private static final Logger LOG = Logger.getLogger(QueryExternalHandler.class);

	private static final String EXTERNAL_URL = "https://jsonplaceholder.typicode.com/posts";

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		BasicConfigurator.configure();
		LOG.info("received: " + input);

		Map<String, String> headers = new HashMap<>();
		headers.put("X-Powered-By", "AWS Lambda & Serverless");
		headers.put("Content-Type", "application/json");

		Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
		if (pathParameters == null) {
			LOG.info("path parameter is empty.");
			return handleException(404, "No Id found", input);
		}
		String id = pathParameters.get("id");

		if (id == null) {
			return handleException(404, "No Id found", input);
		}

		try {
			URL url = new URL(EXTERNAL_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(conn.getInputStream());
			List<ExternalUser> users = mapper.readValue(new BufferedReader(new InputStreamReader(conn.getInputStream())), new TypeReference<List<ExternalUser>>(){});
			
			LOG.info("********Parsed Json successfully " + users.size());
			conn.disconnect();
			
			return buildResponse(200, queryById(Integer.valueOf(id).intValue(), users), input);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return handleException(404, "External resource error", input);
		} catch (IOException e) {
			e.printStackTrace();
			return handleException(404, "External resource error", input);
		} catch (NumberFormatException e) {
			return handleException(404, "Id is not integer", input);
		}
		
	}

	private List<ExternalUser> queryById(int id, List<ExternalUser> users) {
		List<ExternalUser> result = new ArrayList<>();
		
		if (users == null) {
			return result;
		}
		
		for (ExternalUser u : users) {
			if (u.getId().intValue() == id) {
				result.add(u);
			}
		}
		return result;
	}

	private ApiGatewayResponse buildResponse(int statusCode, List<ExternalUser> users, Map<String, Object> input) {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Powered-By", "AWS Lambda & Serverless");
		headers.put("Content-Type", "application/json");

		return ApiGatewayResponse.builder().setStatusCode(statusCode)
		.setObjectBody(new Response("Success", input)).setHeaders(headers).build();
	}
	
	private ApiGatewayResponse handleException(int statusCode, String message, Map<String, Object> input) {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Powered-By", "AWS Lambda & Serverless");
		headers.put("Content-Type", "application/json");

		return ApiGatewayResponse.builder().setStatusCode(statusCode).setObjectBody(new Response(message, input))
				.setHeaders(headers).build();
	}
}
