package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.Order;
import io.reactivex.Flowable;

/**
 * Гейтвей получения предзаказа.
 */
public interface PreOrderGateway {

  /**
   * Ожидает предзаказы у сокета.
   *
   * @param channelId - ID "канала", откуда брать предзаказы.
   * @return {@link Flowable<Order>} предзаказы для исполнителя.
   */
  @NonNull
  Flowable<Order> getPreOrders(@Nullable String channelId);
}
