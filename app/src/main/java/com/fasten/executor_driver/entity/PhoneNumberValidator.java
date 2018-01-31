package com.fasten.executor_driver.entity;

import android.support.annotation.Nullable;

/**
 * Валидатор номера телефона
 */
public class PhoneNumberValidator implements Validator<String> {

	@SuppressWarnings("SimplifiableIfStatement")
	@Override
	public boolean validate(@Nullable String phoneNumber) {
		if (phoneNumber == null) return false;
		if (phoneNumber.length() != 11) return false;
		return phoneNumber.matches("7\\d*");
	}
}
