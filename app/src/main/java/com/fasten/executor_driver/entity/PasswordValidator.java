package com.fasten.executor_driver.entity;

import android.support.annotation.Nullable;

/**
 * Валидатор логина
 */
class PasswordValidator implements Validator<String> {

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean validate(@Nullable String login) {
		if (login == null) return false;
		if (login.length() != 4) return false;
		if (!login.matches("\\d*")) return false;
		return true;
	}

}
