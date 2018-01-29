package com.fasten.executor_driver.presentation.code;

import android.support.annotation.NonNull;

/**
 * Состояние процесса при вводе кода.
 */
final class CodeViewStatePending extends CodeViewStateCommon {

	CodeViewStatePending(int inputMessageId) {
		super(inputMessageId);
	}

	CodeViewStatePending(@NonNull CodeViewStateCommon codeViewStateCommon) {
		super(codeViewStateCommon);
	}

	@Override
	public void apply(@NonNull CodeViewActions stateActions) {
		super.apply(stateActions);
		stateActions.showPending(true);
		stateActions.showError(null);
	}
}
