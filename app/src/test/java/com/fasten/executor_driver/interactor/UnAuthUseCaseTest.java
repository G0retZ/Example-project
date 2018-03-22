package com.fasten.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.reactivex.Completable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UnAuthUseCaseTest {

  private UnAuthUseCase unAuthUseCase;

  @Mock
  private UnAuthGateway gateway;

  @Before
  public void setUp() throws Exception {
    when(gateway.waitForUnauthorized()).thenReturn(Completable.never());
    unAuthUseCase = new UnAuthUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея наблюдение за событием потери авторизации.
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayForWatchAuthLoss() throws Exception {
    // Действие:
    unAuthUseCase.getUnauthorized().test();

    // Результат:
    verify(gateway, only()).waitForUnauthorized();
  }

  /* Проверяем реакцию на потерю авторизации */

  /**
   * Должен ответить успехом потери авторизации.
   *
   * @throws Exception error
   */
  @Test
  public void answerAuthLossSuccessful() throws Exception {
    // Дано:
    when(gateway.waitForUnauthorized()).thenReturn(Completable.complete());

    // Действие и Результат:
    unAuthUseCase.getUnauthorized().test().assertComplete();
  }
}