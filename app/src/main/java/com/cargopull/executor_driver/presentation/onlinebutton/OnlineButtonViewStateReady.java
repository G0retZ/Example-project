package com.cargopull.executor_driver.presentation.onlinebutton;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Начальное состояние готовой кнопки.
 */
public final class OnlineButtonViewStateReady implements ViewState<OnlineButtonViewActions> {

  @Override
  public void apply(@NonNull OnlineButtonViewActions stateActions) {
    stateActions.enableGoOnlineButton(true);
    stateActions.showGoOnlinePending(false);
  }
}
