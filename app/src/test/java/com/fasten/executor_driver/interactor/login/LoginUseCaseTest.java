package com.fasten.executor_driver.interactor.login;

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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginUseCaseTest {

	private LoginUseCase loginUseCase;

	@Mock
	private LoginGateway gateway;

	@Mock
	private Validator<String> loginValidator;

	@Mock
	private Validator<String> passwordValidator;

	@Before
	public void setUp() throws Exception {
		loginUseCase = new LoginUseCaseImpl(gateway, loginValidator, passwordValidator);
		when(gateway.login(nullable(LoginData.class))).thenReturn(Completable.never());
		when(loginValidator.validate("login")).thenReturn(true);
		when(passwordValidator.validate("password")).thenReturn(true);
	}

	/* Проверяем работу с валидаторами */

	/**
	 * Должен запросить у валидатора логина проверку
	 *
	 * @throws Throwable error
	 */
	@Test
	public void askLoginValidatorForResult() throws Throwable {
		// when:
		loginUseCase.setLogin("").test();

		// then:
		verify(loginValidator, only()).validate("");
	}

	/**
	 * Должен запросить у валидатора пароля проверку
	 *
	 * @throws Throwable error
	 */
	@Test
	public void askPasswordValidatorForResult() throws Throwable {
		// when:
		loginUseCase.setPassword("").test();

		// then:
		verify(passwordValidator, only()).validate("");
	}

	/* Проверяем ответы сеттеров с валидаторами */

	/**
	 * Должен ответить ошибкой, если логин неверный
	 *
	 * @throws Throwable error
	 */
	@Test
	public void answerErrorIfLoginInvalid() throws Throwable {
		// then:
		loginUseCase.setLogin("").test().assertError(IllegalArgumentException.class);
	}

	/**
	 * Должен ответить ошибкой, если пароль неверный
	 *
	 * @throws Throwable error
	 */
	@Test
	public void answerErrorIfPasswordInvalid() throws Throwable {
		// then:
		loginUseCase.setPassword("").test().assertError(IllegalArgumentException.class);
	}

	/**
	 * Должен ответить успехом, если логин верный
	 *
	 * @throws Throwable error
	 */
	@Test
	public void answerSuccessIfLoginValid() throws Throwable {
		// when:
		when(loginValidator.validate(anyString())).thenReturn(true);

		// then:
		loginUseCase.setLogin("").test().assertComplete();
	}

	/**
	 * Должен ответить успехом, если пароль верный
	 *
	 * @throws Throwable error
	 */
	@Test
	public void answerSuccessIfPasswordValid() throws Throwable {
		// when:
		when(passwordValidator.validate(anyString())).thenReturn(true);

		// then:
		loginUseCase.setPassword("").test().assertComplete();
	}

	/* Проверяем работу с гейтвеем */

	/**
	 * Должен запросить у гейтвея completable входа
	 *
	 * @throws Throwable error
	 */
	@Test
	public void askGatewayForLogin() throws Throwable {
		// when:
		loginUseCase.setLogin("login").test();
		loginUseCase.setPassword("password").test();
		loginUseCase.login();

		// then:
		verify(gateway, only()).login(new LoginData("login", "password"));
	}

	/* Проверяем ответы на логин */

	/**
	 * Должен ответить ошибкой аргумента, если данных для входа еще нет
	 *
	 * @throws Throwable error
	 */
	@Test
	public void answerIllegalArgumentErrorIfNoData() throws Throwable {
		// then:
		loginUseCase.login().test().assertError(IllegalArgumentException.class);
	}

	/**
	 * Должен ответить ошибкой аргумента, если не было логина для входа
	 *
	 * @throws Throwable error
	 */
	@Test
	public void answerIllegalArgumentErrorIfNoLogin() throws Throwable {
		// when:
		loginUseCase.setPassword("password").test();

		// then:
		loginUseCase.login().test().assertError(IllegalArgumentException.class);
	}

	/**
	 * Должен ответить ошибкой аргумента, если не было пароля для входа
	 *
	 * @throws Throwable error
	 */
	@Test
	public void answerIllegalArgumentErrorIfNoPassword() throws Throwable {
		// when:
		loginUseCase.setLogin("login").test();

		// then:
		loginUseCase.login().test().assertError(IllegalArgumentException.class);
	}

	/**
	 * Должен ответить ошибкой сети
	 *
	 * @throws Throwable error
	 */
	@Test
	public void answerNoNetworkError() throws Throwable {
		// when:
		when(gateway.login(any(LoginData.class))).thenReturn(Completable.error(new NoNetworkException()));
		loginUseCase.setLogin("login").test();
		loginUseCase.setPassword("password").test();

		// then:
		loginUseCase.login().test().assertError(NoNetworkException.class);
	}

	/**
	 * Должен ответить успехом
	 *
	 * @throws Throwable error
	 */
	@Test
	public void answerLoginSuccessful() throws Throwable {
		// when:
		when(gateway.login(any(LoginData.class))).thenReturn(Completable.complete());
		loginUseCase.setLogin("login").test();
		loginUseCase.setPassword("password").test();

		// then:
		loginUseCase.login().test().assertComplete();
	}

}