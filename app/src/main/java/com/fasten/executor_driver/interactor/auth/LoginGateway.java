package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;

import io.reactivex.Completable;

/**
 * Гейтвей входа
 */
public interface LoginGateway {
	@NonNull
	Completable checkLogin(@NonNull String login);
}
