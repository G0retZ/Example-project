package com.fasten.executor_driver.presentation.timeoutbutton;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания таймаута.
 */
public final class TimeoutButtonViewStateHold implements ViewState<TimeoutButtonViewActions> {

	private final long secondsLeft;

	TimeoutButtonViewStateHold(long secondsLeft) {
		this.secondsLeft = secondsLeft;
	}

	@Override
	public void apply(@NonNull TimeoutButtonViewActions stateActions) {
		stateActions.setResponsive(false);
		stateActions.showTimer(secondsLeft);
	}

	@Override
	public String toString() {
		return "TimeoutButtonViewStateHold{" +
				"secondsLeft=" + secondsLeft +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TimeoutButtonViewStateHold that = (TimeoutButtonViewStateHold) o;

		return secondsLeft == that.secondsLeft;
	}

	@Override
	public int hashCode() {
		return (int) (secondsLeft ^ (secondsLeft >>> 32));
	}
}
