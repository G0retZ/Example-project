package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Completable;

/**
 * Юзкейс запроса звонка с кодом
 */
public interface SmsUseCase {

	/**
	 * Валидирует номер телефона, и запрашивает на него СМС с кодом.
	 * @param phoneNumber {@link String} номер телефона.
	 * @return {@link Completable} результат валидации или запроса.
	 */
	@NonNull
	Completable sendMeCode(@Nullable String phoneNumber);
}
