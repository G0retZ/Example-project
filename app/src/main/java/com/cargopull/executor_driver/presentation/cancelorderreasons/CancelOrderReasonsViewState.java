package com.cargopull.executor_driver.presentation.cancelorderreasons;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.presentation.ViewState;
import java.util.List;

/**
 * Состояние вида списка выбора причины отказа.
 */
final class CancelOrderReasonsViewState implements ViewState<CancelOrderReasonsViewActions> {

  @NonNull
  private final List<CancelOrderReason> cancelOrderReasons;

  CancelOrderReasonsViewState(@NonNull List<CancelOrderReason> cancelOrderReasons) {
    this.cancelOrderReasons = cancelOrderReasons;
  }

  @Override
  public void apply(@NonNull CancelOrderReasonsViewActions stateActions) {
    stateActions.showCancelOrderReasons(true);
    stateActions.showCancelOrderReasonsPending(false);
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

    CancelOrderReasonsViewState that = (CancelOrderReasonsViewState) o;

    return cancelOrderReasons.equals(that.cancelOrderReasons);
  }

  @Override
  public int hashCode() {
    return cancelOrderReasons.hashCode();
  }
}
