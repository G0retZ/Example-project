package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Completable;

/**
 * Юзкейс проверки имени для входа
 */
public interface LoginUseCase {

	/**
	 * Проверяет формат логина.
	 */
	@NonNull
	Completable validateLogin(@Nullable String login);

	/**
	 * Проверяет логин на существование.
	 */
	@NonNull
	Completable checkLogin(@Nullable String login);

}
