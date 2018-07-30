package com.cargopull.executor_driver.presentation.updatemessage;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel сообщений о новой версии приложения.
 */
interface UpdateMessageViewModel extends ViewModel<UpdateMessageViewActions> {

  /**
   * Запрашивает подписку на сообщения о новой версии приложения.
   */
  void initializeUpdateMessages();
}
