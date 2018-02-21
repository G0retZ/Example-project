package com.fasten.executor_driver.presentation.phone;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна ввода номера телефона.
 */
public interface PhoneViewModel extends ViewModel<PhoneViewActions> {

  /**
   * Передает введенный/измененный номер для валидации.
   *
   * @param phoneNumber - номер телефона
   */
  void phoneNumberChanged(@NonNull String phoneNumber);

  /**
   * Событие нажатия кнопки "Далее".
   */
  void nextClicked();
}
