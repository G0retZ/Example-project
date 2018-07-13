package com.cargopull.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Vehicle;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Гейтвей последнего использованного ТС исполнителя.
 */
public interface LastUsedVehicleGateway {

  /**
   * Достает из локальных настроек ИД посленего использованного ТС.
   *
   * @return результат запроса - либо ИД, либо -1
   */
  @NonNull
  Single<Long> getLastUsedVehicleId();

  /**
   * Сохраняет в локальные настройки ИД посленего использованного ТС.
   *
   * @param vehicle ТС использующееся ТС.
   * @return результат запроса
   */
  @NonNull
  Completable saveLastUsedVehicleId(@NonNull Vehicle vehicle);
}
