package com.fasten.executor_driver.presentation.onlineswitch;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки сокета при включенном переключателе.
 */
class OnlineSwitchViewStateUnCheckedSocketError implements ViewState<OnlineSwitchViewActions> {

  @Override
  @CallSuper
  public void apply(@NonNull OnlineSwitchViewActions stateActions) {
    stateActions.checkSwitch(false);
    stateActions.showSwitchPending(false);
    stateActions.showError(R.string.no_network_connection);
  }
}
