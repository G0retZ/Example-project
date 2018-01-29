package com.fasten.executor_driver.presentation.phone;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние процесса при вводе номера телефона
 */
public final class PhoneViewStatePending implements ViewState<PhoneViewActions> {

	@Override
	public void apply(@NonNull PhoneViewActions stateActions) {
		stateActions.showPending(true);
		stateActions.showError(null);
		stateActions.enableButton(false);
	}
}
