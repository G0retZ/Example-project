package com.cargopull.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Vehicle;
import io.reactivex.Observable;

/**
 * Юзкейс выборанной ТС исполнителя.
 */
public interface SelectedVehicleUseCase {

  /**
   * Запрашивает выбранное исполнителем ТС для отображения.
   *
   * @return {@link Observable} результат запроса
   */
  @NonNull
  Observable<Vehicle> getSelectedVehicle();
}
