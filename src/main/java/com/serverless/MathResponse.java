package com.serverless;

import java.util.Map;
import java.util.List;
import data.Customer;

public class MathResponse extends Response {
	private final List<Integer> nums;
	
	public MathResponse(String message, Map<String, Object> input, List<Integer> nums) {
		super(message, input);
		this.nums = nums;
	}
	
	public List<Integer> getNums() {
		return this.nums;
	}
}
