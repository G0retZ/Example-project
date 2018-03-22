package com.fasten.executor_driver.presentation.splahscreen;

/**
 * Действия для смены состояния вида окна заставки.
 */
public interface SplashScreenViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showPending(boolean pending);

  /**
   * Показать сообщение об ошибке сети.
   *
   * @param show - показать или нет?
   */
  void showNetworkErrorMessage(boolean show);
}
