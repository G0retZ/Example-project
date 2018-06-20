package com.fasten.executor_driver.presentation.onlineswitch;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания.
 */
class OnlineSwitchViewStatePending implements ViewState<OnlineSwitchViewActions> {

  @Nullable
  private final ViewState<OnlineSwitchViewActions> parentViewState;

  OnlineSwitchViewStatePending(@Nullable ViewState<OnlineSwitchViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  @CallSuper
  public void apply(@NonNull OnlineSwitchViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showSwitchPending(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OnlineSwitchViewStatePending that = (OnlineSwitchViewStatePending) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
