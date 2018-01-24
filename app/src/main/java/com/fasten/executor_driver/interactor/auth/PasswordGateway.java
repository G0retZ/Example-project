package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.entity.LoginData;

import io.reactivex.Completable;

/**
 * Гейтвей входа
 */
public interface PasswordGateway {
	@NonNull
	Completable authorize(@NonNull LoginData loginData);
}
