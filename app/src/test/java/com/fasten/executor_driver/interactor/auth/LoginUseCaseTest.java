package com.fasten.executor_driver.interactor.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.fasten.executor_driver.entity.ValidationException;
import com.fasten.executor_driver.entity.Validator;
import com.fasten.executor_driver.interactor.DataSharer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginUseCaseTest {

  private LoginUseCase loginUseCase;

  @Mock
  private Validator<String> loginValidator;

  @Mock
  private DataSharer<String> loginSharer;

  @Before
  public void setUp() throws Exception {
    doThrow(new ValidationException()).when(loginValidator).validate(anyString());
    loginUseCase = new LoginUseCaseImpl(loginSharer, loginValidator);
  }

  /* Проверяем работу с валидаторами */

  /**
   * Должен запросить у валидатора логина проверку.
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

  /* Проверяем ответы на валидацию */

  /**
   * Должен ответить ошибкой, если логин не соответствует формату
   *
   * @throws Exception error
   */
  @Test
  public void answerErrorIfLoginInvalid() throws Exception {
    // Действие и Результат:
    loginUseCase.validateLogin("12").test().assertError(ValidationException.class);
  }

  /**
   * Не должно быть ошибок, если логин соответствует формату
   *
   * @throws Exception error
   */
  @Test
  public void answerSuccessIfLoginValid() throws Exception {
    // Дано:
    doNothing().when(loginValidator).validate(anyString());

    // Действие и Результат:
    loginUseCase.validateLogin("").test().assertComplete();
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
    doNothing().when(loginValidator).validate(anyString());
    loginUseCase.validateLogin("checkLogin").test();

    // Результат:
    verifyZeroInteractions(loginSharer);
  }

  /**
   * Должен опубликовать логин.
   *
   * @throws Exception error
   */
  @Test
  public void askDataSharerToShareLogin() throws Exception {
    // Дано:
    doNothing().when(loginValidator).validate(anyString());

    // Действие:
    loginUseCase.rememberLogin().test().assertComplete();
    loginUseCase.validateLogin("checkLogin").test();
    loginUseCase.rememberLogin().test().assertComplete();

    // Результат:
    verify(loginSharer).share(null);
    verify(loginSharer).share("checkLogin");
    verifyNoMoreInteractions(loginSharer);
  }
}