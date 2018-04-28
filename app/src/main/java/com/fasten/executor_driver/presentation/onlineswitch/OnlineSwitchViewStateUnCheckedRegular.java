package com.fasten.executor_driver.presentation.onlineswitch;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние выключенного переключателя.
 */
class OnlineSwitchViewStateUnCheckedRegular implements ViewState<OnlineSwitchViewActions> {

  @Override
  @CallSuper
  public void apply(@NonNull OnlineSwitchViewActions stateActions) {
    stateActions.checkSwitch(false);
    stateActions.showSwitchPending(false);
    stateActions.showError(null, false);
  }
}
