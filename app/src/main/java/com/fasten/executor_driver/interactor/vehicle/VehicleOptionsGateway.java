package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Vehicle;
import io.reactivex.Completable;

/**
 * Гейтвей настройки опций ТС исполнителя
 */
interface VehicleOptionsGateway {

  /**
   * Запрашивает у сервера задать опции ТС исполнителя для выхода на линию. Сервер должен вернуть
   * либо успех, либо ошибку с причиной отказа. В принципе на текущий момент никаких заслуживающих
   * внимание причин для отказа нету.
   *
   * @param vehicle ТС с опциями для изменения.
   * @return результат запроса.
   */
  @NonNull
  Completable sendVehicleOptions(Vehicle vehicle);
}
