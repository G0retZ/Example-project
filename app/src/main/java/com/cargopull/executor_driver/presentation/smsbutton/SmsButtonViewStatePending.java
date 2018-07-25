package com.cargopull.executor_driver.presentation.smsbutton;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания во время запроса отправки СМС.
 */
public final class SmsButtonViewStatePending implements ViewState<SmsButtonViewActions> {

  @Override
  public void apply(@NonNull SmsButtonViewActions stateActions) {
    stateActions.enableSmsButton(false);
    stateActions.setSmsButtonText(R.string.repeat_code_from_sms, null);
    stateActions.showSmsSendNetworkErrorMessage(false);
    stateActions.showSmsSendPending(true);
  }
}
