package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.Vehicle;
import io.reactivex.Single;
import java.util.List;

/**
 * Гейтвей выбора ТС исполнителя, кэширует полученные данные, и выдает из кэша всегда.
 */
public interface VehiclesAndOptionsGateway {

  /**
   * Запрашивает у сервера список ТС, достпуных исполнителю. Сервер должен вернуть либо список, либо
   * ошибку с причиной отказа.
   *
   * @return {@link Single} результат запроса списка ТС
   */
  @NonNull
  Single<List<Vehicle>> getExecutorVehicles();

  /**
   * Запрашивает у сервера список опций Исполнителя. Сервер должен вернуть либо список, либо
   * ошибку с причиной отказа.
   *
   * @return {@link Single} результат запроса списка опций Исполнителя
   */
  @SuppressWarnings("unused")
  @NonNull
  Single<List<Option>> getExecutorOptions();
}
