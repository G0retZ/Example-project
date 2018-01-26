package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.entity.LoginData;

import io.reactivex.Completable;

/**
 * Юзкейс ввода пароля
 */
public interface PasswordUseCase {

	/**
	 * Проверяет формат пароля, и запрашивает авторизацию.
	 * @param loginData {@link LoginData} данные для входа.
	 * @return {@link Completable} результат проверки или входа.
	 */
	@NonNull
	Completable authorize(@NonNull LoginData loginData);
}
