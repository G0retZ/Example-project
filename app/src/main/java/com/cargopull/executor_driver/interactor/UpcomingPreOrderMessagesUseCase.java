package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Юзкейс сообщений о предстоящих предзаказах.
 */
public interface UpcomingPreOrderMessagesUseCase {

  /**
   * Запрашивает сообщения о предстоящих предзаказах.
   *
   * @return {@link Flowable<String>} результат запроса.
   */
  @NonNull
  Flowable<String> getUpcomingPreOrderMessages();
}
