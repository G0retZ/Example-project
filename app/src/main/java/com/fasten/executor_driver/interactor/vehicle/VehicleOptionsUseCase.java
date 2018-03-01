package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Option;
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
  Observable<List<Option>> getVehicleOptions();

  /**
   * Задает опции выбранного ТС для сохранения и выхода на линию.
   *
   * @param options список опций выбранного ТС
   * @return {@link Completable} результат сохранения и выхода на линию
   */
  Completable setSelectedVehicleOptions(List<Option> options);
}
