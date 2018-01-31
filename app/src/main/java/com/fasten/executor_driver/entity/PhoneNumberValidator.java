package com.fasten.executor_driver.entity;

import android.support.annotation.Nullable;

import javax.inject.Inject;

/**
 * Валидатор номера телефона
 */
public class PhoneNumberValidator implements Validator<String> {

	@Inject
	PhoneNumberValidator() {
	}

	@SuppressWarnings("SimplifiableIfStatement")
	@Override
	public boolean validate(@Nullable String phoneNumber) {
		if (phoneNumber == null) return false;
		if (phoneNumber.length() != 11) return false;
		return phoneNumber.matches("7\\d*");
	}
}
