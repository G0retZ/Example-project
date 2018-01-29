package com.fasten.executor_driver.presentation.code;

import android.support.annotation.NonNull;

/**
 * Начальное состояние ввода кода.
 */
final class CodeViewStateInitial extends CodeViewStateCommon {

	CodeViewStateInitial(int inputMessageId) {
		super(inputMessageId);
	}

	CodeViewStateInitial(@NonNull CodeViewStateCommon codeViewStateCommon) {
		super(codeViewStateCommon);
	}

	@Override
	public void apply(@NonNull CodeViewActions stateActions) {
		super.apply(stateActions);
		stateActions.showPending(false);
		stateActions.showError(null);
	}
}
