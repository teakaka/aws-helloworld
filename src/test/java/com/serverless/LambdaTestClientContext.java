package com.serverless;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Client;
import com.amazonaws.services.lambda.runtime.ClientContext;

public class LambdaTestClientContext implements ClientContext {

	@Override
	public Client getClient() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getCustom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getEnvironment() {
		// TODO Auto-generated method stub
		return null;
	}

}
