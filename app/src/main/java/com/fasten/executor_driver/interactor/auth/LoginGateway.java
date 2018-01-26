package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;

import io.reactivex.Completable;

/**
 * Гейтвей входа
 */
public interface LoginGateway {

	/**
	 * Проверяет логин на существование в общей БД.
	 * @param login {@link String} логин.
	 * @return {@link Completable} результат проверки.
	 */
	@NonNull
	Completable checkLogin(@NonNull String login);
}
