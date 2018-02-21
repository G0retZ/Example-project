package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.VehicleOption;
import io.reactivex.Completable;
import io.reactivex.Observable;
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
  Observable<List<VehicleOption>> getVehicleOptions();

  /**
   * Задает опции выбранного ТС для сохранения и выхода на линию.
   *
   * @param vehicleOptions список опций выбранного ТС
   * @return {@link Completable} результат сохранения и выхода на линию
   */
  Completable setSelectedVehicleOptions(List<VehicleOption> vehicleOptions);
}
