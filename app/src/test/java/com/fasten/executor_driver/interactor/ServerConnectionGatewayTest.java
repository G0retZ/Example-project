package com.fasten.executor_driver.interactor;

import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.gateway.ServerConnectionGatewayImpl;
import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
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
public class ServerConnectionGatewayTest {

  private ServerConnectionGateway gateway;

  @Mock
  private StompClient stompClient;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    gateway = new ServerConnectionGatewayImpl(stompClient);
    when(stompClient.lifecycle()).thenReturn(Observable.never());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP подписку на событие соединения, проверить статус соединения.
   * Не должен запрашивать соединение, если уже соединен.
   */
  @Test
  public void askStompClientToSubscribeForLifecycleIfConnected() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    gateway.openSocket().test();

    // Результат:
    inOrder.verify(stompClient).lifecycle();
    inOrder.verify(stompClient).isConnected();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP подписку на событие соединения, проверить статуса соединения.
   * Не должен запрашивать соединение, если уже соединяется.
   */
  @Test
  public void askStompClientToSubscribeForLifecycleIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    gateway.openSocket().test();

    // Результат:
    inOrder.verify(stompClient).lifecycle();
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP подписку на событие соединения, проверить статус соединения.
   * Должен запросить соединение, если не соединен и не соединяется.
   */
  @Test
  public void askStompClientToSubscribeForLifecycleAndToConnect() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);

    // Действие:
    gateway.openSocket().test();

    // Результат:
    inOrder.verify(stompClient).lifecycle();
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).reconnect();
    verifyNoMoreInteractions(stompClient);
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на попытку открытия сокета сообщения */

  /**
   * Должен ответить успехом, если он соединен и не соединяется.
   */
  @Test
  public void answerOpenSuccessIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    TestSubscriber<Boolean> testSubscriber = gateway.openSocket().test();

    // Результат:
    testSubscriber.assertNotComplete();
    testSubscriber.assertValue(true);
  }

  /**
   * Не должен ничем отвечать, если он не соединен и соединяется.
   */
  @Test
  public void answerNothingIfNotConnected() {
    // Действие:
    TestSubscriber<Boolean> testSubscriber = gateway.openSocket().test();

    // Результат:
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен ответить успехом после соединения.
   */
  @Test
  public void answerOpenSuccessAfterConnected() {
    // Дано:
    when(stompClient.lifecycle()).thenReturn(
        Observable.just((new LifecycleEvent(Type.OPENED))).concatWith(Observable.never())
    );

    // Действие:
    TestSubscriber<Boolean> testSubscriber = gateway.openSocket().test();

    // Результат:
    testSubscriber.assertNotComplete();
    testSubscriber.assertValue(true);
  }

  /**
   * Должен ответить False + ошибкой, если соединение было закрыто.
   */
  @Test
  public void answerWithFalseAndErrorAfterConnectionClosed() {
    // Дано:
    when(stompClient.lifecycle()).thenReturn(
        Observable.just((new LifecycleEvent(Type.CLOSED))).concatWith(Observable.never())
    );

    // Действие:
    TestSubscriber<Boolean> testSubscriber = gateway.openSocket().test();

    // Результат:
    testSubscriber.assertError(InterruptedException.class);
    testSubscriber.assertValue(false);
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен ответить False + ошибкой, если соединение провалилось.
   */
  @Test
  public void answerWithFalseAndErrorAfterFailed() {
    // Дано:
    when(stompClient.lifecycle()).thenReturn(
        Observable.just((new LifecycleEvent(Type.ERROR, new NoNetworkException())))
            .concatWith(Observable.never())
    );

    // Действие:
    TestSubscriber<Boolean> testSubscriber = gateway.openSocket().test();

    // Результат:
    testSubscriber.assertError(NoNetworkException.class);
    testSubscriber.assertValue(false);
    testSubscriber.assertNotComplete();
  }
}