package com.fasten.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateUseCaseTest {

  private ExecutorStateUseCase executorStateUseCase;

  @Mock
  private ExecutorStateGateway gateway;

  @Mock
  private Observer<ExecutorState> executorStateObserver;

  @Before
  public void setUp() throws Exception {
    when(gateway.getState()).thenReturn(Flowable.never());
    executorStateUseCase = new ExecutorStateUseCaseImpl(gateway, executorStateObserver);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея статус исполнителя.
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayForAuth() throws Exception {
    // Действие:
    executorStateUseCase.loadStatus().test();

    // Результат:
    verify(gateway, only()).getState();
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
    when(gateway.getState()).thenReturn(Flowable.error(new NoNetworkException()));

    // Результат:
    executorStateUseCase.loadStatus().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом.
   *
   * @throws Exception error
   */
  @Test
  public void answerComplete() throws Exception {
    // Действие:
    when(gateway.getState()).thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Результат:
    executorStateUseCase.loadStatus().test().assertComplete();
  }

  /* Проверяем работу с публикатором состояния исполнителя */

  /**
   * Должен опубликовать статус "Смена закрыта".
   *
   * @throws Exception error
   */
  @Test
  public void publishShiftClosed() throws Exception {
    // Дано:
    when(gateway.getState()).thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));

    // Действие:
    executorStateUseCase.loadStatus().test();

    // Результат:
    verify(executorStateObserver, only()).onNext(ExecutorState.SHIFT_CLOSED);
  }

  /**
   * Должен опубликовать статус "Смена открыта".
   *
   * @throws Exception error
   */
  @Test
  public void publishShiftOpened() throws Exception {
    // Дано:
    when(gateway.getState()).thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Действие:
    executorStateUseCase.loadStatus().test();

    // Результат:
    verify(executorStateObserver, only()).onNext(ExecutorState.SHIFT_OPENED);
  }

  /**
   * Должен опубликовать статус "онлайн".
   *
   * @throws Exception error
   */
  @Test
  public void publishOnline() throws Exception {
    // Дано:
    when(gateway.getState()).thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    executorStateUseCase.loadStatus().test();

    // Результат:
    verify(executorStateObserver, only()).onNext(ExecutorState.ONLINE);
  }
}