package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.entity.Validator;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;

public class LoginUseCaseImpl implements LoginUseCase {

	private final LoginGateway gateway;
	private final Validator<String> loginValidator;

	LoginUseCaseImpl(LoginGateway gateway, Validator<String> loginValidator) {
		this.gateway = gateway;
		this.loginValidator = loginValidator;
	}

	@NonNull
	@Override
	public Single<String> checkLogin(String login) {
		return Single.create((SingleOnSubscribe<String>) e -> {
			if (loginValidator.validate(login)) {
				e.onSuccess(login);
			} else {
				e.onError(new IllegalArgumentException());
			}
		}).flatMap(string -> gateway.checkLogin(string).toSingleDefault(string));
	}

}
