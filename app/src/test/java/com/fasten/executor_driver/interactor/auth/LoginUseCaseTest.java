package com.fasten.executor_driver.interactor.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.ValidationException;
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
    loginUseCase = new LoginUseCaseImpl(loginSharer, loginValidator);
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
    when(loginValidator.validate(anyString())).thenReturn(true);

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

    // Результат:
    verifyZeroInteractions(loginSharer);
  }

  /**
   * Должен опубликовать логин после успешной валидации.
   *
   * @throws Exception error
   */
  @Test
  public void askDataSharerToShareLogin() throws Exception {
    // Дано:
    when(loginValidator.validate(any(String.class))).thenReturn(true);

    // Действие:
    loginUseCase.validateLogin("checkLogin").test();

    // Результат:
    verify(loginSharer, only()).share("checkLogin");
  }
}