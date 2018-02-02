package com.fasten.executor_driver.interactor.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.web.ValidationException;
import com.fasten.executor_driver.entity.Validator;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginUseCaseTest {

  private LoginUseCase loginUseCase;

  @Mock
  private LoginGateway gateway;

  @Mock
  private Validator<String> loginValidator;

  @Mock
  private DataSharer<String> loginSharer;

  @Before
  public void setUp() throws Exception {
    loginUseCase = new LoginUseCaseImpl(gateway, loginSharer, loginValidator);
    when(gateway.checkLogin(nullable(String.class))).thenReturn(Completable.never());
  }

	/* Проверяем работу с валидаторами */

  /**
   * Должен запросить у валидатора логина проверку
   *
   * @throws Exception error
   */
  @Test
  public void askLoginValidatorForResult() throws Exception {
    // Действие:
    loginUseCase.validateLogin("").test();

    // Результат:
    verify(loginValidator, only()).validate("");
  }

	/* Проверяем работу с валидатором */

  /**
   * Должен ответить ошибкой, если логин не соответствует формату
   *
   * @throws Exception error
   */
  @Test
  public void answerErrorIfLoginInvalid() throws Exception {
    // Результат:
    loginUseCase.validateLogin("12").test().assertError(ValidationException.class);
  }

  /**
   * Не должно быть ошибок, если логин соответствует формату
   *
   * @throws Exception error
   */
  @Test
  public void answerSuccessIfLoginValid() throws Exception {
    // Действие:
    when(loginValidator.validate(anyString())).thenReturn(true);

    // Результат:
    loginUseCase.validateLogin("").test().assertComplete();
  }

	/* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея проверку логина
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayForLoginCheck() throws Exception {
    // Действие:
    loginUseCase.checkLogin("checkLogin").test();

    // Результат:
    verify(gateway, only()).checkLogin("checkLogin");
  }

	/* Проверяем работу с публикатором логина */

  /**
   * Не должен трогать публикатор.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchDataSharer() throws Exception {
    // Действие:
    loginUseCase.validateLogin("checkLogin").test();
    when(loginValidator.validate(any(String.class))).thenReturn(true);
    loginUseCase.validateLogin("checkLogin").test();
    loginUseCase.checkLogin("checkLogin").test();
    when(gateway.checkLogin(any(String.class)))
        .thenReturn(Completable.error(new NoNetworkException()));
    loginUseCase.checkLogin("checkLogin").test();

    // Результат:
    verifyZeroInteractions(loginSharer);
  }

  /**
   * Должен опубликовать логин после успешной проверки на сервере.
   *
   * @throws Exception error
   */
  @Test
  public void askDataSharerToShareLogin() throws Exception {
    // Действие:
    when(gateway.checkLogin(any(String.class))).thenReturn(Completable.complete());
    loginUseCase.checkLogin("checkLogin").test();

    // Результат:
    verify(loginSharer, only()).share("checkLogin");
  }

	/* Проверяем ответы на проверку логина */

  /**
   * Должен ответить ошибкой аргумента
   *
   * @throws Exception error
   */
  @Test
  public void answerArgumentError() throws Exception {
    // Результат:
    loginUseCase.checkLogin(null).test().assertError(ValidationException.class);
  }

  /**
   * Должен ответить ошибкой сети
   *
   * @throws Exception error
   */
  @Test
  public void answerNoNetworkError() throws Exception {
    // Действие:
    when(gateway.checkLogin(any(String.class)))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Результат:
    loginUseCase.checkLogin("").test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом
   *
   * @throws Exception error
   */
  @Test
  public void answerLoginSuccessful() throws Exception {
    // Действие:
    when(gateway.checkLogin(any(String.class))).thenReturn(Completable.complete());
    loginUseCase.checkLogin("").test().assertComplete();
  }
}