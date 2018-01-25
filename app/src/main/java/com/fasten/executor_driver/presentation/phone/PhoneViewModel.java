package com.fasten.executor_driver.presentation.phone;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;

/**
 * ViewModel окна ввода номера телефона
 */
public interface PhoneViewModel {
	/**
	 * Возвращает состояние вида для применения
	 * @return - {@link ViewState} состояние вида
	 */
	@NonNull
	LiveData<ViewState<PhoneViewActions>> getViewStateLiveData();

	/**
	 * Передает введенный/измененный номер для валидации
	 * @param phoneNumber - номер телефона
	 */
	void phoneNumberChanged(String phoneNumber);

	/**
	 * Событие нажатия кнопки "Далее"
	 */
	void nextClicked();
}
