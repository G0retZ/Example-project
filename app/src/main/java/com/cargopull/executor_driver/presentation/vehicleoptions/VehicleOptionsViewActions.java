package com.cargopull.executor_driver.presentation.vehicleoptions;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.cargopull.executor_driver.presentation.FragmentViewActions;

/**
 * Действия для смены состояния вида окна списка опций ТС исполнителя.
 */
public interface VehicleOptionsViewActions extends FragmentViewActions {

  /**
   * Активировать кнопку готовности.
   *
   * @param enable - активировать или нет?
   */
  void enableReadyButton(boolean enable);

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showVehicleOptionsPending(boolean pending);

  /**
   * Показать список опций ТС.
   *
   * @param show - показать или нет?
   */
  void showVehicleOptionsList(boolean show);

  /**
   * Передать список опций ТС и исполнителя.
   *
   * @param vehicleOptionsListItems - список опций ТС и исполнителя.
   */
  void setVehicleOptionsListItems(@NonNull VehicleOptionsListItems vehicleOptionsListItems);

  /**
   * Показать сообщение об ошибке.
   *
   * @param show - показать или нет?
   */
  void showVehicleOptionsListErrorMessage(boolean show);

  /**
   * Задать сообщение об ошибке.
   *
   * @param messageId - ИД ресурса сообщения об ошибке
   */
  void setVehicleOptionsListErrorMessage(@StringRes int messageId);
}
