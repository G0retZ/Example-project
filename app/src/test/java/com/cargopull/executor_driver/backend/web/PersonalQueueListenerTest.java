package com.cargopull.executor_driver.backend.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.interactor.DataReceiver;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class PersonalQueueListenerTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private TopicListener queueListener;

  @Mock
  private StompClient stompClient;
  @Mock
  private StompMessage stompMessage;
  @Mock
  private DataReceiver<String> loginReceiver;
  @Captor
  private ArgumentCaptor<StompMessage> stompMessageCaptor;
  @Mock
  private Action action;

  @Before
  public void setUp() {
    queueListener = new PersonalQueueListener(stompClient, loginReceiver);
    when(loginReceiver.get()).thenReturn(Observable.never());
    when(stompClient.topic(anyString(), eq(StompClient.ACK_CLIENT_INDIVIDUAL)))
        .thenReturn(Flowable.never());
    when(stompClient.send(any(StompMessage.class)))
        .thenReturn(Completable.complete());
  }

  /**
   * Должен запросить у публикатора логин исполнителя только раз.
   */
  @Test
  public void askLoginPublisherForLogin() {
    // Действие:
    queueListener.getAcknowledgedMessages().test().isDisposed();
    queueListener.getAcknowledgedMessages().test().isDisposed();
    queueListener.getAcknowledgedMessages().test().isDisposed();
    queueListener.getAcknowledgedMessages().test().isDisposed();

    // Результат:
    verify(loginReceiver, only()).get();
  }

  /**
   * Должен перезапросить у клиента STOMP сообщения из топика после ошибки.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void doNotAskLoginPublisherForLoginOnError() {
    // Дано:
    when(loginReceiver.get())
        .thenReturn(Observable.just("1234567890").concatWith(Observable.never()));
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.error(Exception::new),
        Flowable.error(NoNetworkException::new),
        Flowable.error(ConnectionClosedException::new),
        Flowable.never()
    );

    // Действие:
    queueListener.getAcknowledgedMessages().test().isDisposed();

    // Результат:
    verify(loginReceiver, only()).get();
  }

  /**
   * Должен перезапросить у клиента STOMP сообщения из топика после завершения.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void doNotAskLoginPublisherForLoginOnComplete() {
    // Дано:
    when(loginReceiver.get())
        .thenReturn(Observable.just("1234567890").concatWith(Observable.never()));
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.empty(),
        Flowable.empty(),
        Flowable.empty(),
        Flowable.never()
    );

    // Действие:
    queueListener.getAcknowledgedMessages().test().isDisposed();

    // Результат:
    verify(loginReceiver, only()).get();
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP подписку на топики.
   */
  @Test
  public void askStompClientForTopicSubscription() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ).concatWith(Observable.never()));

    // Действие:
    queueListener.getAcknowledgedMessages().test().isDisposed();
    queueListener.getAcknowledgedMessages().test().isDisposed();
    queueListener.getAcknowledgedMessages().test().isDisposed();
    queueListener.getAcknowledgedMessages().test().isDisposed();

    // Результат:
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    inOrder.verify(stompClient).topic("/queue/0987654321", StompClient.ACK_CLIENT_INDIVIDUAL);
    inOrder.verify(stompClient).topic("/queue/123454321", StompClient.ACK_CLIENT_INDIVIDUAL);
    inOrder.verify(stompClient).topic("/queue/09876567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен перезапросить у клиента STOMP сообщения из топика после ошибки.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void askStompClientForTopicMessagesOnError() {
    // Дано:
    when(loginReceiver.get())
        .thenReturn(Observable.just("1234567890").concatWith(Observable.never()));
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.error(Exception::new),
        Flowable.error(NoNetworkException::new),
        Flowable.error(ConnectionClosedException::new),
        Flowable.never()
    );

    // Действие:
    queueListener.getAcknowledgedMessages().test().isDisposed();

    // Результат:
    verify(stompClient, times(4)).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен перезапросить у клиента STOMP сообщения из топика после завершения.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void askStompClientForTopicMessagesOnComplete() {
    // Дано:
    when(loginReceiver.get())
        .thenReturn(Observable.just("1234567890").concatWith(Observable.never()));
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.empty(),
        Flowable.empty(),
        Flowable.empty(),
        Flowable.never()
    );

    // Действие:
    queueListener.getAcknowledgedMessages().test().isDisposed();

    // Результат:
    verify(stompClient, times(4)).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен отписаться у клиента STOMP от подписок на старый топики.
   */
  @Test
  public void ubSubscribeFromPreviousRequestsToStompClient() throws Exception {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ).concatWith(Observable.never()));
    when(stompClient.topic(anyString(), eq(StompClient.ACK_CLIENT_INDIVIDUAL)))
        .thenReturn(Flowable.<StompMessage>never().doOnCancel(action));

    // Действие:
    queueListener.getAcknowledgedMessages().test().isDisposed();
    queueListener.getAcknowledgedMessages().test().isDisposed();
    queueListener.getAcknowledgedMessages().test().isDisposed();
    queueListener.getAcknowledgedMessages().test().isDisposed();

    // Результат:
    verify(action, times(3)).run();
  }

  /**
   * Должен отписаться у клиента STOMP от топика, когда все подписчики ушли.
   */
  @Test
  public void ubSubscribeFromTopicIfNoMoreSubscribers() throws Exception {
    // Дано:
    when(loginReceiver.get())
        .thenReturn(Observable.just("1234567890").concatWith(Observable.never()));
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.<StompMessage>never().doOnCancel(action));

    // Действие:
    TestSubscriber<StompMessage> testSubscriber = queueListener.getAcknowledgedMessages().test();
    TestSubscriber<StompMessage> testSubscriber1 = queueListener.getAcknowledgedMessages().test();
    TestSubscriber<StompMessage> testSubscriber2 = queueListener.getAcknowledgedMessages().test();
    TestSubscriber<StompMessage> testSubscriber3 = queueListener.getAcknowledgedMessages().test();

    // Результат:
    testSubscriber.dispose();
    testSubscriber1.dispose();
    testSubscriber2.dispose();
    verifyZeroInteractions(action);
    testSubscriber3.dispose();
    verify(action, only()).run();
  }

  /**
   * Должен запросить у клиента STOMP отправку ACK сразу после получения сообщения.
   */
  @Test
  public void askStompClientToSendAckForMessageImmediately() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompMessage.findHeader("subscription")).thenReturn("subs0", "subs1", "subs2");
    when(stompMessage.findHeader("message-id")).thenReturn("mess0", "mess1", "mess2");
    when(loginReceiver.get())
        .thenReturn(Observable.just("1234567890").concatWith(Observable.never()));
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(
            Flowable.just(stompMessage, stompMessage, stompMessage).concatWith(Flowable.never()));

    // Действие:
    queueListener.getAcknowledgedMessages().test().isDisposed();

    // Результат:
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    inOrder.verify(stompClient, times(3)).send(stompMessageCaptor.capture());
    assertEquals(stompMessageCaptor.getAllValues().get(0).getStompCommand(), "ACK");
    assertEquals(stompMessageCaptor.getAllValues().get(0).findHeader("subscription"), "subs0");
    assertEquals(stompMessageCaptor.getAllValues().get(0).findHeader("message-id"), "mess0");
    assertEquals(stompMessageCaptor.getAllValues().get(0).getPayload(), "");
    assertEquals(stompMessageCaptor.getAllValues().get(1).getStompCommand(), "ACK");
    assertEquals(stompMessageCaptor.getAllValues().get(1).findHeader("subscription"), "subs1");
    assertEquals(stompMessageCaptor.getAllValues().get(1).findHeader("message-id"), "mess1");
    assertEquals(stompMessageCaptor.getAllValues().get(1).getPayload(), "");
    assertEquals(stompMessageCaptor.getAllValues().get(2).getStompCommand(), "ACK");
    assertEquals(stompMessageCaptor.getAllValues().get(2).findHeader("subscription"), "subs2");
    assertEquals(stompMessageCaptor.getAllValues().get(2).findHeader("message-id"), "mess2");
    assertEquals(stompMessageCaptor.getAllValues().get(2).getPayload(), "");
    verifyNoMoreInteractions(stompClient);
  }

  /* Проверяем результаты обработки сообщений от сервера */

  /**
   * Должен вернуть сообщение.
   */
  @Test
  public void answerWithStompMessage() {
    // Дано:
    when(loginReceiver.get())
        .thenReturn(Observable.just("1234567890").concatWith(Observable.never()));
    when(stompMessage.findHeader("subscription")).thenReturn("subs");
    when(stompMessage.findHeader("message-id")).thenReturn("mess");
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(stompMessage).concatWith(Flowable.never()));

    // Действие:
    TestSubscriber<StompMessage> testSubscriber = queueListener.getAcknowledgedMessages().test();

    // Результат:
    testSubscriber.assertValue(stompMessage);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен игнорировать ошибки.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void ignoreErrors() {
    // Дано:
    when(loginReceiver.get())
        .thenReturn(Observable.just("1234567890").concatWith(Observable.never()));
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.error(Exception::new),
        Flowable.error(NoNetworkException::new),
        Flowable.error(ConnectionClosedException::new),
        Flowable.never()
    );

    // Действие:
    TestSubscriber<StompMessage> testSubscriber = queueListener.getAcknowledgedMessages().test();

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть сообщение.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithStompMessageAfterErrors() {
    // Дано:
    when(loginReceiver.get())
        .thenReturn(Observable.just("1234567890").concatWith(Observable.never()));
    when(stompMessage.findHeader("subscription")).thenReturn("subs");
    when(stompMessage.findHeader("message-id")).thenReturn("mess");
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.error(Exception::new),
        Flowable.error(NoNetworkException::new),
        Flowable.error(ConnectionClosedException::new),
        Flowable.just(stompMessage).concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<StompMessage> testSubscriber = queueListener.getAcknowledgedMessages().test();

    // Результат:
    testSubscriber.assertValue(stompMessage);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть сообщение.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithStompMessageAfterCompletions() {
    // Дано:
    when(loginReceiver.get())
        .thenReturn(Observable.just("1234567890").concatWith(Observable.never()));
    when(stompMessage.findHeader("subscription")).thenReturn("subs");
    when(stompMessage.findHeader("message-id")).thenReturn("mess");
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.empty(),
        Flowable.empty(),
        Flowable.empty(),
        Flowable.just(stompMessage).concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<StompMessage> testSubscriber = queueListener.getAcknowledgedMessages().test();

    // Результат:
    testSubscriber.assertValue(stompMessage);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }
}