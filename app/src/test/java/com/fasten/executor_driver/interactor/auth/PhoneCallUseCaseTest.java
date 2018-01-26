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
		// when:
		phoneCallUseCase.callMe("").test();

		// then:
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
		// then:
		phoneCallUseCase.callMe("").test().assertError(IllegalArgumentException.class);
	}

	/**
	 * Не должно быть ошибок, если номер телефона соответствует формату.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void answerSuccessIfPhoneNumberValid() throws Exception {
		// when:
		when(phoneNumberValidator.validate(anyString())).thenReturn(true);

		// then:
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
		// when:
		phoneCallUseCase.callMe("012345").test();

		// then:
		verifyZeroInteractions(gateway);
	}

	/**
	 * Должен запросить у гейтвея звонок.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void askGatewayForCall() throws Exception {
		// when:
		phoneCallUseCase.callMe("0123456").test();

		// then:
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
		// when:
		when(gateway.callMe(anyString())).thenReturn(Completable.error(new NoNetworkException()));

		// then:
		phoneCallUseCase.callMe("0123456").test().assertError(NoNetworkException.class);
	}

	/**
	 * Должен ответить успехом.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void answerCallSuccessful() throws Exception {
		// when:
		when(gateway.callMe(anyString())).thenReturn(Completable.complete());

		// then:
		phoneCallUseCase.callMe("0123456").test().assertComplete();
	}
}