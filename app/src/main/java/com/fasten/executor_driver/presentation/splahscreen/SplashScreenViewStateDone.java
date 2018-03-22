package com.fasten.executor_driver.presentation.splahscreen;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние окончания загрузки на экране заставки.
 */
final class SplashScreenViewStateDone implements ViewState<SplashScreenViewActions> {

  @Override
  public void apply(@NonNull SplashScreenViewActions stateActions) {
    stateActions.showPending(false);
    stateActions.showNetworkErrorMessage(false);
  }
}
