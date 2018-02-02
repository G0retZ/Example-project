package com.fasten.executor_driver.presentation.smsbutton;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Начальное состояние готовой кнопки.
 */
public final class SmsButtonViewStateReady implements ViewState<SmsButtonViewActions> {

  @Override
  public void apply(@NonNull SmsButtonViewActions stateActions) {
    stateActions.setSmsButtonResponsive(true);
    stateActions.showSmsButtonTimer(null);
    stateActions.showSmsSendError(null);
    stateActions.showSmsSendPending(false);
  }
}
