package com.fasten.executor_driver.backend.web;

import java.io.IOException;

/**
 * Исключение об ошибке валидации данных.
 */

public class ValidationException extends IOException {

	public ValidationException() {
		super();
	}

	@SuppressWarnings("unused")
	public ValidationException(String message) {
		super(message);
	}

	@SuppressWarnings("unused")
	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	@SuppressWarnings("unused")
	public ValidationException(Throwable cause) {
		super(cause);
	}
}
