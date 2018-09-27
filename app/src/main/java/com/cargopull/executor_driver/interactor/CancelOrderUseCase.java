package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import io.reactivex.Completable;

/**
 * Юзкейс отмены заказа.
 */
public interface CancelOrderUseCase {

  /**
   * Отменить заказ с причиной.
   *
   * @param cancelOrderReason - причина отмены
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable cancelOrder(@NonNull CancelOrderReason cancelOrderReason);
}
