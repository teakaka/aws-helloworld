package com.serverless;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class MathHandlerTest {
	private static final Logger LOG = Logger.getLogger(MathHandlerTest.class);
	
	private MathHandler subject;
    private LambdaTestContext testContext;

    @Before
    public void setUp() throws Exception {
        subject = new MathHandler();  
        testContext = new LambdaTestContext("fibonacci");
    }

    @Test
    public void testHandleRequest() {
    	Map<String, Object> input = new HashMap<>(); 
    	Map<String, String> queryStringMap = new HashMap<>();
    	queryStringMap.put("n", "5");
        input.put("queryStringParameters", queryStringMap);
        
        ApiGatewayResponse output = subject.handleRequest(input, testContext);
        assertNotNull(output);
        String mathResponse = output.getBody();
        LOG.info(mathResponse);
        assertTrue(mathResponse.contains("\"nums\":[1,1,2,3,5]"));
    }
    
    @Test
    public void testHandleRequestNullInput() {
    	Map<String, Object> input = new HashMap<>();   	
        input.put("queryStringParameters", null);
        
        ApiGatewayResponse output = subject.handleRequest(input, testContext);
        assertNotNull(output);
        String response = output.getBody();
        LOG.info(response);
        assertTrue(response.contains("Please enter a positive integer as query string parameter"));
        assertEquals(404, output.getStatusCode());
    }
    
    @Test
    public void testHandleRequestEmptyInput() {
    	Map<String, Object> input = new HashMap<>(); 
    	Map<String, String> queryStringMap = new HashMap<>();
    	queryStringMap.put("n", "");
        input.put("queryStringParameters", queryStringMap);
        
        ApiGatewayResponse output = subject.handleRequest(input, testContext);
        assertNotNull(output);
        String response = output.getBody();
        assertTrue(response.contains("Please enter a positive integer as query string parameter"));
        assertEquals(404, output.getStatusCode());
    }
    
    @Test
    public void testHandleRequestNegativeInput() {
    	Map<String, Object> input = new HashMap<>(); 
    	Map<String, String> queryStringMap = new HashMap<>();
    	queryStringMap.put("n", "-7");
        input.put("queryStringParameters", queryStringMap);
        
        ApiGatewayResponse output = subject.handleRequest(input, testContext);
        assertNotNull(output);
        String response = output.getBody();
        assertTrue(response.contains("Please enter a positive integer as query string parameter"));
        assertEquals(404, output.getStatusCode());
    }
    
    @Test
    public void testHandleRequestInvalidFormatInput() {
    	Map<String, Object> input = new HashMap<>(); 
    	Map<String, String> queryStringMap = new HashMap<>();
    	queryStringMap.put("n", "abc");
        input.put("queryStringParameters", queryStringMap);
        
        ApiGatewayResponse output = subject.handleRequest(input, testContext);
        assertNotNull(output);
        String response = output.getBody();
        assertTrue(response.contains("Please enter a positive integer as query string parameter"));
        assertEquals(404, output.getStatusCode());
    }
    

}
