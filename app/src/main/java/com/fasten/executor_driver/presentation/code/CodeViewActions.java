package com.fasten.executor_driver.presentation.code;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

/**
 * Действия для смены состояния вида окна ввода кода.
 */
public interface CodeViewActions {

	/**
	 * Войти в приложение (завершить авторизацию, и перейти далее).
	 */
	void letIn();

	/**
	 * Показать ошибку
	 * @param error - ошибка
	 */
	void showError(@Nullable Throwable error);

	/**
	 * Показать индикатор процесса
	 * @param pending - показать или нет?
	 */
	void showPending(boolean pending);

	/**
	 * Задать {@link StringRes} сообщения для описания поля ввода кода
	 * @param messageId - ИД сообщения
	 */
	void setInputMessage(@StringRes int messageId);
}
