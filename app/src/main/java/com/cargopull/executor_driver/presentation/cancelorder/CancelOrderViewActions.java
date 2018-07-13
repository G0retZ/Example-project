package com.cargopull.executor_driver.presentation.cancelorder;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import java.util.List;

/**
 * Действия для смены состояния вида окна списка ТС исполнителя.
 */
public interface CancelOrderViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showCancelOrderPending(boolean pending);

  /**
   * Показать список ТС.
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
