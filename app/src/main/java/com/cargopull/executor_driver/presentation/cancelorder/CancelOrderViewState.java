package com.cargopull.executor_driver.presentation.cancelorder;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.presentation.ViewState;
import java.util.List;

/**
 * Состояние вида списка выбора причины отказа.
 */
final class CancelOrderViewState implements ViewState<CancelOrderViewActions> {

  @NonNull
  private final List<CancelOrderReason> cancelOrderReasons;

  CancelOrderViewState(@NonNull List<CancelOrderReason> cancelOrderReasons) {
    this.cancelOrderReasons = cancelOrderReasons;
  }

  @Override
  public void apply(@NonNull CancelOrderViewActions stateActions) {
    stateActions.showCancelOrderReasons(true);
    stateActions.showCancelOrderPending(false);
    stateActions.setCancelOrderReasons(cancelOrderReasons);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CancelOrderViewState that = (CancelOrderViewState) o;

    return cancelOrderReasons.equals(that.cancelOrderReasons);
  }

  @Override
  public int hashCode() {
    return cancelOrderReasons.hashCode();
  }
}
