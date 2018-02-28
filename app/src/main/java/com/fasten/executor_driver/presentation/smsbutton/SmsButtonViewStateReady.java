package com.fasten.executor_driver.presentation.smsbutton;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Начальное состояние готовой кнопки.
 */
public final class SmsButtonViewStateReady implements ViewState<SmsButtonViewActions> {

  @Override
  public void apply(@NonNull SmsButtonViewActions stateActions) {
    stateActions.enableSmsButton(true);
    stateActions.setSmsButtonText(R.string.repeat_code_from_sms, null);
    stateActions.setSmsSendNetworkErrorMessage(false);
    stateActions.showSmsSendPending(false);
  }
}
