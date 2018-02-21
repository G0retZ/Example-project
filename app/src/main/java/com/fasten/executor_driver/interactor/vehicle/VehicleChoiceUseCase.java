package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Vehicle;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.List;

/**
 * Юзкейс выбора ТС исполнителя.
 */
public interface VehicleChoiceUseCase {

  /**
   * Запрашивает список ТС, доступных исполнителю.
   *
   * @return {@link Single} результат запроса
   */
  @NonNull
  Observable<List<Vehicle>> getVehicles();

  /**
   * Задает выбранное ТС для запоминания и сохранения.
   *
   * @param vehicle выбранное ТС из списка полученных ТС
   * @return {@link Completable} результат выбора
   */
  Completable selectVehicle(Vehicle vehicle);
}
