package com.cargopull.executor_driver.presentation.preorder;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние доступности предзаказа.
 */
final class PreOrderViewStateAvailable implements ViewState<PreOrderViewActions> {

  @Override
  public void apply(@NonNull PreOrderViewActions stateActions) {
    stateActions.showPreOrderAvailable(true);
  }
}
