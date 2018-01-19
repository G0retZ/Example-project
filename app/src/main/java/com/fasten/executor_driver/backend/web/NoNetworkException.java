package com.fasten.executor_driver.backend.web;

import java.io.IOException;

/**
 * Исключение об отсутствии сети.
 */

@SuppressWarnings("unused")
class NoNetworkException extends IOException {

	NoNetworkException() {
		super();
	}

	NoNetworkException(String message) {
		super(message);
	}

	NoNetworkException(String message, Throwable cause) {
		super(message, cause);
	}

	NoNetworkException(Throwable cause) {
		super(cause);
	}

}
