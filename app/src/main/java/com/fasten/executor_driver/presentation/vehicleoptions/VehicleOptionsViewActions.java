package com.fasten.executor_driver.presentation.vehicleoptions;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import java.util.List;

/**
 * Действия для смены состояния вида окна списка опций ТС исполнителя.
 */
interface VehicleOptionsViewActions {

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
   * Передать список опций ТС.
   *
   * @param chooseVehicleListItems - список ТС.
   */
  void setVehicleOptionsListItems(@NonNull List<VehicleOptionsListItem<?>> chooseVehicleListItems);

  /**
   * Показать сообщение об ошибке.
   *
   * @param show - показать или нет?
   */
  void showVehicleOptionsListErrorMessage(boolean show);

  /**
   * Задать сообщение об ошибке.
   *
   * @param messageId - ИД ресурса сообщения об ошибке.
   */
  void setVehicleOptionsListErrorMessage(@StringRes int messageId);
}
