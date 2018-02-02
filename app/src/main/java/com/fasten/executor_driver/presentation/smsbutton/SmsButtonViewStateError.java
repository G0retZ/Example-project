package com.fasten.executor_driver.presentation.smsbutton;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;
import com.fasten.executor_driver.utils.ThrowableUtils;

/**
 * Состояние ошибки при отправке СМС.
 */
final class SmsButtonViewStateError implements ViewState<SmsButtonViewActions> {

  @NonNull
  private final Throwable error;

  SmsButtonViewStateError(@NonNull Throwable error) {
    this.error = error;
  }

  @Override
  public void apply(@NonNull SmsButtonViewActions stateActions) {
    stateActions.showSmsButtonTimer(null);
    stateActions.setSmsButtonResponsive(false);
    stateActions.showSmsSendPending(false);
    stateActions.showSmsSendError(error);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SmsButtonViewStateError that = (SmsButtonViewStateError) o;

    return ThrowableUtils.throwableEquals(error, that.error);
  }

  @Override
  public int hashCode() {
    return error.hashCode();
  }
}
