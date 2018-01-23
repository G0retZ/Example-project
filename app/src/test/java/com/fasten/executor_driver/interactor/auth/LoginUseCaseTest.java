package com.fasten.executor_driver.interactor.auth;

import com.fasten.executor_driver.backend.web.NoNetworkException;
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

	@Before
	public void setUp() throws Exception {
		loginUseCase = new LoginUseCaseImpl(gateway, loginValidator);
		when(gateway.checkLogin(nullable(String.class))).thenReturn(Completable.never());
		when(loginValidator.validate("checkLogin")).thenReturn(true);
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
		loginUseCase.checkLogin("").test();

		// then:
		verify(loginValidator, only()).validate("");
	}

	/* Проверяем работу с валидатором */

	/**
	 * Должен ответить ошибкой, если логин не соответствует формату
	 *
	 * @throws Throwable error
	 */
	@Test
	public void answerErrorIfLoginInvalid() throws Throwable {
		// then:
		loginUseCase.checkLogin("").test().assertError(IllegalArgumentException.class);
	}

	/**
	 * Не должно быть ошибок, если логин соответствует формату
	 *
	 * @throws Throwable error
	 */
	@Test
	public void answerSuccessIfLoginValid() throws Throwable {
		// when:
		when(loginValidator.validate(anyString())).thenReturn(true);

		// then:
		loginUseCase.checkLogin("").test().assertNoErrors();
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
		loginUseCase.checkLogin("checkLogin").test();

		// then:
		verify(gateway, only()).checkLogin("checkLogin");
	}

	/* Проверяем ответы на проверку логина */

	/**
	 * Должен ответить ошибкой сети
	 *
	 * @throws Throwable error
	 */
	@Test
	public void answerNoNetworkError() throws Throwable {
		// when:
		when(gateway.checkLogin(any(String.class))).thenReturn(Completable.error(new NoNetworkException()));

		// then:
		loginUseCase.checkLogin("checkLogin").test().assertError(NoNetworkException.class);
	}

	/**
	 * Должен ответить успехом
	 *
	 * @throws Throwable error
	 */
	@Test
	public void answerLoginSuccessful() throws Throwable {
		// when:
		when(gateway.checkLogin(any(String.class))).thenReturn(Completable.complete());
		loginUseCase.checkLogin("checkLogin").test().assertComplete();
	}

}