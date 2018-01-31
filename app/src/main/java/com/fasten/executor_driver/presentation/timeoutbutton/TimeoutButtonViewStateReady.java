package com.fasten.executor_driver.presentation.timeoutbutton;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;

/**
 * Начальное состояние готовой кнопки.
 */
public final class TimeoutButtonViewStateReady implements ViewState<TimeoutButtonViewActions> {

  @Override
  public void apply(@NonNull TimeoutButtonViewActions stateActions) {
    stateActions.setResponsive(true);
    stateActions.showTimer(null);
  }
}
