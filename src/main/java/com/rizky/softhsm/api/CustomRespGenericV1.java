package com.rizky.softhsm.api;

import java.util.List;

public class CustomRespGenericV1 {
	
	boolean success;
	String message;
	List<String> data;
	
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
	
	
	

}
