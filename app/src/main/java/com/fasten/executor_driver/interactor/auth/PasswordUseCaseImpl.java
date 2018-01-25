package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.entity.LoginData;
import com.fasten.executor_driver.entity.Validator;

import io.reactivex.Completable;

public class PasswordUseCaseImpl implements PasswordUseCase {

	@NonNull
	private final PasswordGateway gateway;
	@NonNull
	private final Validator<String> passwordValidator;

	PasswordUseCaseImpl(@NonNull PasswordGateway gateway, @NonNull Validator<String> passwordValidator) {
		this.gateway = gateway;
		this.passwordValidator = passwordValidator;
	}

	@NonNull
	@Override
	public Completable authorize(@NonNull LoginData loginData) {
		return Completable.create(e -> {
			if (passwordValidator.validate(loginData.getPassword())) {
				e.onComplete();
			} else {
				e.onError(new IllegalArgumentException());
			}
		}).andThen(gateway.authorize(loginData));
	}

}
