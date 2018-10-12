package com.cargopull.executor_driver.presentation.cancelorderreasons;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import java.util.List;

/**
 * Действия для смены состояния вида окна списка ТС исполнителя.
 */
public interface CancelOrderReasonsViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showCancelOrderReasonsPending(boolean pending);

  /**
   * Показать список причин для отказа.
   *
   * @param show - показать или нет?
   */
  void showCancelOrderReasons(boolean show);

  /**
   * Передать список ТС.
   *
   * @param cancelOrderReasons - список ТС
   */
  void setCancelOrderReasons(@NonNull List<CancelOrderReason> cancelOrderReasons);
}
