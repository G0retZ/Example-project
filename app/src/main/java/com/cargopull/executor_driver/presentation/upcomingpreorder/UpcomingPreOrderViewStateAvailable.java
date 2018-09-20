package com.cargopull.executor_driver.presentation.upcomingpreorder;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние доступности предстоящего предзаказа.
 */
final class UpcomingPreOrderViewStateAvailable implements ViewState<UpcomingPreOrderViewActions> {

  @Override
  public void apply(@NonNull UpcomingPreOrderViewActions stateActions) {
    stateActions.showUpcomingPreOrderAvailable(true);
  }
}
