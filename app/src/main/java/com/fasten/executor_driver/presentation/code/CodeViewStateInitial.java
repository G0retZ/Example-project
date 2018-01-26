package com.fasten.executor_driver.presentation.code;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;

/**
 * Начальное состояние ввода кода.
 */
public final class CodeViewStateInitial implements ViewState<CodeViewActions> {

	@Override
	public void apply(@NonNull CodeViewActions stateActions) {
		stateActions.showPending(false);
		stateActions.showError(null);
	}

}
