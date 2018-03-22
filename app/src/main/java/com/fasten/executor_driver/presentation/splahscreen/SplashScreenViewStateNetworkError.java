package com.fasten.executor_driver.presentation.splahscreen;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки сети на экране заставки.
 */
final class SplashScreenViewStateNetworkError implements ViewState<SplashScreenViewActions> {

  @Override
  public void apply(@NonNull SplashScreenViewActions stateActions) {
    stateActions.showPending(false);
    stateActions.showNetworkErrorMessage(true);
  }
}
