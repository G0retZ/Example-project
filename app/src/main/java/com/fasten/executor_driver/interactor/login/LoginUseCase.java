package com.fasten.executor_driver.interactor.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Completable;

/**
 * Юзкейс входа
 */
public interface LoginUseCase {

	/**
	 * Задает логин.
	 */
	@NonNull
	Completable setLogin(@Nullable String login);

	/**
	 * Задает пароль.
	 */
	@NonNull
	Completable setPassword(String password);

	/**
	 * Совершает вход.
	 */
	@NonNull
	Completable login();

}
