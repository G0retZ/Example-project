package com.fasten.executor_driver.interactor;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.gateway.SocketGatewayImpl;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.LifecycleEvent;
import ua.naiksoftware.stomp.LifecycleEvent.Type;
import ua.naiksoftware.stomp.client.StompClient;

@RunWith(MockitoJUnitRunner.class)
public class SocketGatewayTest {

  private SocketGateway socketGateway;

  @Mock
  private StompClient stompClient;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    socketGateway = new SocketGatewayImpl(stompClient);
    when(stompClient.lifecycle()).thenReturn(Observable.never());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Не должен просить у клиента STOMP соединения, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void doNotAskStompClientToConnect() throws Exception {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    socketGateway.openSocket().test();

    // Результат:
    verify(stompClient).isConnected();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP соединение, если он не соединен и не соединяется, и
   * подписаться на событие соединения.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToConnectAndSubscribeForLifecycle() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);

    // Действие:
    socketGateway.openSocket().test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).lifecycle();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).connect();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен отписаться у клиента STOMP от событий соединения, если он не соединен и не соединяется,
   * и после соединения.
   *
   * @throws Exception error
   */
  @Test
  public void unSubscribeFromStompClientLifeCycleAfterConnect() throws Exception {
    // Дано:
    PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
    when(stompClient.lifecycle()).thenReturn(publishSubject);

    // Действие:
    socketGateway.openSocket().test();
    publishSubject.onNext(new LifecycleEvent(Type.OPENED));

    // Результат:
    assertFalse(publishSubject.hasObservers());
  }

  /**
   * Должен отписаться у клиента STOMP от событий соединения, если он не соединен и не соединяется,
   * и соединение было закрыто.
   *
   * @throws Exception error
   */
  @Test
  public void unSubscribeStompClientLifeCycleAfterDisconnect() throws Exception {
    // Дано:
    PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
    when(stompClient.lifecycle()).thenReturn(publishSubject);

    // Действие:
    socketGateway.openSocket().test();
    publishSubject.onNext(new LifecycleEvent(Type.CLOSED));

    // Результат:
    assertFalse(publishSubject.hasObservers());
  }

  /**
   * Должен отписаться у клиента STOMP от событий соединения, если он не соединен и не соединяется,
   * и соединение провалилось.
   *
   * @throws Exception error
   */
  @Test
  public void unSubscribeStompClientLifeCycleAfterFailed() throws Exception {
    // Дано:
    PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
    when(stompClient.lifecycle()).thenReturn(publishSubject);

    // Действие:
    socketGateway.openSocket().test();
    publishSubject.onNext(new LifecycleEvent(Type.ERROR));

    // Результат:
    assertFalse(publishSubject.hasObservers());
  }

  /**
   * Должен запросить у клиента STOMP подписку на событие соединения, если он не соединен и
   * соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToSubscribeForLifecycle() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    socketGateway.openSocket().test();

    // Результат:
    verify(stompClient).isConnected();
    verify(stompClient).lifecycle();
    verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен отписаться у клиента STOMP от событий соединения, если он не соединен и соединяется,
   * и после соединения.
   *
   * @throws Exception error
   */
  @Test
  public void unSubscribeConnectingStompClientLifeCycleAfterConnect() throws Exception {
    // Дано:
    PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
    when(stompClient.lifecycle()).thenReturn(publishSubject);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    socketGateway.openSocket().test();
    publishSubject.onNext(new LifecycleEvent(Type.OPENED));

    // Результат:
    assertFalse(publishSubject.hasObservers());
  }

  /**
   * Должен отписаться у клиента STOMP от событий соединения, если он не соединен и соединяется,
   * и соединение было закрыто.
   *
   * @throws Exception error
   */
  @Test
  public void unSubscribeConnectingStompClientLifeCycleAfterDisconnect() throws Exception {
    // Дано:
    PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
    when(stompClient.lifecycle()).thenReturn(publishSubject);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    socketGateway.openSocket().test();
    publishSubject.onNext(new LifecycleEvent(Type.CLOSED));

    // Результат:
    assertFalse(publishSubject.hasObservers());
  }

  /**
   * Должен отписаться у клиента STOMP от событий соединения, если он не соединен и соединяется,
   * и соединение провалилось.
   *
   * @throws Exception error
   */
  @Test
  public void unSubscribeConnectingStompClientLifeCycleAfterFailed() throws Exception {
    // Дано:
    PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
    when(stompClient.lifecycle()).thenReturn(publishSubject);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    socketGateway.openSocket().test();
    publishSubject.onNext(new LifecycleEvent(Type.ERROR));

    // Результат:
    assertFalse(publishSubject.hasObservers());
  }

  /**
   * Должен просить у клиента STOMP разединения, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToDisconnectIfConnected() throws Exception {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    socketGateway.closeSocket();

    // Результат:
    verify(stompClient).isConnected();
    verify(stompClient).disconnect();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Не Должен запросить у клиента STOMP разединения, если он не соединен и соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToDisconnectIfConnecting() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    socketGateway.closeSocket();

    // Результат:
    verify(stompClient).isConnected();
    verify(stompClient).isConnecting();
    verify(stompClient).disconnect();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Не должен просить у клиента STOMP разединения, если он не соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void doNotAskStompClientToDisconnect() throws Exception {
    // Действие:
    socketGateway.closeSocket();

    // Результат:
    verify(stompClient).isConnected();
    verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на попытку открытия сокета сообщения */

  /**
   * Должен ответить успехом, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerOpenSuccessIfConnected() throws Exception {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    TestObserver<Void> testObserver = socketGateway.openSocket().test();

    // Результат:
    testObserver.assertComplete();
  }

  /**
   * Должен ответить успехом, если он не соединен и не соединяется, после соединения.
   *
   * @throws Exception error
   */
  @Test
  public void answerOpenSuccessAfterConnected() throws Exception {
    // Дано:
    when(stompClient.lifecycle()).thenReturn(Observable.just((new LifecycleEvent(Type.OPENED))));

    // Действие:
    TestObserver<Void> testObserver = socketGateway.openSocket().test();

    // Результат:
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется, если соединение было закрыто.
   *
   * @throws Exception error
   */
  @Test
  public void answerOpenErrorAfterDisconnected() throws Exception {
    // Дано:
    when(stompClient.lifecycle()).thenReturn(Observable.just((new LifecycleEvent(Type.CLOSED))));

    // Действие:
    TestObserver<Void> testObserver = socketGateway.openSocket().test();

    // Результат:
    testObserver.assertError(ConnectionClosedException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется, если соединение провалилось.
   *
   * @throws Exception error
   */
  @Test
  public void answerOpenErrorAfterFailed() throws Exception {
    // Дано:
    when(stompClient.lifecycle())
        .thenReturn(Observable.just((new LifecycleEvent(Type.ERROR, new NoNetworkException()))));

    // Действие:
    TestObserver<Void> testObserver = socketGateway.openSocket().test();

    // Результат:
    testObserver.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом, если он не соединен и соединяется, после соединения.
   *
   * @throws Exception error
   */
  @Test
  public void ifConnectingAnswerOpenSuccessAfterConnected() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.lifecycle()).thenReturn(Observable.just((new LifecycleEvent(Type.OPENED))));

    // Действие:
    TestObserver<Void> testObserver = socketGateway.openSocket().test();

    // Результат:
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он не соединен и соединяется, если соединение было закрыто.
   *
   * @throws Exception error
   */
  @Test
  public void ifConnectingAnswerOpenErrorAfterDisconnected() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.lifecycle()).thenReturn(Observable.just((new LifecycleEvent(Type.CLOSED))));

    // Действие:
    TestObserver<Void> testObserver = socketGateway.openSocket().test();

    // Результат:
    testObserver.assertError(ConnectionClosedException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и соединяется, если соединение провалилось.
   *
   * @throws Exception error
   */
  @Test
  public void ifConnectingAnswerOpenErrorAfterFailed() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.lifecycle())
        .thenReturn(Observable.just((new LifecycleEvent(Type.ERROR, new NoNetworkException()))));

    // Действие:
    TestObserver<Void> testObserver = socketGateway.openSocket().test();

    // Результат:
    testObserver.assertError(NoNetworkException.class);
  }
}