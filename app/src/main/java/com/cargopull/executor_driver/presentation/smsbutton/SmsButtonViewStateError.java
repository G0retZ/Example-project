package com.cargopull.executor_driver.presentation.smsbutton;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки при отправке СМС.
 */
final class SmsButtonViewStateError implements ViewState<SmsButtonViewActions> {

  @Override
  public void apply(@NonNull SmsButtonViewActions stateActions) {
    stateActions.setSmsButtonText(R.string.repeat_code_from_sms, null);
    stateActions.enableSmsButton(true);
    stateActions.showSmsSendPending(false);
    stateActions.showSmsSendNetworkErrorMessage(true);
  }
}
