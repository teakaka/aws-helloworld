package com.serverless;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class QueryExternalHandlerTest {

	private static final Logger LOG = Logger.getLogger(QueryExternalHandlerTest.class);
	
	private QueryExternalHandler subject;
    private LambdaTestContext testContext;

    @Before
    public void setUp() throws Exception {
        subject = new QueryExternalHandler();  
        testContext = new LambdaTestContext("externalJson");
    }

    @Test
    public void testHandleRequest() {
    	Map<String, Object> input = new HashMap<>(); 
    	Map<String, String> pathParamMap = new HashMap<>();
    	pathParamMap.put("id", "3");
        input.put("pathParameters", pathParamMap);
        
        ApiGatewayResponse output = subject.handleRequest(input, testContext);
        assertNotNull(output);
        String queryResponse = output.getBody();
        LOG.info(queryResponse);
        assertTrue(queryResponse.contains("userId"));
        assertTrue(queryResponse.contains("ea molestias quasi exercitationem repellat qui ipsa sit aut"));
    }
    
    @Test
    public void testHandleRequestNullPathParm() {
    	Map<String, Object> input = new HashMap<>(); 
    	Map<String, String> pathParamMap = new HashMap<>();
    	pathParamMap.put("id", null);
        input.put("pathParameters", pathParamMap);
        
        ApiGatewayResponse output = subject.handleRequest(input, testContext);
        assertNotNull(output);
        String queryResponse = output.getBody();
        LOG.info(queryResponse);
        assertTrue(queryResponse.contains("No Id found"));
        assertEquals(404, output.getStatusCode());
    }
    
    @Test
    public void testHandleRequestNonIntegerPathParm() {
    	Map<String, Object> input = new HashMap<>(); 
    	Map<String, String> pathParamMap = new HashMap<>();
    	pathParamMap.put("id", "abc");
        input.put("pathParameters", pathParamMap);
        
        ApiGatewayResponse output = subject.handleRequest(input, testContext);
        assertNotNull(output);
        String queryResponse = output.getBody();
        LOG.info(queryResponse);
        assertTrue(queryResponse.contains("Id is not integer"));
        assertEquals(404, output.getStatusCode());
    }
    
    @Test
    public void testHandleRequestNoMatchingId() {
    	Map<String, Object> input = new HashMap<>(); 
    	Map<String, String> pathParamMap = new HashMap<>();
    	pathParamMap.put("id", Integer.toString(Integer.MAX_VALUE));
        input.put("pathParameters", pathParamMap);
        
        ApiGatewayResponse output = subject.handleRequest(input, testContext);
        assertNotNull(output);
        String queryResponse = output.getBody();
        LOG.info(queryResponse);
        assertTrue(queryResponse.contains("\"users\":[]"));
    }
}
