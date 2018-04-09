package com.fasten.executor_driver.presentation.services;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import java.util.List;

/**
 * Действия для смены состояния вида окна списка услуг исполнителя.
 */
public interface ServicesViewActions {

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
  void showServicesPending(boolean pending);

  /**
   * Показать список услуг исполнителя.
   *
   * @param show - показать или нет?
   */
  void showServicesList(boolean show);

  /**
   * Передать список услуг исполнителя.
   *
   * @param servicesListItems - список услуг исполнителя.
   */
  void setServicesListItems(@NonNull List<ServicesListItem> servicesListItems);

  /**
   * Показать сообщение об ошибке.
   *
   * @param show - показать или нет?
   * @param messageId - ИД ресурса сообщения об ошибке
   */
  void showServicesListErrorMessage(boolean show, @StringRes int messageId);

  /**
   * Показать решаемое сообщение об ошибке.
   *
   * @param show - показать или нет?
   * @param messageId - ИД ресурса сообщения об ошибке
   */
  void showServicesListResolvableErrorMessage(boolean show, @StringRes int messageId);
}
