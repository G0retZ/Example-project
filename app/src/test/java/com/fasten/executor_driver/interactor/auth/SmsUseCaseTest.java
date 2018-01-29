package com.fasten.executor_driver.interactor.auth;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.web.ValidationException;
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
public class SmsUseCaseTest {

	private SmsUseCase smsUseCase;

	@Mock
	private SmsGateway gateway;

	@Mock
	private Validator<String> phoneNumberValidator;

	@Before
	public void setUp() throws Exception {
		smsUseCase = new SmsUseCaseImpl(gateway, phoneNumberValidator);
		when(gateway.sendMeCode(anyString())).thenReturn(Completable.never());
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
		smsUseCase.sendMeCode("").test();

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
		smsUseCase.sendMeCode("").test().assertError(ValidationException.class);
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
		smsUseCase.sendMeCode("").test().assertNoErrors();
	}

	/* Проверяем работу с гейтвеем */

	/**
	 * Не должен запрашивать у гейтвея СМС, если валидация не прошла.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void doNotAskGatewayForSms() throws Exception {
		// Действие:
		smsUseCase.sendMeCode("012345").test();

		// Результат:
		verifyZeroInteractions(gateway);
	}

	/**
	 * Должен запросить у гейтвея СМС.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void askGatewayForSms() throws Exception {
		// Действие:
		smsUseCase.sendMeCode("0123456").test();

		// Результат:
		verify(gateway, only()).sendMeCode("0123456");
	}

	/* Проверяем ответы на запрос СМС */

	/**
	 * Должен ответить ошибкой сети.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void answerNoNetworkError() throws Exception {
		// Действие:
		when(gateway.sendMeCode(anyString())).thenReturn(Completable.error(new NoNetworkException()));

		// Результат:
		smsUseCase.sendMeCode("0123456").test().assertError(NoNetworkException.class);
	}

	/**
	 * Должен ответить успехом.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void answerSmsSendSuccessful() throws Exception {
		// Действие:
		when(gateway.sendMeCode(anyString())).thenReturn(Completable.complete());

		// Результат:
		smsUseCase.sendMeCode("0123456").test().assertComplete();
	}
}
