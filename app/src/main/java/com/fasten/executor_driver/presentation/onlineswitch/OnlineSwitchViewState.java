package com.fasten.executor_driver.presentation.onlineswitch;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние переключателя.
 */
class OnlineSwitchViewState implements ViewState<OnlineSwitchViewActions> {

  /**
   * Включен переключатель или выключен?
   */
  private final boolean checked;

  OnlineSwitchViewState(boolean checked) {
    this.checked = checked;
  }

  @Override
  @CallSuper
  public void apply(@NonNull OnlineSwitchViewActions stateActions) {
    stateActions.checkSwitch(checked);
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

    return checked == that.checked;
  }

  @Override
  public int hashCode() {
    return (checked ? 1 : 0);
  }
}
