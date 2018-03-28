package com.fasten.executor_driver.presentation.splahscreen;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна заставки.
 */
public interface SplashScreenViewModel extends ViewModel<SplashScreenViewActions> {

  /**
   * Запрашивает инициализацию приложения.
   */
  void initializeApp();
}
