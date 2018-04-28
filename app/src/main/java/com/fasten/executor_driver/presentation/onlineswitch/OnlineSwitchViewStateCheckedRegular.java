package com.fasten.executor_driver.presentation.onlineswitch;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние включенного переключателя.
 */
class OnlineSwitchViewStateCheckedRegular implements ViewState<OnlineSwitchViewActions> {

  @Override
  @CallSuper
  public void apply(@NonNull OnlineSwitchViewActions stateActions) {
    stateActions.checkSwitch(true);
    stateActions.showSwitchPending(false);
    stateActions.showError(null, false);
  }
}
