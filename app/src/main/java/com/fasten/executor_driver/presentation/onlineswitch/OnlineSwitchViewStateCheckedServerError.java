package com.fasten.executor_driver.presentation.onlineswitch;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки сервера при включенном переключателе.
 */
class OnlineSwitchViewStateCheckedServerError implements ViewState<OnlineSwitchViewActions> {

  @Override
  @CallSuper
  public void apply(@NonNull OnlineSwitchViewActions stateActions) {
    stateActions.checkSwitch(true);
    stateActions.showSwitchPending(false);
    stateActions.showError(R.string.server_fail);
  }
}
