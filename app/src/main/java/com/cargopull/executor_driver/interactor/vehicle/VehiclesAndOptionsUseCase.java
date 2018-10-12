package com.cargopull.executor_driver.interactor.vehicle;

import androidx.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Юзкейс получения списка ТС с проверками блокировки, баланса и пр.
 */
public interface VehiclesAndOptionsUseCase {

  /**
   * Загружает список ТС.
   *
   * @return {@link Completable} результат запроса
   */
  @NonNull
  Completable loadVehiclesAndOptions();
}
