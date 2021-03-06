package com.cargopull.executor_driver.presentation.onlineswitch;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние переключателя.
 */
class OnlineSwitchViewState implements ViewState<OnlineSwitchViewActions> {

  /**
   * Включен переключатель или выключен?
   */
  private final boolean online;

  OnlineSwitchViewState(boolean online) {
    this.online = online;
  }

  @Override
  @CallSuper
  public void apply(@NonNull OnlineSwitchViewActions stateActions) {
    stateActions.showTakeBreakButton(online);
    stateActions.showBreakText(!online);
    stateActions.showResumeWorkButton(!online);
    stateActions.showSwitchPending(false);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OnlineSwitchViewState that = (OnlineSwitchViewState) o;

    return online == that.online;
  }

  @Override
  public int hashCode() {
    return (online ? 1 : 0);
  }
}
