package com.fasten.executor_driver.presentation.phone;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;

/**
 * Начальное состояние ввода номера телефона
 */
public final class PhoneViewStateReady implements ViewState<PhoneViewActions> {

	@Override
	public void apply(@NonNull PhoneViewActions stateActions) {
		stateActions.showPending(false);
		stateActions.showError(null);
		stateActions.enableButton(true);
	}

}
