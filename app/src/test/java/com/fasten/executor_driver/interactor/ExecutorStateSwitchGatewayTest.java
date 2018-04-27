package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.gateway.ExecutorStateSwitchGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompClient;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateSwitchGatewayTest {

  private ExecutorStateSwitchGateway geoTrackingGateway;

  @Mock
  private StompClient stompClient;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    geoTrackingGateway = new ExecutorStateSwitchGatewayImpl(stompClient);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку, если он соединен и не соединяется.
   */
  @Test
  public void askStompClientToSendMessage() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    geoTrackingGateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    verify(stompClient).isConnected();
    verify(stompClient).send("/mobile/status", "\"ONLINE\"");
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Не должен просить у клиента STOMP соединение, если он не соединен и не соединяется.
   */
  @Test
  public void doNotAskStompClientToConnectOrSendIfNotConnected() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);

    // Действие:
    geoTrackingGateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP отправку, если он не соединен и соединяется.
   */
  @Test
  public void askStompClientToSendMessageIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    geoTrackingGateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    verify(stompClient).isConnected();
    verify(stompClient).isConnecting();
    verify(stompClient).send("/mobile/status", "\"ONLINE\"");
    verifyNoMoreInteractions(stompClient);
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на попытку отправки сообщения */

  /**
   * Должен ответить успехом, если он соединен и не соединяется.
   */
  @Test
  public void answerSuccessIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он соединен и не соединяется.
   */
  @Test
  public void answerErrorIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    testObserver.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется.
   */
  @Test
  public void answerErrorIfNotConnectedAndNotConnecting() {
    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    testObserver.assertError(ConnectionClosedException.class);
  }

  /**
   * Должен ответить успехом, если он не соединен и соединяется.
   */
  @Test
  public void answerSuccessIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он не соединен и соединяется.
   */
  @Test
  public void answerErrorIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new ConnectionClosedException()));

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    testObserver.assertError(ConnectionClosedException.class);
  }
}