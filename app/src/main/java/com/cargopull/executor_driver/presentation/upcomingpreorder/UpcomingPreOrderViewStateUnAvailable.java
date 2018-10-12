package com.cargopull.executor_driver.presentation.upcomingpreorder;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние недоступности предстоящего предзаказа.
 */
final class UpcomingPreOrderViewStateUnAvailable implements ViewState<UpcomingPreOrderViewActions> {

  @Override
  public void apply(@NonNull UpcomingPreOrderViewActions stateActions) {
    stateActions.showUpcomingPreOrderAvailable(false);
  }
}
