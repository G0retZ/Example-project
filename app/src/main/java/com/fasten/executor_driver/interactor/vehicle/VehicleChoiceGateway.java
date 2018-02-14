package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Vehicle;
import io.reactivex.Single;
import java.util.List;

/**
 * Гейтвей выбора ТС исполнителя
 */
public interface VehicleChoiceGateway {

  /**
   * Запрашивает у сервера список ТС, достпуных исполнителю. Сервер должен вернуть либо список, либо
   * ошибку с причиной отказа.
   *
   * @return {@link Single} результат запроса списка ТС.
   */
  @NonNull
  Single<List<Vehicle>> getExecutorVehicles();
}
