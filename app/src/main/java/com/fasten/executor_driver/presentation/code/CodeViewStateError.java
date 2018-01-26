package com.fasten.executor_driver.presentation.code;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;
import com.fasten.executor_driver.utils.ThrowableUtils;

/**
 * Состояние ошибки при вводе кода.
 */
public final class CodeViewStateError implements ViewState<CodeViewActions> {

	@NonNull
	private final Throwable error;

	CodeViewStateError(@NonNull Throwable error) {
		this.error = error;
	}

	@Override
	public void apply(@NonNull CodeViewActions stateActions) {
		stateActions.showPending(false);
		stateActions.showError(error);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CodeViewStateError that = (CodeViewStateError) o;

		return ThrowableUtils.throwableEquals(error, that.error);
	}

	@Override
	public int hashCode() {
		return error.hashCode();
	}

}
