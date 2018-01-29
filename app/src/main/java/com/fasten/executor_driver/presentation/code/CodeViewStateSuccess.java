package com.fasten.executor_driver.presentation.code;

import android.support.annotation.NonNull;

/**
 * Финальное состояние при вводе кода, которое впустит пользователя.
 */
final class CodeViewStateSuccess extends CodeViewStateCommon {

	CodeViewStateSuccess(int inputMessageId) {
		super(inputMessageId);
	}

	CodeViewStateSuccess(@NonNull CodeViewStateCommon codeViewStateCommon) {
		super(codeViewStateCommon);
	}

	@Override
	public void apply(@NonNull CodeViewActions stateActions) {
		super.apply(stateActions);
		stateActions.showPending(false);
		stateActions.showError(null);
		stateActions.letIn();
	}
}
