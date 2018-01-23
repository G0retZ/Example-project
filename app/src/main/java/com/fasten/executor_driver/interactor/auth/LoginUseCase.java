package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Single;

/**
 * Юзкейс входа
 */
public interface LoginUseCase {

	/**
	 * Проверяет номер телефона.
	 */
	@NonNull
	Single<String> checkLogin(@Nullable String login);

}
