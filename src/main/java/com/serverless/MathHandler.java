package com.serverless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class MathHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
	private static final Logger LOG = Logger.getLogger(MathHandler.class);

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		BasicConfigurator.configure();
		LOG.info("received: " + input);
		LOG.info("Context: MemoryLimitInMB" + context.getMemoryLimitInMB() + "; LambdaLogger = "+context.getLogger() 
				+ "; getAWSRequestId = " + context.getAwsRequestId() + "; getIdentity = " + context.getIdentity().toString()
				+ "; getLogStreamName = " + context.getLogStreamName() + "; getRemainingTimeInMillis = " + context.getRemainingTimeInMillis()
				+ "getLogGroupName = " + context.getLogGroupName() + "; getInvokedFunctionArn = " + context.getInvokedFunctionArn());
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Powered-By", "AWS Lambda & Serverless");
		headers.put("Content-Type", "application/json");
		
		Map<String, String> queryStringParameters = (Map<String, String>) input.get("queryStringParameters");
		if(queryStringParameters == null || queryStringParameters.size() == 0) {
			return ApiGatewayResponse.builder().setStatusCode(404).setObjectBody(new Response("Please enter a positive integer as query string parameter", input)).setHeaders(headers).build();
		}
		
		int n = -1;
		try{
			n = Integer.valueOf(queryStringParameters.get("n")).intValue();
		} catch (NumberFormatException e) {
			return ApiGatewayResponse.builder().setStatusCode(404).setObjectBody(new Response("Please enter a positive integer as query string parameter", input)).setHeaders(headers).build();
		}
		
		if (n <= 0) {
			return ApiGatewayResponse.builder().setStatusCode(404).setObjectBody(new Response("Please enter a positive integer as query string parameter", input)).setHeaders(headers).build();
		} 
		
		List<Integer> fibonacci = new ArrayList<>();
		
		fibonacci.add(1);
		int count = 1;
		
		if (n > 1) {
			fibonacci.add(1);
			count++;
			
			while (count < n) {
				fibonacci.add(fibonacci.get(count - 1) + fibonacci.get(count - 2));
				count++;
			}
		}
		
		return ApiGatewayResponse.builder()
				.setStatusCode(200)
				.setObjectBody(new MathResponse("Fibonacci array: ", input, fibonacci))
				.setHeaders(headers).build();
	}
	
	
	//This helper returns the Nth fibonacci number. starts with 1, 1, ...
	//It is required to return a Fibonacci array, the straightforward iterative calculation is fastest way to get the result
	//This helper is used to demo the recursive algorithm 
	private int fiboHelper(int n) {
		if (n == 1 || n == 2) {
			return 1;
		}
		return fiboHelper(n - 1) + fiboHelper(n - 2);
	}

}
