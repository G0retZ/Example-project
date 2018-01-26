package com.fasten.executor_driver.interactor.auth;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.LoginData;
import com.fasten.executor_driver.entity.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PasswordUseCaseTest {

	private PasswordUseCase passwordUseCase;

	@Mock
	private PasswordGateway gateway;

	@Mock
	private Validator<String> passwordValidator;

	@Before
	public void setUp() throws Exception {
		passwordUseCase = new PasswordUseCaseImpl(gateway, passwordValidator);
		when(gateway.authorize(nullable(LoginData.class))).thenReturn(Completable.never());
		when(passwordValidator.validate("password")).thenReturn(true);
	}

	/* Проверяем работу с валидаторами */

	/**
	 * Должен запросить у валидатора пароля проверку
	 *
	 * @throws Exception error
	 */
	@Test
	public void askPasswordValidatorForResult() throws Exception {
		// when:
		passwordUseCase.authorize(new LoginData("", "")).test();

		// then:
		verify(passwordValidator, only()).validate("");
	}

	/* Проверяем ответы валидатора */

	/**
	 * Должен ответить ошибкой, если пароль неверный
	 *
	 * @throws Exception error
	 */
	@Test
	public void answerErrorIfPasswordInvalid() throws Exception {
		// then:
		passwordUseCase.authorize(new LoginData("", ""))
				.test().assertError(IllegalArgumentException.class);
	}

	/**
	 * Не должно быть ошибок, если пароль соответствует формату
	 *
	 * @throws Exception error
	 */
	@Test
	public void answerSuccessIfPasswordValid() throws Exception {
		// when:
		when(passwordValidator.validate(anyString())).thenReturn(true);

		// then:
		passwordUseCase.authorize(new LoginData("", ""))
				.test().assertNoErrors();
	}

	/* Проверяем работу с гейтвеем */

	/**
	 * Не должен запрашивать у гейтвея входа, если валидация не прошла
	 *
	 * @throws Exception error
	 */
	@Test
	public void doNotAskGatewayForAuth() throws Exception {
		// when:
		passwordUseCase.authorize(new LoginData("login", "passwor")).test();

		// then:
		verifyZeroInteractions(gateway);
	}

	/**
	 * Должен запросить у гейтвея вход
	 *
	 * @throws Exception error
	 */
	@Test
	public void askGatewayForAuth() throws Exception {
		// when:
		passwordUseCase.authorize(new LoginData("login", "password")).test();

		// then:
		verify(gateway, only()).authorize(new LoginData("login", "password"));
	}

	/* Проверяем ответы на авторизацию */

	/**
	 * Должен ответить ошибкой сети
	 *
	 * @throws Exception error
	 */
	@Test
	public void answerNoNetworkError() throws Exception {
		// when:
		when(gateway.authorize(any(LoginData.class))).thenReturn(Completable.error(new NoNetworkException()));

		// then:
		passwordUseCase.authorize(new LoginData("login", "password")).test().assertError(NoNetworkException.class);
	}

	/**
	 * Должен ответить успехом
	 *
	 * @throws Exception error
	 */
	@Test
	public void answerAuthSuccessful() throws Exception {
		// when:
		when(gateway.authorize(any(LoginData.class))).thenReturn(Completable.complete());

		// then:
		passwordUseCase.authorize(new LoginData("login", "password")).test().assertComplete();
	}

}