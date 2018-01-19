package com.fasten.executor_driver.backend.web;

public interface TokenKeeper {
	void saveToken(String token);
	String getToken();
}
