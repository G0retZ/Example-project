package com.fasten.executor_driver.presentation.splahscreen;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания на экране заставки.
 */
final class SplashScreenViewStatePending implements ViewState<SplashScreenViewActions> {

  @Override
  public void apply(@NonNull SplashScreenViewActions stateActions) {
    stateActions.showPending(true);
  }
}
