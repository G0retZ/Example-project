package com.cargopull.executor_driver.presentation.upcomingpreorder;

/**
 * Действия для смены состояния вида окна предстоящего предзаказа.
 */
public interface UpcomingPreOrderViewActions {

  /**
   * показать сообщение о том, что доступен предстоящий предзаказ.
   *
   * @param show - показать или нет?
   */
  void showUpcomingPreOrderAvailable(boolean show);
}
