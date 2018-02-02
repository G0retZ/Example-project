package com.fasten.executor_driver.presentation.smsbutton;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания во время запроса отправки СМС.
 */
public final class SmsButtonViewStatePending implements ViewState<SmsButtonViewActions> {

  @Override
  public void apply(@NonNull SmsButtonViewActions stateActions) {
    stateActions.setSmsButtonResponsive(false);
    stateActions.showSmsSendError(null);
    stateActions.showSmsSendPending(true);
    stateActions.showSmsButtonTimer(null);
  }
}
