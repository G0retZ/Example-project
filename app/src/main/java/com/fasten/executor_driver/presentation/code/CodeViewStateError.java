package com.fasten.executor_driver.presentation.code;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.utils.ThrowableUtils;

/**
 * Состояние ошибки при вводе кода.
 */
final class CodeViewStateError extends CodeViewStateCommon {

	@NonNull
	private final Throwable error;

	CodeViewStateError(int inputMessageId, @NonNull Throwable error) {
		super(inputMessageId);
		this.error = error;
	}

	CodeViewStateError(CodeViewStateCommon codeViewStateCommon, @NonNull Throwable error) {
		super(codeViewStateCommon);
		this.error = error;
	}

	@Override
	public void apply(@NonNull CodeViewActions stateActions) {
		super.apply(stateActions);
		stateActions.showPending(false);
		stateActions.showError(error);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		CodeViewStateError that = (CodeViewStateError) o;

		return ThrowableUtils.throwableEquals(error, that.error);
	}

	@Override
	public int hashCode() {
		return error.hashCode();
	}
}
