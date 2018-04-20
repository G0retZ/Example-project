package com.fasten.executor_driver.interactor.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.LoginData;
import com.fasten.executor_driver.entity.ValidationException;
import com.fasten.executor_driver.entity.Validator;
import com.fasten.executor_driver.interactor.DataReceiver;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
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
  private DataReceiver<String> loginReceiver;

  @Before
  public void setUp() throws Exception {
    when(gateway.authorize(nullable(LoginData.class))).thenReturn(Completable.never());
    doThrow(new ValidationException()).when(passwordValidator).validate(anyString());
    doNothing().when(passwordValidator).validate("password");
    when(loginReceiver.get()).thenReturn(Observable.never());
    passwordUseCase = new PasswordUseCaseImpl(gateway, loginReceiver, passwordValidator);
  }

  /* Проверяем работу с публикатором логина */

  /**
   * Должен подписываться на данные публиктора.
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void doNotTouchDataSharer() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(""));

    // Действие:
    passwordUseCase.authorize("passwor", Completable.complete()).test();
    passwordUseCase.authorize("password", Completable.never()).test();
    passwordUseCase.authorize("password", Completable.complete()).test();
    when(gateway.authorize(any(LoginData.class)))
        .thenReturn(Completable.error(new NoNetworkException()));
    passwordUseCase.authorize("password", Completable.complete()).test();
    when(gateway.authorize(any(LoginData.class))).thenReturn(Completable.complete());
    passwordUseCase.authorize("password", Completable.complete()).test();

    // Результат:
    verify(loginReceiver, times(5)).get();
  }

  /* Проверяем работу с валидаторами */

  /**
   * Должен запросить у валидатора пароля проверку.
   *
   * @throws Exception error
   */
  @Test
  public void askPasswordValidatorForResult() throws Exception {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(""));

    // Действие:
    passwordUseCase.authorize("", Completable.complete()).test();

    // Результат:
    verify(passwordValidator, only()).validate("");
  }

  /* Проверяем ответы валидатора */

  /**
   * Должен ответить ошибкой, если пароль неверный.
   */
  @Test
  public void answerErrorIfPasswordInvalid() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(""));

    // Действие:
    TestObserver<Void> testObserver =
        passwordUseCase.authorize("", Completable.complete()).test();

    // Результат:
    testObserver.assertError(ValidationException.class);
  }

  /**
   * Не должно быть ошибок, если пароль соответствует формату.
   */
  @Test
  public void answerSuccessIfPasswordValid() {
    // Действие:
    TestObserver<Void> testObserver =
        passwordUseCase.authorize("password", Completable.complete()).test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Не должен запрашивать у гейтвея входа, если валидация не прошла.
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void doNotAskGatewayForAuth() {
    // Действие:
    passwordUseCase.authorize("passwor", Completable.complete()).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрашивать у гейтвея входа, если валидация прошла, но действие после валидации не
   * выполнено.
   */
  @Test
  public void doNotAskGatewayForAuthIfAfterValidationNotComplete() {
    // Действие:
    passwordUseCase.authorize("password", Completable.never()).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрашивать у гейтвея входа, если валидация прошла, но действие после валидации
   * отменено.
   */
  @Test
  public void doNotAskGatewayForAuthIfAfterValidationFailed() {
    // Действие:
    passwordUseCase.authorize("password", Completable.error(new Exception())).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Должен запросить у гейтвея вход.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void askGatewayForAuth() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("login"), Observable.never());
    passwordUseCase = new PasswordUseCaseImpl(gateway, loginReceiver, passwordValidator);

    // Действие:
    passwordUseCase.authorize("password", Completable.complete()).test();

    // Результат:
    verify(gateway, only()).authorize(new LoginData("login", "password"));
  }

  /* Проверяем ответы на авторизацию */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(""));
    when(gateway.authorize(any(LoginData.class)))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> testObserver =
        passwordUseCase.authorize("password", Completable.complete()).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(NoNetworkException.class);
  }

  /**
   * Должен ошибкой, если действие после валиации отменено.
   */
  @Test
  public void answerAfterValidationFailed() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(""));
    when(gateway.authorize(any(LoginData.class))).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver =
        passwordUseCase.authorize("password", Completable.error(new Exception())).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(Exception.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerAuthSuccessful() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(""));
    when(gateway.authorize(any(LoginData.class))).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver =
        passwordUseCase.authorize("password", Completable.complete()).test();

    // Результат:
    testObserver.assertComplete();
    testObserver.assertNoErrors();
  }
}