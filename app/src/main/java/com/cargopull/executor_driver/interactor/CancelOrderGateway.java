package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import java.util.List;

/**
 * Гейтвей отмены заказа.
 */
public interface CancelOrderGateway {

  /**
   * Получать список причин отмены от заказа.
   *
   * @param channelId - ID "канала", откуда брать причины отказа.
   * @return {@link Flowable<CancelOrderReason>} результат запроса.
   */
  @NonNull
  Flowable<List<CancelOrderReason>> loadCancelOrderReasons(@NonNull String channelId);

  /**
   * Отменить заказ с причиной.
   *
   * @param cancelOrderReason - причина отмены
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable cancelOrder(@NonNull CancelOrderReason cancelOrderReason);
}