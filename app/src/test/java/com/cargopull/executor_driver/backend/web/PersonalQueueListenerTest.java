package com.cargopull.executor_driver.backend.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.backend.stomp.StompClient;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.interactor.CommonGateway;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class PersonalQueueListenerTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private PersonalQueueListener queueListener;

  @Mock
  private StompClient stompClient;
  @Mock
  private StompFrame stompFrame;
  @Mock
  private AppSettingsService appSettings;
  @Mock
  private CommonGateway<Boolean> networkConnectionGateway;
  @Mock
  private Action action;
  private TestScheduler testScheduler;


  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
    queueListener = new PersonalQueueListener(stompClient, networkConnectionGateway, appSettings);
    when(networkConnectionGateway.getData()).thenReturn(Flowable.never());
    when(stompClient.listenToDestination(anyString(), eq(2000), eq(2F)))
        .thenReturn(Flowable.never());
  }

  /**
   * Должен запросить у публикатора состояние сети только раз.
   */
  @Test
  public void askPublisherForNetworkState() {
    // Action:
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();

    // Effect:
    verify(networkConnectionGateway, only()).getData();
  }

  /**
   * Не должен просить у публикатора логин исполнителя.
   */
  @Test
  public void doNotAskLoginPublisherForLogin() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(false));

    // Action:
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();

    // Effect:
    verifyNoInteractions(appSettings);
  }

  /**
   * Должен запросить у публикатора логин исполнителя только раз.
   */
  @Test
  public void askLoginPublisherForLogin() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));

    // Action:
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();

    // Effect:
    verify(appSettings, only()).getData("authorizationLogin");
  }

  /**
   * Должен перезапросить у клиента STOMP сообщения из топика после ошибки.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void doNotAskLoginPublisherForLoginOnError() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F)).thenReturn(
        Flowable.error(Exception::new),
        Flowable.error(NoNetworkException::new),
        Flowable.error(ConnectionClosedException::new),
        Flowable.never()
    );

    // Action:
    queueListener.getMessages().test().isDisposed();

    // Effect:
    verify(appSettings, only()).getData("authorizationLogin");
  }

  /**
   * Должен перезапросить у клиента STOMP сообщения из топика после завершения.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void doNotAskLoginPublisherForLoginOnComplete() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F)).thenReturn(
        Flowable.empty(),
        Flowable.empty(),
        Flowable.empty(),
        Flowable.never()
    );

    // Action:
    queueListener.getMessages().test().isDisposed();

    // Effect:
    verify(appSettings, only()).getData("authorizationLogin");
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP подписку на топики.
   */
  @Test
  public void askStompClientForTopicSubscription() {
    // Given:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(networkConnectionGateway.getData()).thenReturn(
        Flowable.fromArray(true, false, true, false, false, true, true, false, true, false, true)
            .concatWith(Flowable.never())
    );
    when(appSettings.getData("authorizationLogin"))
        .thenReturn("1234567890", "0987654321", null, "123454321", "09876567890");

    // Action:
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();

    // Effect:
    inOrder.verify(stompClient).listenToDestination("/queue/1234567890", 2000, 2F);
    inOrder.verify(stompClient).listenToDestination("/queue/0987654321", 2000, 2F);
    inOrder.verify(stompClient).listenToDestination("/queue/", 2000, 2F);
    inOrder.verify(stompClient).listenToDestination("/queue/123454321", 2000, 2F);
    inOrder.verify(stompClient).listenToDestination("/queue/09876567890", 2000, 2F);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен перезапросить у клиента STOMP сообщения из топика после ошибки.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void askStompClientForTopicMessagesOnError() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F)).thenReturn(
        Flowable.error(Exception::new),
        Flowable.error(NoNetworkException::new),
        Flowable.error(ConnectionClosedException::new),
        Flowable.never()
    );

    // Action:
    queueListener.getMessages().test().isDisposed();
    testScheduler.advanceTimeBy(5, TimeUnit.MINUTES);

    // Effect:
    verify(stompClient, times(4)).listenToDestination("/queue/1234567890", 2000, 2F);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Не должен перезапросить у клиента STOMP сообщения из топика после ошибки версии.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void doNotAskStompClientForTopicMessagesOnDeprecatedVersionError() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F)).thenReturn(
        Flowable.error(DeprecatedVersionException::new),
        Flowable.error(NoNetworkException::new),
        Flowable.error(ConnectionClosedException::new),
        Flowable.never()
    );

    // Action:
    queueListener.getMessages().test().isDisposed();
    testScheduler.advanceTimeBy(5, TimeUnit.MINUTES);

    // Effect:
    verify(stompClient, only()).listenToDestination("/queue/1234567890", 2000, 2F);
  }

  /**
   * Не должен перезапросить у клиента STOMP сообщения из топика после ошибки авторизации.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void doNotAskStompClientForTopicMessagesOnAuthorizationError() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F)).thenReturn(
        Flowable.error(AuthorizationException::new),
        Flowable.error(NoNetworkException::new),
        Flowable.error(ConnectionClosedException::new),
        Flowable.never()
    );

    // Action:
    queueListener.getMessages().test().isDisposed();
    testScheduler.advanceTimeBy(5, TimeUnit.MINUTES);

    // Effect:
    verify(stompClient, only()).listenToDestination("/queue/1234567890", 2000, 2F);
  }

  /**
   * Должен перезапросить у клиента STOMP сообщения из топика после сброса ошибки авторизации.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void askStompClientForTopicMessagesOnAuthorizationErrorReset() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F)).thenReturn(
        Flowable.error(AuthorizationException::new),
        Flowable.error(NoNetworkException::new),
        Flowable.error(ConnectionClosedException::new),
        Flowable.never()
    );

    // Action:
    queueListener.getMessages().test().isDisposed();
    queueListener.restart();
    testScheduler.advanceTimeBy(5, TimeUnit.MINUTES);

    // Effect:
    verify(stompClient, times(4)).listenToDestination("/queue/1234567890", 2000, 2F);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен перезапросить у клиента STOMP сообщения из топика после завершения.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void askStompClientForTopicMessagesOnComplete() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F)).thenReturn(
        Flowable.empty(),
        Flowable.empty(),
        Flowable.empty(),
        Flowable.never()
    );

    // Action:
    queueListener.getMessages().test().isDisposed();

    // Effect:
    verify(stompClient, only()).listenToDestination("/queue/1234567890", 2000, 2F);
  }

  /**
   * Должен отписаться у клиента STOMP от подписок на старые топики.
   */
  @Test
  public void unSubscribeFromPreviousRequestsToStompClient() throws Exception {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(
        Flowable.fromArray(true, false, true, false, true, false, true).concatWith(Flowable.never())
    );
    when(stompClient.listenToDestination(anyString(), eq(2000), eq(2F)))
        .thenReturn(Flowable.<StompFrame>never().doOnCancel(action));

    // Action:
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();
    queueListener.getMessages().test().isDisposed();

    // Effect:
    verify(action, times(3)).run();
  }

  /**
   * Должен отписаться у клиента STOMP от топика, когда все подписчики ушли.
   */
  @Test
  public void unSubscribeFromTopicIfNoMoreSubscribers() throws Exception {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F))
        .thenReturn(Flowable.<StompFrame>never().doOnCancel(action));

    // Action:
    TestSubscriber<StompFrame> testSubscriber = queueListener.getMessages().test();
    TestSubscriber<StompFrame> testSubscriber1 = queueListener.getMessages().test();
    TestSubscriber<StompFrame> testSubscriber2 = queueListener.getMessages().test();
    TestSubscriber<StompFrame> testSubscriber3 = queueListener.getMessages().test();

    // Effect:
    testSubscriber.dispose();
    testSubscriber1.dispose();
    testSubscriber2.dispose();
    verifyNoInteractions(action);
    testSubscriber3.dispose();
    verify(action, only()).run();
  }

  /* Проверяем результаты обработки сообщений от сервера */

  /**
   * Должен вернуть сообщение.
   */
  @Test
  public void answerWithStompFrame() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F))
        .thenReturn(Flowable.just(stompFrame).concatWith(Flowable.never()));

    // Action:
    TestSubscriber<StompFrame> testSubscriber = queueListener.getMessages().test();

    // Effect:
    testSubscriber.assertValue(stompFrame);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен игнорировать ошибки.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void ignoreErrors() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F)).thenReturn(
        Flowable.error(Exception::new),
        Flowable.error(NoNetworkException::new),
        Flowable.error(ConnectionClosedException::new),
        Flowable.never()
    );

    // Action:
    TestSubscriber<StompFrame> testSubscriber = queueListener.getMessages().test();

    // Effect:
    testSubscriber.assertNoErrors();
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен выдать ошибку апдейта версии.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithDeprecatedVersionError() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F)).thenReturn(
        Flowable.error(DeprecatedVersionException::new),
        Flowable.never()
    );

    // Action:
    TestSubscriber<StompFrame> testSubscriber = queueListener.getMessages().test();

    // Effect:
    testSubscriber.assertError(DeprecatedVersionException.class);
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
  }

  /**
   * Не должен давать ошибки.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void doNotAnswer() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F)).thenReturn(
        Flowable.error(AuthorizationException::new),
        Flowable.never()
    );

    // Action:
    TestSubscriber<StompFrame> testSubscriber = queueListener.getMessages().test();

    // Effect:
    testSubscriber.assertNoErrors();
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть сообщение.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithStompFrameAfterErrors() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F)).thenReturn(
        Flowable.error(Exception::new),
        Flowable.error(NoNetworkException::new),
        Flowable.error(ConnectionClosedException::new),
        Flowable.just(stompFrame).concatWith(Flowable.never())
    );

    // Action:
    TestSubscriber<StompFrame> testSubscriber = queueListener.getMessages().test();
    testScheduler.advanceTimeBy(5, TimeUnit.MINUTES);

    // Effect:
    testSubscriber.assertValue(stompFrame);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть сообщение.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithStompFrameAfterCompletions() {
    // Given:
    when(networkConnectionGateway.getData()).thenReturn(Flowable.<Boolean>never().startWith(true));
    when(appSettings.getData("authorizationLogin")).thenReturn("1234567890");
    when(stompClient.listenToDestination("/queue/1234567890", 2000, 2F)).thenReturn(
        Flowable.empty(),
        Flowable.empty(),
        Flowable.empty(),
        Flowable.just(stompFrame).concatWith(Flowable.never())
    );

    // Action:
    TestSubscriber<StompFrame> testSubscriber = queueListener.getMessages().test();
    testScheduler.advanceTimeBy(6, TimeUnit.MINUTES);

    // Effect:
    testSubscriber.assertValue(stompFrame);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }
}