package com.fasten.executor_driver.entity;

import android.support.annotation.Nullable;

/**
 * Валидатор пароля
 */
class PasswordValidator implements Validator<String> {

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean validate(@Nullable String password) {
		if (password == null) return false;
		if (password.length() != 4) return false;
		if (!password.matches("\\d*")) return false;
		return true;
	}

}
