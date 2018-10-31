package com.cargopull.executor_driver.interactor.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.cargopull.executor_driver.entity.ValidationException;
import com.cargopull.executor_driver.entity.Validator;
import io.reactivex.Observer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginUseCaseTest {

  private LoginUseCase useCase;

  @Mock
  private Validator<String> loginValidator;

  @Mock
  private Observer<String> loginObserver;

  @Before
  public void setUp() throws Exception {
    doThrow(new ValidationException()).when(loginValidator).validate(anyString());
    useCase = new LoginUseCaseImpl(loginObserver, loginValidator);
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
    useCase.validateLogin("").test().isDisposed();

    // Результат:
    verify(loginValidator, only()).validate("");
  }

  /* Проверяем ответы на валидацию */

  /**
   * Должен ответить ошибкой, если логин не соответствует формату.
   */
  @Test
  public void answerErrorIfLoginInvalid() {
    // Действие и Результат:
    useCase.validateLogin("12").test().assertError(ValidationException.class);
  }

  /**
   * Не должно быть ошибок, если логин соответствует формату.
   *
   * @throws Exception error
   */
  @Test
  public void answerSuccessIfLoginValid() throws Exception {
    // Дано:
    doNothing().when(loginValidator).validate(anyString());

    // Действие и Результат:
    useCase.validateLogin("").test().assertComplete();
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
    useCase.validateLogin("checkLogin").test().isDisposed();
    doNothing().when(loginValidator).validate(anyString());
    useCase.validateLogin("checkLogin").test().isDisposed();

    // Результат:
    verifyZeroInteractions(loginObserver);
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
    useCase.rememberLogin().test().assertComplete();
    useCase.validateLogin("checkLogin").test().isDisposed();
    useCase.rememberLogin().test().assertComplete();

    // Результат:
    verify(loginObserver).onComplete();
    verify(loginObserver).onNext("checkLogin");
    verifyNoMoreInteractions(loginObserver);
  }
}