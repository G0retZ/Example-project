package com.fasten.executor_driver.interactor;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.GeoLocation;
import com.fasten.executor_driver.gateway.GeoTrackingGatewayImpl;
import io.reactivex.Completable;
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
public class GeoTrackingGatewayTest {

  private GeoTrackingGateway geoTrackingGateway;

  @Mock
  private StompClient stompClient;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    geoTrackingGateway = new GeoTrackingGatewayImpl(stompClient);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
    when(stompClient.lifecycle()).thenReturn(Observable.never());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToSendMessage() throws Exception {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Результат:
    verify(stompClient).isConnected();
    verify(stompClient)
        .send("/mobile/online", "{\"latitude\":1.0,\"longitude\":2.0,\"regDate\":3}");
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
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).lifecycle();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).connect();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP соединение, если он не соединен и не соединяется,
   * подписаться на событие соединения, и отправить сообщение после соединения.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToConnectAndToSendMessageAfterConnect() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
    when(stompClient.lifecycle()).thenReturn(publishSubject);

    // Действие:
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();
    publishSubject.onNext(new LifecycleEvent(Type.OPENED));

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).lifecycle();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).connect();
    inOrder.verify(stompClient)
        .send("/mobile/online", "{\"latitude\":1.0,\"longitude\":2.0,\"regDate\":3}");
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен отписаться у клиента STOMP от событий соединения, если он не соединен и не соединяется,
   * и после соединения.
   *
   * @throws Exception error
   */
  @Test
  public void unSubscribeStompClientLifeCycleAfterConnect() throws Exception {
    // Дано:
    PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
    when(stompClient.lifecycle()).thenReturn(publishSubject);

    // Действие:
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();
    publishSubject.onNext(new LifecycleEvent(Type.OPENED));

    // Результат:
    assertFalse(publishSubject.hasObservers());
  }

  /**
   * Должен запросить у клиента STOMP соединение, если он не соединен и не соединяется,
   * подписаться на событие соединения, но не отправлять сообщение если соединение было закрыто.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToConnectAndDoNotAskToSendMessageAfterDisconnect() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
    when(stompClient.lifecycle()).thenReturn(publishSubject);

    // Действие:
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();
    publishSubject.onNext(new LifecycleEvent(Type.CLOSED));

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).lifecycle();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).connect();
    verifyNoMoreInteractions(stompClient);
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
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();
    publishSubject.onNext(new LifecycleEvent(Type.CLOSED));

    // Результат:
    assertFalse(publishSubject.hasObservers());
  }

  /**
   * Должен запросить у клиента STOMP соединение, если он не соединен и не соединяется,
   * подписаться на событие соединения, но не отправлять сообщение если соединение провалилось.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToConnectAndDoNotAskToSendMessageAfterConnectionFailed()
      throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
    when(stompClient.lifecycle()).thenReturn(publishSubject);

    // Действие:
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();
    publishSubject.onNext(new LifecycleEvent(Type.ERROR));

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).lifecycle();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).connect();
    verifyNoMoreInteractions(stompClient);
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
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();
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
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Результат:
    verify(stompClient).isConnected();
    verify(stompClient).lifecycle();
    verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP подписаться на событие соединения, если он не соединен и
   * соединяется, и отправить сообщение после соединения.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToSendMessageAfterConnect() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
    when(stompClient.lifecycle()).thenReturn(publishSubject);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();
    publishSubject.onNext(new LifecycleEvent(Type.OPENED));

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).lifecycle();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient)
        .send("/mobile/online", "{\"latitude\":1.0,\"longitude\":2.0,\"regDate\":3}");
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
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();
    publishSubject.onNext(new LifecycleEvent(Type.OPENED));

    // Результат:
    assertFalse(publishSubject.hasObservers());
  }

  /**
   * Должен запросить у клиента STOMP подписаться на событие соединения, если он не соединен и
   * соединяется, но не отправлять сообщение если соединение было закрыто.
   *
   * @throws Exception error
   */
  @Test
  public void doNotAskToSendMessageAfterDisconnect() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
    when(stompClient.lifecycle()).thenReturn(publishSubject);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();
    publishSubject.onNext(new LifecycleEvent(Type.CLOSED));

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).lifecycle();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
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
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();
    publishSubject.onNext(new LifecycleEvent(Type.CLOSED));

    // Результат:
    assertFalse(publishSubject.hasObservers());
  }

  /**
   * Должен запросить у клиента STOMP подписаться на событие соединения, если он не соединен и
   * соединяется, но не отправлять сообщение если соединение провалилось.
   *
   * @throws Exception error
   */
  @Test
  public void doNotAskToSendMessageAfterConnectionFailed()
      throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
    when(stompClient.lifecycle()).thenReturn(publishSubject);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();
    publishSubject.onNext(new LifecycleEvent(Type.ERROR));

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).lifecycle();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
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
    geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();
    publishSubject.onNext(new LifecycleEvent(Type.ERROR));

    // Результат:
    assertFalse(publishSubject.hasObservers());
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на попытку отправки сообщения */

  /**
   * Должен ответить успехом, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerSuccessIfConnected() throws Exception {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Результат:
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerErrorIfConnected() throws Exception {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Результат:
    testObserver.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом, если он не соединен и не соединяется, после соединения.
   *
   * @throws Exception error
   */
  @Test
  public void answerSuccessAfterConnected() throws Exception {
    // Дано:
    when(stompClient.lifecycle()).thenReturn(Observable.just((new LifecycleEvent(Type.OPENED))));
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Результат:
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется, после соединения.
   *
   * @throws Exception error
   */
  @Test
  public void answerErrorAfterConnected() throws Exception {
    // Дано:
    when(stompClient.lifecycle()).thenReturn(Observable.just((new LifecycleEvent(Type.OPENED))));
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Результат:
    testObserver.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется, если соединение было закрыто.
   *
   * @throws Exception error
   */
  @Test
  public void answerErrorAfterDisconnected() throws Exception {
    // Дано:
    when(stompClient.lifecycle()).thenReturn(Observable.just((new LifecycleEvent(Type.CLOSED))));

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Результат:
    testObserver.assertError(ConnectionClosedException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется, если соединение провалилось.
   *
   * @throws Exception error
   */
  @Test
  public void answerErrorAfterFailed() throws Exception {
    // Дано:
    when(stompClient.lifecycle())
        .thenReturn(Observable.just((new LifecycleEvent(Type.ERROR, new NoNetworkException()))));

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Результат:
    testObserver.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом, если он не соединен и соединяется, после соединения.
   *
   * @throws Exception error
   */
  @Test
  public void ifConnectingAnswerSuccessAfterConnected() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.lifecycle()).thenReturn(Observable.just((new LifecycleEvent(Type.OPENED))));
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Результат:
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он не соединен и соединяется, после соединения.
   *
   * @throws Exception error
   */
  @Test
  public void ifConnectingAnswerErrorAfterConnected() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.lifecycle()).thenReturn(Observable.just((new LifecycleEvent(Type.OPENED))));
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Результат:
    testObserver.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и соединяется, если соединение было закрыто.
   *
   * @throws Exception error
   */
  @Test
  public void ifConnectingAnswerErrorAfterDisconnected() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.lifecycle()).thenReturn(Observable.just((new LifecycleEvent(Type.CLOSED))));

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Результат:
    testObserver.assertError(ConnectionClosedException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и соединяется, если соединение провалилось.
   *
   * @throws Exception error
   */
  @Test
  public void ifConnectingAnswerErrorAfterFailed() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.lifecycle())
        .thenReturn(Observable.just((new LifecycleEvent(Type.ERROR, new NoNetworkException()))));

    // Действие:
    TestObserver<Void> testObserver =
        geoTrackingGateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Результат:
    testObserver.assertError(NoNetworkException.class);
  }
}