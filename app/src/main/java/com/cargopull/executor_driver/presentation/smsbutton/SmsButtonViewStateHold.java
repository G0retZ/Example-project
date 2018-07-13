package com.cargopull.executor_driver.presentation.smsbutton;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания таймаута после отправки СМС.
 */
public final class SmsButtonViewStateHold implements ViewState<SmsButtonViewActions> {

  private final long secondsLeft;

  SmsButtonViewStateHold(long secondsLeft) {
    this.secondsLeft = secondsLeft;
  }

  @Override
  public void apply(@NonNull SmsButtonViewActions stateActions) {
    stateActions.enableSmsButton(false);
    stateActions.setSmsButtonText(R.string.repeat_code_from_sms_delayed, secondsLeft);
    stateActions.showSmsSendNetworkErrorMessage(false);
    stateActions.showSmsSendPending(false);
  }

  @Override
  public String toString() {
    return "SmsButtonViewStateHold{" +
        "secondsLeft=" + secondsLeft +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SmsButtonViewStateHold that = (SmsButtonViewStateHold) o;

    return secondsLeft == that.secondsLeft;
  }

  @Override
  public int hashCode() {
    return (int) (secondsLeft ^ (secondsLeft >>> 32));
  }
}
