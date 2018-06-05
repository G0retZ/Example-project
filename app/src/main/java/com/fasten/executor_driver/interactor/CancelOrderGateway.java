package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.CancelOrderReason;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import java.util.List;

/**
 * Гейтвей отмены заказа.
 */
interface CancelOrderGateway {

  /**
   * Получать список причин отмены от заказа.
   *
   * @return {@link Flowable<CancelOrderReason>} результат запроса.
   */
  @NonNull
  Flowable<List<CancelOrderReason>> loadCancelOrderReasons();

  /**
   * Отменить заказ с причиной.
   *
   * @param cancelOrderReason - причина отмены
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable cancelOrder(@NonNull CancelOrderReason cancelOrderReason);
}
