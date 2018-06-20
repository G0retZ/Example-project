package com.fasten.executor_driver.presentation.onlineswitch;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки в данных с сервера.
 */
class OnlineSwitchViewStateServerDataError implements ViewState<OnlineSwitchViewActions> {

  @Nullable
  private final ViewState<OnlineSwitchViewActions> parentViewState;

  OnlineSwitchViewStateServerDataError(
      @Nullable ViewState<OnlineSwitchViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  @CallSuper
  public void apply(@NonNull OnlineSwitchViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showServerDataError();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OnlineSwitchViewStateServerDataError that = (OnlineSwitchViewStateServerDataError) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
