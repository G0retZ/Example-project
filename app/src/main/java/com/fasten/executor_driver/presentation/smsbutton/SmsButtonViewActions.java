package com.fasten.executor_driver.presentation.smsbutton;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

/**
 * Действия для смены состояния вида кнопки отправки СМС с таймаутом.
 */
public interface SmsButtonViewActions {

  /**
   * Задать текст на кнопке.
   *
   * @param textId - ИД ресурса текста
   * @param secondsLeft {@link Integer} сколько секунд осталось. null не отображается.
   */
  void setSmsButtonText(@StringRes int textId, @Nullable Long secondsLeft);

  /**
   * Сделать кнопку отправки СМС нажимаемой.
   *
   * @param enable - нажимаема или нет?
   */
  void enableSmsButton(boolean enable);

  /**
   * Показать сообщение об ошибке сети.
   *
   * @param show - показать или нет?
   */
  void setSmsSendNetworkErrorMessage(boolean show);

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showSmsSendPending(boolean pending);
}
