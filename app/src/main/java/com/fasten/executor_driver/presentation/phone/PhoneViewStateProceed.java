package com.fasten.executor_driver.presentation.phone;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;

/**
 * Финальное состояние при вводе кода по прозвону, которое отправит пользователя далее
 */
public final class PhoneViewStateProceed implements ViewState<PhoneViewActions> {

	@NonNull
	private final String login;

	PhoneViewStateProceed(@NonNull String login) {
		this.login = login;
	}

	@Override
	public String toString() {
		return "PhoneViewStateProceed{" +
				"login='" + login + '\'' +
				'}';
	}

	@Override
	public void apply(@NonNull PhoneViewActions stateActions) {
		stateActions.showPending(false);
		stateActions.showError(null);
		stateActions.setInputEditable(false);
		stateActions.enableButton(false);
		stateActions.proceedNext(login);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PhoneViewStateProceed that = (PhoneViewStateProceed) o;

		return login.equals(that.login);
	}

	@Override
	public int hashCode() {
		return login.hashCode();
	}
}
