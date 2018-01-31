package com.fasten.executor_driver.presentation.phone;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Действия для смены состояния вида окна входа
 */
public interface PhoneViewActions {

  /**
   * Перейти на следующий шаг. Передает проверенный логин, чтобы сохранился в бандле, и переживал
   * прибитие апп
   *
   * @param login - логин для входа
   */
  @SuppressWarnings("unused")
  void proceedNext(@NonNull String login);

  /**
   * Сделать кнопку "Далее" нажимаемой
   *
   * @param enable - нажимаема или нет?
   */
  void enableButton(boolean enable);

  /**
   * Показать ошибку
   *
   * @param error - ошибка
   */
  void showError(@Nullable Throwable error);

  /**
   * Показать инликатор процесса
   *
   * @param pending - показать или нет?
   */
  void showPending(boolean pending);

  /**
   * Сделать поле ввода редактируемым
   *
   * @param editable - редактируемое или нет?
   */
  void setInputEditable(boolean editable);
}
