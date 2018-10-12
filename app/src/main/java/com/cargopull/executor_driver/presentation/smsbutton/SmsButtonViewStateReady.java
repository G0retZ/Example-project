package com.cargopull.executor_driver.presentation.smsbutton;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Начальное состояние готовой кнопки.
 */
public final class SmsButtonViewStateReady implements ViewState<SmsButtonViewActions> {

  @Override
  public void apply(@NonNull SmsButtonViewActions stateActions) {
    stateActions.enableSmsButton(true);
    stateActions.setSmsButtonText(R.string.repeat_code_from_sms, null);
    stateActions.showSmsSendNetworkErrorMessage(false);
    stateActions.showSmsSendPending(false);
  }
}
