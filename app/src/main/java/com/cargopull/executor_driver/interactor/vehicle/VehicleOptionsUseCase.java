package com.cargopull.executor_driver.interactor.vehicle;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Option;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.List;

/**
 * Юзкейс выбора ТС исполнителя.
 */
public interface VehicleOptionsUseCase {

  /**
   * Запрашивает список опций выбранной ТС, доступных для изменения исполнителем.
   *
   * @return {@link Observable} результат запроса
   */
  @NonNull
  Observable<List<Option>> getVehicleOptions();

  /**
   * Запрашивает список опций выбранной ТС, доступных для изменения исполнителем.
   *
   * @return {@link Observable} результат запроса
   */
  @NonNull
  Single<List<Option>> getDriverOptions();

  /**
   * Задает опции выбранного ТС для сохранения и выхода на линию.
   *
   * @param vehicleOptions список опций выбранного ТС
   * @param driverOptions список опций исполнителя
   * @return {@link Completable} результат сохранения и выхода на линию
   */
  Completable setSelectedVehicleAndOptions(@NonNull List<Option> vehicleOptions,
      @NonNull List<Option> driverOptions);
}
