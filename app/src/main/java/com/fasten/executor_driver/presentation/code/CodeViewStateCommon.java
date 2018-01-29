package com.fasten.executor_driver.presentation.code;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.fasten.executor_driver.presentation.ViewState;

/**
 * Общее состояние ввода кода.
 */
class CodeViewStateCommon implements ViewState<CodeViewActions> {

	@StringRes
	private final int inputMessageId;

	CodeViewStateCommon(@StringRes int inputMessageId) {
		this.inputMessageId = inputMessageId;
	}

	CodeViewStateCommon(@NonNull CodeViewStateCommon codeViewStateCommon) {
		inputMessageId = codeViewStateCommon.inputMessageId;
	}

	@Override
	@CallSuper
	public void apply(@NonNull CodeViewActions stateActions) {
		stateActions.setInputMessage(inputMessageId);
	}

	@Override
	public String toString() {
		return "CodeViewStateCommon{" +
				"inputMessageId=" + inputMessageId +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CodeViewStateCommon that = (CodeViewStateCommon) o;

		return inputMessageId == that.inputMessageId;
	}

	@Override
	public int hashCode() {
		return inputMessageId;
	}
}
