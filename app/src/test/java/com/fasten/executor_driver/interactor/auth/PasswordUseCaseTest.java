package com.fasten.executor_driver.interactor.auth;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.web.ValidationException;
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
    // Действие:
    passwordUseCase.authorize(new LoginData("", ""), Completable.complete()).test();

    // Результат:
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
    // Результат:
    passwordUseCase.authorize(new LoginData("", ""), Completable.complete())
        .test().assertError(ValidationException.class);
  }

  /**
   * Не должно быть ошибок, если пароль соответствует формату
   *
   * @throws Exception error
   */
  @Test
  public void answerSuccessIfPasswordValid() throws Exception {
    // Действие:
    when(passwordValidator.validate(anyString())).thenReturn(true);

    // Результат:
    passwordUseCase.authorize(new LoginData("", ""), Completable.complete())
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
    // Действие:
    passwordUseCase.authorize(
        new LoginData("login", "passwor"),
        Completable.complete()
    ).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрашивать у гейтвея входа, если валидация прошла, но действие после валидации не
   * выполнено
   *
   * @throws Exception error
   */
  @Test
  public void doNotAskGatewayForAuthIfAfterValidationNotComplete() throws Exception {
    // Действие:
    passwordUseCase.authorize(
        new LoginData("login", "password"),
        Completable.never()
    ).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрашивать у гейтвея входа, если валидация прошла, но действие после валидации
   * отменено
   *
   * @throws Exception error
   */
  @Test
  public void doNotAskGatewayForAuthIfAfterValidationFailed() throws Exception {
    // Действие:
    passwordUseCase.authorize(
        new LoginData("login", "password"),
        Completable.error(new Exception())
    ).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Должен запросить у гейтвея вход
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayForAuth() throws Exception {
    // Действие:
    passwordUseCase.authorize(
        new LoginData("login", "password"),
        Completable.complete()
    ).test();

    // Результат:
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
    // Действие:
    when(gateway.authorize(any(LoginData.class)))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Результат:
    passwordUseCase.authorize(
        new LoginData("login", "password"),
        Completable.complete()
    ).test().assertError(NoNetworkException.class);
  }

  /**
   * Должен успехом, если действие после валиации отменено
   *
   * @throws Exception error
   */
  @Test
  public void answerValidationSuccessful() throws Exception {
    // Результат:
    passwordUseCase.authorize(
        new LoginData("login", "password"),
        Completable.error(new Exception())
    ).test().assertComplete();
  }

  /**
   * Должен ответить успехом
   *
   * @throws Exception error
   */
  @Test
  public void answerAuthSuccessful() throws Exception {
    // Действие:
    when(gateway.authorize(any(LoginData.class))).thenReturn(Completable.complete());

    // Результат:
    passwordUseCase.authorize(
        new LoginData("login", "password"),
        Completable.complete()
    ).test().assertComplete();
  }
}