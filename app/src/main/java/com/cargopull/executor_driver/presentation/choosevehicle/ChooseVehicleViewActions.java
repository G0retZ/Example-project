package com.cargopull.executor_driver.presentation.choosevehicle;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import java.util.List;

/**
 * Действия для смены состояния вида окна списка ТС исполнителя.
 */
public interface ChooseVehicleViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showVehicleListPending(boolean pending);

  /**
   * Показать список ТС.
   *
   * @param show - показать или нет?
   */
  void showVehicleList(boolean show);

  /**
   * Передать список ТС.
   *
   * @param chooseVehicleListItems - список ТС
   */
  void setVehicleListItems(@NonNull List<ChooseVehicleListItem> chooseVehicleListItems);

  /**
   * Показать сообщение об ошибке.
   *
   * @param show - показать или нет?
   */
  void showVehicleListErrorMessage(boolean show);

  /**
   * Задать сообщение об ошибке.
   *
   * @param messageId - ИД ресурса сообщения об ошибке
   */
  void setVehicleListErrorMessage(@StringRes int messageId);
}
