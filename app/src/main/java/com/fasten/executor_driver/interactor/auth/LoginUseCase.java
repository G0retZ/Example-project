package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Single;

/**
 * Юзкейс проверки имени для входа
 */
public interface LoginUseCase {

	/**
	 * Проверяет имя входа.
	 */
	@NonNull
	Single<String> checkLogin(@Nullable String login);

}
