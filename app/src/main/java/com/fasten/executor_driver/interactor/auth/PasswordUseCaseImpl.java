package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.backend.web.ValidationException;
import com.fasten.executor_driver.entity.LoginData;
import com.fasten.executor_driver.entity.Validator;

import javax.inject.Inject;

import io.reactivex.Completable;

public class PasswordUseCaseImpl implements PasswordUseCase {

	@NonNull
	private final PasswordGateway gateway;
	@NonNull
	private final Validator<String> passwordValidator;

	@Inject
	public PasswordUseCaseImpl(@NonNull PasswordGateway gateway, @NonNull Validator<String> passwordValidator) {
		this.gateway = gateway;
		this.passwordValidator = passwordValidator;
	}

	@NonNull
	@Override
	public Completable authorize(@NonNull LoginData loginData, @NonNull Completable afterValidation) {
		return Completable.create(e -> {
			if (passwordValidator.validate(loginData.getPassword())) {
				afterValidation.subscribe(
						() -> gateway.authorize(loginData).subscribe(e::onComplete, e::onError),
						throwable -> e.onComplete()
				);
			} else {
				e.onError(new ValidationException());
			}
		});
	}
}
