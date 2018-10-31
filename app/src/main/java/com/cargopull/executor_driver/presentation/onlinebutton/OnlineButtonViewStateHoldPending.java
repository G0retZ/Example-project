package com.cargopull.executor_driver.presentation.onlinebutton;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания после запроса выхода на линию.
 */
public final class OnlineButtonViewStateHoldPending implements ViewState<OnlineButtonViewActions> {

  @Override
  public void apply(@NonNull OnlineButtonViewActions stateActions) {
    stateActions.enableGoOnlineButton(false);
    stateActions.showGoOnlinePending(true);
  }
}
