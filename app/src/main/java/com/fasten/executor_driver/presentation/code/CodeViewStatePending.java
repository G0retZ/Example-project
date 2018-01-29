package com.fasten.executor_driver.presentation.code;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние процесса при вводе кода.
 */
final class CodeViewStatePending implements ViewState<CodeViewActions> {

	@Override
	public void apply(@NonNull CodeViewActions stateActions) {
		stateActions.showPending(true);
		stateActions.showError(null);
	}
}
