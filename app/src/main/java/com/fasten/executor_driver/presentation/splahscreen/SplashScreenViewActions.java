package com.fasten.executor_driver.presentation.splahscreen;

/**
 * Действия для смены состояния вида окна заставки.
 */
interface SplashScreenViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showPending(boolean pending);
}
