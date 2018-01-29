package com.fasten.executor_driver.presentation.phone;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;
import com.fasten.executor_driver.utils.ThrowableUtils;

/**
 * Состояние ошибки при вводе номера телефона
 */
public final class PhoneViewStateError implements ViewState<PhoneViewActions> {

	@NonNull
	private final Throwable error;

	PhoneViewStateError(@NonNull Throwable error) {
		this.error = error;
	}

	@Override
	public void apply(@NonNull PhoneViewActions stateActions) {
		stateActions.showPending(false);
		stateActions.showError(error);
		stateActions.enableButton(false);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PhoneViewStateError that = (PhoneViewStateError) o;

		return ThrowableUtils.throwableEquals(error, that.error);
	}

	@Override
	public int hashCode() {
		return error.hashCode();
	}
}
