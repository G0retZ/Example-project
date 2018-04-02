package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.subscribers.TestSubscriber;
import java.net.ConnectException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateUseCaseTest {

  private ExecutorStateUseCase executorStateUseCase;

  @Mock
  private ExecutorStateGateway gateway;

  @Mock
  private SocketGateway socketGateway;

  @Mock
  private DataReceiver<String> loginReceiver;

  @Mock
  private Action action;

  @Before
  public void setUp() {
    when(gateway.getState(anyString())).thenReturn(Flowable.never());
    when(loginReceiver.get()).thenReturn(Observable.never());
    when(socketGateway.openSocket()).thenReturn(Completable.never());
    executorStateUseCase = new ExecutorStateUseCaseImpl(gateway, socketGateway, loginReceiver);
  }

  /* Проверяем работу с гейтвеем сокета */

  /**
   * Должен запросить у гейтвея соединение.
   */
  @Test
  public void askSocketGatewayForConnection() {
    // Действие:
    executorStateUseCase.getExecutorStates().test();

    // Результат:
    verify(socketGateway, only()).openSocket();
  }

  /* Проверяем работу с публикатором логина */

  /**
   * Должен запросить у публикатора логин исполнителя.
   */
  @Test
  public void askLoginPublisherForLogin() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());

    // Действие:
    executorStateUseCase.getExecutorStates().test();

    // Результат:
    verify(loginReceiver, only()).get();
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея статус исполнителя.
   */
  @Test
  public void askGatewayForStatus() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(gateway);
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    executorStateUseCase.getExecutorStates().test();

    // Результат:
    inOrder.verify(gateway).getState("1234567890");
    inOrder.verify(gateway).getState("0987654321");
    inOrder.verify(gateway).getState("123454321");
    inOrder.verify(gateway).getState("09876567890");
    verifyNoMoreInteractions(gateway);
  }

  /**
   * Должен отписаться от предыдущих запросов статусов исполнителя.
   *
   * @throws Exception error
   */
  @Test
  public void ubSubscribeFromPreviousRequestsToGateway() throws Exception {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));
    when(gateway.getState(anyString()))
        .thenReturn(Flowable.<ExecutorState>never().doOnCancel(action));

    // Действие:
    executorStateUseCase.getExecutorStates().test();

    // Результат:
    verify(action, times(3)).run();
  }

  /**
   * Не должен запрпрашивать у гейтвея статус исполнителя.
   */
  @Test
  public void doNotAskGatewayForStatus() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.error(NoNetworkException::new));

    // Действие:
    executorStateUseCase.getExecutorStates().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть статусы.
   */
  @Test
  public void answerWithStatuses() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.getState("1234567890")).thenReturn(
        Flowable.just(ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED, ExecutorState.ONLINE)
    );

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber = executorStateUseCase.getExecutorStates().test();

    // Результат:
    testSubscriber
        .assertValues(ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED, ExecutorState.ONLINE);
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку, если открытие сокета обломалось.
   */
  @Test
  public void answerWithErrorIfSocketConnectionFailed() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.error(ConnectException::new));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber = executorStateUseCase.getExecutorStates().test();

    // Результат:
    testSubscriber.assertError(ConnectException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть ошибку, если была ошибка получения логина.
   */
  @Test
  public void answerWithErrorIfGetLoginFailed() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.error(ConnectException::new));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber = executorStateUseCase.getExecutorStates().test();

    // Результат:
    testSubscriber.assertError(ConnectException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть ошибку, если подписка обломалась.
   */
  @Test
  public void answerWithErrorIfSubscriptionFailed() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.getState("1234567890")).thenReturn(Flowable.error(ConnectException::new));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber = executorStateUseCase.getExecutorStates().test();

    // Результат:
    testSubscriber.assertError(ConnectException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен завершить получение статусов.
   */
  @Test
  public void answerComplete() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.getState("1234567890")).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber = executorStateUseCase.getExecutorStates().test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }
}