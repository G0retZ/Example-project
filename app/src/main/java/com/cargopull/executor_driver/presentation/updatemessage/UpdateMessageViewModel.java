package com.cargopull.executor_driver.presentation.updatemessage;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel сообщений о новой версии приложения.
 */
public interface UpdateMessageViewModel extends ViewModel<UpdateMessageViewActions> {

  /**
   * Сообщает о том что сообщение прочитано.
   */
  void messageConsumed();

  /**
   * Запрашивает подписку на сообщения о новой версии приложения.
   */
  void initializeUpdateMessages();
}
