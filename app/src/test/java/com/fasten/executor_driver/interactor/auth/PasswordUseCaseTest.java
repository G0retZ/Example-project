package com.fasten.executor_driver.interactor.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.ValidationException;
import com.fasten.executor_driver.entity.LoginData;
import com.fasten.executor_driver.entity.Validator;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PasswordUseCaseTest {

  private PasswordUseCase passwordUseCase;

  @Mock
  private PasswordGateway gateway;

  @Mock
  private Validator<String> passwordValidator;

  @Mock
  private DataSharer<String> loginSharer;

  @Before
  public void setUp() throws Exception {
    when(gateway.authorize(nullable(LoginData.class))).thenReturn(Completable.never());
    doThrow(new ValidationException()).when(passwordValidator).validate(anyString());
    doNothing().when(passwordValidator).validate("password");
    when(loginSharer.get()).thenReturn(Observable.just("login"));
    passwordUseCase = new PasswordUseCaseImpl(gateway, loginSharer, passwordValidator);
  }

  /* Проверяем работу с публикатором логина */

  /**
   * Должен подписаться при создании сразу же.
   *
   * @throws Exception error
   */
  @Test
  public void getFromDataSharerImmediately() throws Exception {
    // Результат:
    verify(loginSharer, only()).get();
  }

  /**
   * Не должен взаимодействовать с публиктором в любых иных случаях.
   *
   * @throws Exception error
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void doNotTouchDataSharer() throws Exception {
    passwordUseCase.authorize("passwor", Completable.complete()).test();
    passwordUseCase.authorize("password", Completable.never()).test();
    passwordUseCase.authorize("password", Completable.complete()).test();
    when(gateway.authorize(any(LoginData.class)))
        .thenReturn(Completable.error(new NoNetworkException()));
    passwordUseCase.authorize("password", Completable.complete()).test();
    when(gateway.authorize(any(LoginData.class))).thenReturn(Completable.complete());
    passwordUseCase.authorize("password", Completable.complete()).test();

    // Результат:
    verify(loginSharer, only()).get();
  }

  /* Проверяем работу с валидаторами */

  /**
   * Должен запросить у валидатора пароля проверку.
   *
   * @throws Exception error
   */
  @Test
  public void askPasswordValidatorForResult() throws Exception {
    // Действие:
    passwordUseCase.authorize("", Completable.complete()).test();

    // Результат:
    verify(passwordValidator, only()).validate("");
  }

  /* Проверяем ответы валидатора */

  /**
   * Должен ответить ошибкой, если пароль неверный.
   *
   * @throws Exception error
   */
  @Test
  public void answerErrorIfPasswordInvalid() throws Exception {
    // Результат:
    passwordUseCase.authorize("", Completable.complete())
        .test().assertError(ValidationException.class);
  }

  /**
   * Не должно быть ошибок, если пароль соответствует формату.
   *
   * @throws Exception error
   */
  @Test
  public void answerSuccessIfPasswordValid() throws Exception {
    // Действие:
    doNothing().when(passwordValidator).validate(anyString());

    // Результат:
    passwordUseCase.authorize("password", Completable.complete())
        .test().assertNoErrors();
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Не должен запрашивать у гейтвея входа, если валидация не прошла.
   *
   * @throws Exception error
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void doNotAskGatewayForAuth() throws Exception {
    // Действие:
    passwordUseCase.authorize("passwor", Completable.complete()).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрашивать у гейтвея входа, если валидация прошла, но действие после валидации не
   * выполнено.
   *
   * @throws Exception error
   */
  @Test
  public void doNotAskGatewayForAuthIfAfterValidationNotComplete() throws Exception {
    // Действие:
    passwordUseCase.authorize("password", Completable.never()).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрашивать у гейтвея входа, если валидация прошла, но действие после валидации
   * отменено.
   *
   * @throws Exception error
   */
  @Test
  public void doNotAskGatewayForAuthIfAfterValidationFailed() throws Exception {
    // Действие:
    passwordUseCase.authorize("password", Completable.error(new Exception())).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Должен запросить у гейтвея вход.
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayForAuth() throws Exception {
    // Действие:
    passwordUseCase.authorize("password", Completable.complete()).test();

    // Результат:
    verify(gateway, only()).authorize(new LoginData("login", "password"));
  }

  /* Проверяем ответы на авторизацию */

  /**
   * Должен ответить ошибкой сети.
   *
   * @throws Exception error
   */
  @Test
  public void answerNoNetworkError() throws Exception {
    // Действие:
    when(gateway.authorize(any(LoginData.class)))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Результат:
    passwordUseCase.authorize("password", Completable.complete())
        .test().assertError(NoNetworkException.class);
  }

  /**
   * Должен успехом, если действие после валиации отменено.
   *
   * @throws Exception error
   */
  @Test
  public void answerValidationSuccessful() throws Exception {
    // Результат:
    passwordUseCase.authorize("password", Completable.error(new Exception()))
        .test().assertComplete();
  }

  /**
   * Должен ответить успехом.
   *
   * @throws Exception error
   */
  @Test
  public void answerAuthSuccessful() throws Exception {
    // Действие:
    when(gateway.authorize(any(LoginData.class))).thenReturn(Completable.complete());

    // Результат:
    passwordUseCase.authorize("password", Completable.complete()).test().assertComplete();
  }
}