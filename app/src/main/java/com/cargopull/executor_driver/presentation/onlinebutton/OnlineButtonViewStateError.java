package com.cargopull.executor_driver.presentation.onlinebutton;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.ThrowableUtils;

/**
 * Состояние ошибки при выходе на линию.
 */
final class OnlineButtonViewStateError implements ViewState<OnlineButtonViewActions> {

  @NonNull
  private final Throwable error;

  OnlineButtonViewStateError(@NonNull Throwable error) {
    this.error = error;
  }

  @Override
  public void apply(@NonNull OnlineButtonViewActions stateActions) {
    stateActions.enableGoOnlineButton(false);
    stateActions.showGoOnlineError(error);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OnlineButtonViewStateError that = (OnlineButtonViewStateError) o;

    return ThrowableUtils.throwableEquals(error, that.error);
  }

  @Override
  public int hashCode() {
    return error.hashCode();
  }
}
