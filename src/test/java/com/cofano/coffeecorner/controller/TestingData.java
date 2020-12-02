package com.cofano.coffeecorner.controller;

public class TestingData {
	private final static int NEGATIVE_ONE = -1; 
	
	public static final String VALID_USER_ID = "12345";
	public static final String INVALID_USER_ID = "invalid";
	
	public static final String VALID_USER_OBJECT = "%7B%22id%22%3A%2212345%22%2C%22email%22%3A%22test%40gmail.com%22%2C%22name%22%3A%22Test%20User%22%2C%22iconUri%22%3A%22http%3A%2F%2Fexample.com%2Ficon.jpg%22%2C%22statusCode%22%3A0%7D";
	public static final String INVALID_USER_OBJECT = "%7B%22id%22%3A%2212345%22%2C%22email%22%3A%22test%40gmail.com%22%2C%22iconUri%22%3A%22http%3A%2F%2Fexample.com%2Ficon.jpg%22%2C%22statusCode%22%3A0%7D";
	
	public static final int VALID_USER_STATUS = 0;
	public static final int INVALID_USER_STATUS = 3;
	
	public static final int VALID_EVENT_ID = 32;
	public static final int INVALID_EVENT_ID = NEGATIVE_ONE;
	
	public static final String VALID_EVENT_TYPE = "Break";
	public static final String INVALID_EVENT_TYPE = "Invalid";
	
	public static final int VALID_BULLETIN_ID = 9;
	public static final int INVALID_BULLETIN_ID = NEGATIVE_ONE;
	
	public static final int VALID_MESSAGE_ID = 26;
	public static final int INVALID_MESSAGE_ID = NEGATIVE_ONE;
	
	public static final int HTTP_CUSTOM_ERROR_STATUSCODE = 515;
}
