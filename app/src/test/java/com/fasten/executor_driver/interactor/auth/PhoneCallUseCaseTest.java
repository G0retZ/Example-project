package com.fasten.executor_driver.interactor.auth;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PhoneCallUseCaseTest {

	private PhoneCallUseCase phoneCallUseCase;

	@Mock
	private PhoneCallGateway gateway;

	@Mock
	private Validator<String> phoneNumberValidator;

	@Before
	public void setUp() throws Exception {
		phoneCallUseCase = new PhoneCallUseCaseImpl(gateway, phoneNumberValidator);
		when(gateway.callMe(anyString())).thenReturn(Completable.never());
		when(phoneNumberValidator.validate("0123456")).thenReturn(true);
	}

	/* Проверяем работу с валидаторами */

	/**
	 * Должен запросить у валидатора номера телефона проверку.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void askPhoneNumberValidatorForResult() throws Exception {
		// Действие:
		phoneCallUseCase.callMe("").test();

		// Результат:
		verify(phoneNumberValidator, only()).validate("");
	}

	/* Проверяем ответы валидатора */

	/**
	 * Должен ответить ошибкой, если номер телефона неверный.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void answerErrorIfPhoneNumberInvalid() throws Exception {
		// Результат:
		phoneCallUseCase.callMe("").test().assertError(IllegalArgumentException.class);
	}

	/**
	 * Не должно быть ошибок, если номер телефона соответствует формату.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void answerSuccessIfPhoneNumberValid() throws Exception {
		// Действие:
		when(phoneNumberValidator.validate(anyString())).thenReturn(true);

		// Результат:
		phoneCallUseCase.callMe("").test().assertNoErrors();
	}

	/* Проверяем работу с гейтвеем */

	/**
	 * Не должен запрашивать у гейтвея звонок, если валидация не прошла.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void doNotAskGatewayForCall() throws Exception {
		// Действие:
		phoneCallUseCase.callMe("012345").test();

		// Результат:
		verifyZeroInteractions(gateway);
	}

	/**
	 * Должен запросить у гейтвея звонок.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void askGatewayForCall() throws Exception {
		// Действие:
		phoneCallUseCase.callMe("0123456").test();

		// Результат:
		verify(gateway, only()).callMe("0123456");
	}

	/* Проверяем ответы на запрос звонка */

	/**
	 * Должен ответить ошибкой сети.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void answerNoNetworkError() throws Exception {
		// Действие:
		when(gateway.callMe(anyString())).thenReturn(Completable.error(new NoNetworkException()));

		// Результат:
		phoneCallUseCase.callMe("0123456").test().assertError(NoNetworkException.class);
	}

	/**
	 * Должен ответить успехом.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void answerCallSuccessful() throws Exception {
		// Действие:
		when(gateway.callMe(anyString())).thenReturn(Completable.complete());

		// Результат:
		phoneCallUseCase.callMe("0123456").test().assertComplete();
	}
}
