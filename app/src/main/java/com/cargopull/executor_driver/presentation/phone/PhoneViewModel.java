package com.cargopull.executor_driver.presentation.phone;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewModel;

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
