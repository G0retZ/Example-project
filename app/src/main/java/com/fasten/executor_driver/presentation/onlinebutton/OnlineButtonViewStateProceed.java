package com.fasten.executor_driver.presentation.onlinebutton;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Финальное состояние при запросе выхода на линию, которое даст пользователю выбрать автомобиль.
 */
final class OnlineButtonViewStateProceed implements ViewState<OnlineButtonViewActions> {

  @Override
  public void apply(@NonNull OnlineButtonViewActions stateActions) {
    stateActions.setOnlineButtonResponsive(true);
    stateActions.showGoOnlineError(null);
    stateActions.goChooseVehicle();
  }
}
