package com.serverless;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HandlerTest {

private static final Logger LOG = Logger.getLogger(HandlerTest.class);
	
	private Handler subject;
    private LambdaTestContext testContext;

    @Before
    public void setUp() throws Exception {
        subject = new Handler();  
        testContext = new LambdaTestContext("hello");
    }

    
    @Test
    public void testHandleRequest() {
    	Map<String, Object> input = new HashMap<>(); 
    	
        ApiGatewayResponse output = subject.handleRequest(input, testContext);
        assertNotNull(output);
        String response = output.getBody();
        LOG.info(response);
        assertTrue(response.contains("Hello, go serverless!"));
        assertEquals(200, output.getStatusCode());
    }
    
    
    @Test
    public void testGetCustomerNullName() {
    	Map<String, Object> input = new HashMap<>();   	
    	Map<String, String> pathParamMap = new HashMap<>();
    	pathParamMap.put("name", null);
        input.put("pathParameters", pathParamMap);
        testContext.setFunctionName("getCustomers");
        
        ApiGatewayResponse output = subject.getCustomer(input, testContext);
        assertNotNull(output);
        String response = output.getBody();
        LOG.info(response);
        assertTrue(response.contains("Please provide the customer name for query"));
        assertEquals(500, output.getStatusCode());
    }

   
    @Test
    public void testGetCustomerEmptyName() {
    	Map<String, Object> input = new HashMap<>();   	
    	Map<String, String> pathParamMap = new HashMap<>();
    	pathParamMap.put("name", "");
        input.put("pathParameters", pathParamMap);
        testContext.setFunctionName("getCustomers");
        
        ApiGatewayResponse output = subject.getCustomer(input, testContext);
        assertNotNull(output);
        String response = output.getBody();
        LOG.info(response);
        assertTrue(response.contains("Please provide the customer name for query"));
        assertEquals(500, output.getStatusCode());
    }

    @Test
    public void testGetCustomerNameNotFound() {
    	Map<String, Object> input = new HashMap<>();   	
    	Map<String, String> pathParamMap = new HashMap<>();
    	pathParamMap.put("name", "NoSuchPerson");
        input.put("pathParameters", pathParamMap);
        testContext.setFunctionName("getCustomers");
        
        ApiGatewayResponse output = subject.getCustomer(input, testContext);
        assertNotNull(output);
        String response = output.getBody();
        LOG.info(response);
        assertTrue(response.contains("No result found"));
        assertEquals(404, output.getStatusCode());
    }
    
  
    @Test
    public void testGetCustomerNameExist() {
    	Map<String, Object> input = new HashMap<>();   	
    	Map<String, String> pathParamMap = new HashMap<>();
    	pathParamMap.put("name", "TEST_CUSTOMER_EXIST");
        input.put("pathParameters", pathParamMap);
        testContext.setFunctionName("getCustomers");
        
        ApiGatewayResponse output = subject.getCustomer(input, testContext);
        assertNotNull(output);
        String response = output.getBody();
        LOG.info(response);
        
        assertNotNull(response);
      
        if (response.contains("No result found")) {
        	
        	LOG.info("Test customer name does not exist, add it first");
        	Map<String, Object> addInput =new HashMap<>(input);
        	addInput.put("body", "{\"name\":\"TEST_CUSTOMER_EXIST\"}");
        	
        	LambdaTestContext testAddContext = new LambdaTestContext("addCustomer");
        	ApiGatewayResponse addOutput = subject.addCustomer(addInput, testAddContext);
        	
        	assertNotNull(addOutput);
        	String addResponse = addOutput.getBody();
        	assertEquals(200, addOutput.getStatusCode());
        	assertNotNull(addResponse);
        	
        	LOG.info(addResponse);
        	response = addResponse;
        }
        
        ObjectMapper mapper = new ObjectMapper();
        CustomerResponse custResponse = null;
		try {
			custResponse = mapper.readValue(response, new TypeReference<CustomerResponse>() { 
					});
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("Parsing get/addCustomer response failed: "+ response);
		}
        				
		assertNotNull(custResponse);
		assertNotNull(custResponse.getCustomers());
		assertTrue(custResponse.getCustomers().size() > 0);
		assertTrue(custResponse.getCustomers().get(0).getName().contains("TEST_CUSTOMER_EXIST")); 
    }
    

    @Test
    public void testAddCustomerNameAllowDuplicate() {
    	
    	Map<String, Object> input = new HashMap<>();   	
    	input.put("body", "{\"name\":\"TEST_CUSTOMER_EXIST\"}");
    	
    	LambdaTestContext testAddContext = new LambdaTestContext("addCustomer");
    	ApiGatewayResponse addOutput = subject.addCustomer(input, testAddContext);
    	
    	assertNotNull(addOutput);
    	String addResponse = addOutput.getBody();
    	assertEquals(200, addOutput.getStatusCode());
    	assertNotNull(addResponse);
    	assertTrue(addResponse.contains("Added customer successfully"));
        LOG.info(addResponse);
       
        ObjectMapper mapper = new ObjectMapper();
        CustomerResponse custResponse = null;
		try {
			custResponse = mapper.readValue(addResponse, new TypeReference<CustomerResponse>() { 
					});
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("Parsing addCustomer response failed: "+ addResponse);
		}
        				
		assertNotNull(custResponse);
		assertNotNull(custResponse.getCustomers());
		assertTrue(custResponse.getCustomers().size() > 0);
		assertTrue(custResponse.getCustomers().get(0).getName().contains("TEST_CUSTOMER_EXIST")); 
    }
    
    @Test
    public void testAddCustomerInputBodyNull() {
    	
    	Map<String, Object> input = new HashMap<>();   	
    	input.put("body", null);
    	
    	LambdaTestContext testAddContext = new LambdaTestContext("addCustomer");
    	ApiGatewayResponse addOutput = subject.addCustomer(input, testAddContext);
    	
    	assertNotNull(addOutput);   	
    	String addResponse = addOutput.getBody();
    	assertNotNull(addResponse);
    	LOG.info(addResponse);
    	assertEquals(500, addOutput.getStatusCode());
    	assertTrue(addResponse.contains("Please provide the customer name for creation."));        
    }
    
    @Test
    public void testDeleteCustomerNameExist() {
    	Map<String, Object> input = new HashMap<>();   	
    	Map<String, String> pathParamMap = new HashMap<>();
    	pathParamMap.put("name", "TEST_CUSTOMER_EXIST");
        input.put("pathParameters", pathParamMap);
        testContext.setFunctionName("getCustomers");
        
        ApiGatewayResponse output = subject.getCustomer(input, testContext);
        assertNotNull(output);
        String response = output.getBody();
        LOG.info(response);
        
        assertNotNull(response);
      
        //Add the test customer if this customer is not found in db
        if (response.contains("No result found")) {      	
        	LOG.info("Test customer name does not exist, add it first");
        	Map<String, Object> addInput =new HashMap<>(input);
        	addInput.put("body", "{\"name\":\"TEST_CUSTOMER_EXIST\"}");
        	
        	LambdaTestContext testAddContext = new LambdaTestContext("addCustomer");
        	ApiGatewayResponse addOutput = subject.addCustomer(addInput, testAddContext);
        	
        	assertNotNull(addOutput);
        	String addResponse = addOutput.getBody();
        	assertEquals(200, addOutput.getStatusCode());
        	assertNotNull(addResponse);
        	
        	LOG.info(addResponse);
        	response = addResponse;
        }
        
        ObjectMapper mapper = new ObjectMapper();
        CustomerResponse custResponse = null;
		try {
			custResponse = mapper.readValue(response, new TypeReference<CustomerResponse>() { 
					});
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("Parsing get/addCustomer response failed: "+ response);
		}
        				
		assertNotNull(custResponse);
		assertNotNull(custResponse.getCustomers());
		assertTrue(custResponse.getCustomers().size() > 0);
		assertTrue(custResponse.getCustomers().get(0).getName().contains("TEST_CUSTOMER_EXIST")); 
		
		//delete this customer
		LambdaTestContext testDelContext = new LambdaTestContext("deleteCustomer");
		ApiGatewayResponse delOutput = subject.deleteCustomer(input, testDelContext);
        assertNotNull(delOutput);
        String delResponse = delOutput.getBody();
        LOG.info(delResponse);
        assertNotNull(delResponse);
        assertEquals(200, delOutput.getStatusCode());
        assertTrue(delResponse.contains("SUCCESS"));
    }
    
    @Test
    public void testDeleteCustomerNameNull() {
    	Map<String, Object> input = new HashMap<>();   	
    	Map<String, String> pathParamMap = new HashMap<>();
    	pathParamMap.put("name", null);
        input.put("pathParameters", pathParamMap);
        testContext.setFunctionName("deleteCustomers");
        
        ApiGatewayResponse output = subject.deleteCustomer(input, testContext);
        assertNotNull(output);
        String response = output.getBody();
        LOG.info(response);
        
        assertNotNull(response);
        assertEquals(500, output.getStatusCode());
        assertTrue(response.contains("Please provide the customer name for query"));
    }
    
    @Test
    public void testDeleteCustomerNameNotFound() {
    	Map<String, Object> input = new HashMap<>();   	
    	Map<String, String> pathParamMap = new HashMap<>();
    	pathParamMap.put("name", "NO_SUCH_TEST_CUSTOMER");
        input.put("pathParameters", pathParamMap);
        testContext.setFunctionName("deleteCustomers");
        
        ApiGatewayResponse output = subject.deleteCustomer(input, testContext);
        assertNotNull(output);
        String response = output.getBody();
        LOG.info(response);
        
        assertNotNull(response);
        assertEquals(500, output.getStatusCode());
        assertTrue(response.contains("Cannot find this customer NO_SUCH_TEST_CUSTOMER"));
    }
}
