package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.Vehicle;
import io.reactivex.Completable;
import java.util.List;

/**
 * Гейтвей настройки опций ТС исполнителя.
 */
public interface VehicleOptionsGateway {

  /**
   * Запрашивает у сервера задать опции ТС и исполнителя для выхода на линию. Сервер должен вернуть
   * либо успех, либо ошибку с причиной отказа. В принципе на текущий момент никаких заслуживающих
   * внимания причин для отказа нету.
   *
   * @param vehicle ТС с опциями для изменения
   * @param driverOptions ТС с опциями для изменения
   * @return результат запроса
   */
  @NonNull
  Completable sendVehicleOptions(@NonNull Vehicle vehicle, @NonNull List<Option> driverOptions);
}
