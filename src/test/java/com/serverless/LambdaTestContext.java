package com.serverless;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class LambdaTestContext implements Context {
	private static final Logger LOG = Logger.getLogger(LambdaTestContext.class);
	
	private String functionName;
	
	public LambdaTestContext(String functionName) {
		this.functionName = functionName;
	}
	
	@Override
	public int getMemoryLimitInMB() {
		return 1024;
	}
	
	@Override
    public String getFunctionName() {
        return "TEST" + functionName;
    }
	
	@Override
    public String getFunctionVersion() {
        return "TESTFunctionVersion";
    }
	
	@Override
	public LambdaLogger getLogger() {
		return new LambdaLogger() {
			@Override
			public void log(String s) {
				LOG.info(s);
			}
		};
	}
	
	@Override
	public String getAwsRequestId() {
		return "TEST_" + functionName + "_AWSRequestId";
	}
	
	@Override
	public CognitoIdentity getIdentity() {
		return new CognitoIdentity() {
			@Override
			public String getIdentityId() {
				return "TESTIdentityId";
			}
			
			@Override
			public String getIdentityPoolId() {
				return "TESTIdentityPoolId";
			}
		};
	}
	
	@Override
	public String getLogStreamName() {
		return "TEST_" + functionName + "_LogStreamName";
	}
	
	@Override
	public int getRemainingTimeInMillis() {
		return 1024;
	}
	
	@Override
	public String getLogGroupName() {
		return "TESTLogGroupName";
	}
	
	@Override
	public String getInvokedFunctionArn() {
		return "TESTInvokedFunctionArn";
	}
	
	@Override
	public ClientContext getClientContext() {
		return new LambdaTestClientContext();
	}
	
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
}
