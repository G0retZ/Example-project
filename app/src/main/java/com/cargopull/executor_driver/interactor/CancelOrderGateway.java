package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import io.reactivex.Completable;

/**
 * Гейтвей отмены заказа.
 */
public interface CancelOrderGateway {

  /**
   * Отменить заказ с причиной.
   *
   * @param cancelOrderReason - причина отмены
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable cancelOrder(@NonNull CancelOrderReason cancelOrderReason);
}
