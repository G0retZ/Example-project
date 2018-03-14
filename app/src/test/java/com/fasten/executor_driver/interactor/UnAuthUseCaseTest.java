package com.fasten.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.ExecutorState;
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

  @Mock
  private DataSharer<ExecutorState> executorStateSharer;

  @Before
  public void setUp() throws Exception {
    when(gateway.waitForUnauthorized()).thenReturn(Completable.never());
    unAuthUseCase = new UnAuthUseCaseImpl(gateway, executorStateSharer);
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

  /* Проверяем работу с публикатором состояния */

  /**
   * Должен сменить состояние на "не авторизован" при получении такого события.
   *
   * @throws Exception error
   */
  @Test
  public void setUnauthorizedState() throws Exception {
    // Дано:
    when(gateway.waitForUnauthorized()).thenReturn(Completable.complete());

    // Действие:
    unAuthUseCase.getUnauthorized().test();

    // Результат:
    verify(executorStateSharer, only()).share(ExecutorState.UNAUTHORIZED);
  }

  /**
   * Не должен взаимодействовать с публиктором до получения события потери авторизации.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchDataSharer() throws Exception {
    // Действие:
    unAuthUseCase.getUnauthorized().test();

    // Результат:
    verifyZeroInteractions(executorStateSharer);
  }
}