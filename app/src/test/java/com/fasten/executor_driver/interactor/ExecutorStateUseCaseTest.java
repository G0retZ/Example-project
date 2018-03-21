package com.fasten.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Observer;
import io.reactivex.Single;
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
    when(gateway.getState()).thenReturn(Single.never());
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
    when(gateway.getState()).thenReturn(Single.error(new NoNetworkException()));

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
    when(gateway.getState()).thenReturn(Single.just(ExecutorState.ONLINE));

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
    when(gateway.getState()).thenReturn(Single.just(ExecutorState.SHIFT_CLOSED));

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
    when(gateway.getState()).thenReturn(Single.just(ExecutorState.SHIFT_OPENED));

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
    when(gateway.getState()).thenReturn(Single.just(ExecutorState.ONLINE));

    // Действие:
    executorStateUseCase.loadStatus().test();

    // Результат:
    verify(executorStateObserver, only()).onNext(ExecutorState.ONLINE);
  }

  /**
   * Должен опубликовать статус "на пути с к точке погрузки".
   *
   * @throws Exception error
   */
  @Test
  public void publishApproachingLoadingPoint() throws Exception {
    // Дано:
    when(gateway.getState()).thenReturn(Single.just(ExecutorState.APPROACHING_LOADING_POINT));

    // Действие:
    executorStateUseCase.loadStatus().test();

    // Результат:
    verify(executorStateObserver, only()).onNext(ExecutorState.APPROACHING_LOADING_POINT);
  }

  /**
   * Должен опубликовать статус "погрузка".
   *
   * @throws Exception error
   */
  @Test
  public void publishLoading() throws Exception {
    // Дано:
    when(gateway.getState()).thenReturn(Single.just(ExecutorState.LOADING));

    // Действие:
    executorStateUseCase.loadStatus().test();

    // Результат:
    verify(executorStateObserver, only()).onNext(ExecutorState.LOADING);
  }

  /**
   * Должен опубликовать статус "на пути с к точке разгрузки".
   *
   * @throws Exception error
   */
  @Test
  public void publishApproachingUnloadingPoint() throws Exception {
    // Дано:
    when(gateway.getState()).thenReturn(Single.just(ExecutorState.APPROACHING_UNLOADING_POINT));

    // Действие:
    executorStateUseCase.loadStatus().test();

    // Результат:
    verify(executorStateObserver, only()).onNext(ExecutorState.APPROACHING_UNLOADING_POINT);
  }

  /**
   * Должен опубликовать статус "разгрузка".
   *
   * @throws Exception error
   */
  @Test
  public void publishUnloading() throws Exception {
    // Дано:
    when(gateway.getState()).thenReturn(Single.just(ExecutorState.UNLOADING));

    // Действие:
    executorStateUseCase.loadStatus().test();

    // Результат:
    verify(executorStateObserver, only()).onNext(ExecutorState.UNLOADING);
  }
}