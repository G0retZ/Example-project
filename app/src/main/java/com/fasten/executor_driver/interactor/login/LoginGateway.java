package com.fasten.executor_driver.interactor.login;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.entity.LoginData;

import io.reactivex.Completable;

/**
 * Гейтвей входа
 */
public interface LoginGateway {
	@NonNull
	Completable login(@NonNull LoginData loginData);
}
