package com.fasten.executor_driver.interactor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.gateway.CurrentCostPollingGatewayImpl;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.gateway.Mapper;
import com.fasten.executor_driver.utils.Pair;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class CurrentCostPollingGatewayTest {

  private CurrentCostPollingGateway gateway;
  @Mock
  private StompClient stompClient;
  @Mock
  private Mapper<String, Pair<Long, Long>> mapper;
  @Captor
  private ArgumentCaptor<StompMessage> stompMessageCaptor;

  private TestScheduler testScheduler;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    ExecutorState.MOVING_TO_CLIENT.setData(null);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.never());
    when(stompClient.sendAfterConnection(any(StompMessage.class)))
        .thenReturn(Completable.complete());
    gateway = new CurrentCostPollingGatewayImpl(stompClient, mapper);
  }

  /* Проверяем работу с клиентом STOMP на получение таймингов */

  /**
   * Должен запросить у клиента STOMP таймеры заказа, если он соединен и не соединяется.
   */
  @Test
  public void askStompClientForTimers() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    gateway.startPolling("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Не должен просить у клиента STOMP соединение, если он не соединен и не соединяется.
   */
  @Test
  public void doNotAskStompClientToConnectOrForTimers() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);

    // Действие:
    gateway.startPolling("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP таймеры заказа, если он не соединен и соединяется.
   */
  @Test
  public void askStompClientForTimersIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    gateway.startPolling("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP отправку ACK сразу, если он соединен и не соединяется.
   */
  @Test
  public void askStompClientToSendAckForTimersIfConnected() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Arrays.asList(
                    new StompHeader("OverPackage", "1"),
                    new StompHeader("subscription", "subs"),
                    new StompHeader("message-id", "mess")
                ),
                "\n"
            )
        ));

    // Действие:
    gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    inOrder.verify(stompClient).sendAfterConnection(stompMessageCaptor.capture());
    assertEquals(stompMessageCaptor.getValue().getStompCommand(), "ACK");
    assertEquals(stompMessageCaptor.getValue().findHeader("subscription"), "subs");
    assertEquals(stompMessageCaptor.getValue().findHeader("message-id"), "mess");
    assertEquals(stompMessageCaptor.getValue().getPayload(), "");
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP отправку ACK сразу, если он не соединен и соединяется.
   */
  @Test
  public void askStompClientToSendAckForTimersIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Arrays.asList(
                    new StompHeader("OverPackage", "1"),
                    new StompHeader("subscription", "subs"),
                    new StompHeader("message-id", "mess")
                ),
                "\n"
            )
        ));

    // Действие:
    gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    inOrder.verify(stompClient).sendAfterConnection(stompMessageCaptor.capture());
    assertEquals(stompMessageCaptor.getValue().getStompCommand(), "ACK");
    assertEquals(stompMessageCaptor.getValue().findHeader("subscription"), "subs");
    assertEquals(stompMessageCaptor.getValue().findHeader("message-id"), "mess");
    assertEquals(stompMessageCaptor.getValue().getPayload(), "");
    verifyNoMoreInteractions(stompClient);
  }

  /* Проверяем работу с маппером */

  /**
   * Не должен трогать маппер, если сообщение без нужных заголовков.
   */
  @Test
  public void doNotTouchMapperIfWrongHeader() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage("MESSAGE", null, "SHIFT"),
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("Type", "State")),
                "SHIFT"
            )
        ));

    // Действие:
    gateway.startPolling("1234567890").test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Не должен трогать маппер, если сообщение с заголовком OverPackage = 0.
   */
  @Test
  public void doNotTouchMapperForOverPackageHeader0() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(
                    new StompHeader("OverPackage", "0")
                ),
                "\n"
            )
        ));

    // Действие:
    gateway.startPolling("1234567890").test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить маппинг если сообщение с заголовком OverPackage = 1.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForOverPackageHeader1() throws Exception {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(
                    new StompHeader("OverPackage", "1")
                ),
                "\n"
            )
        ));

    // Действие:
    gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    verify(mapper, only()).map("\n");
  }

  /* Проверяем работу с клиентом STOMP на отправку пинга */

  /**
   * Не должен просить у клиента STOMP отправку пинга.
   */
  @Test
  public void doNotAskStompClientToSendPollPing() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(
                    new StompHeader("OverPackage", "")
                ),
                "\n"
            )
        ));

    // Действие:
    gateway.startPolling("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен просить у клиента STOMP отправку ACK и пинга сразу, если пакет истек.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToSendPollPingImmediately() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Arrays.asList(
                    new StompHeader("OverPackage", "1"),
                    new StompHeader("subscription", "subs"),
                    new StompHeader("message-id", "mess")
                ),
                "\n"
            )
        ));
    when(mapper.map(anyString())).thenReturn(new Pair<>(0L, 30_000L));

    // Действие:
    gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    inOrder.verify(stompClient).sendAfterConnection(stompMessageCaptor.capture());
    assertEquals(stompMessageCaptor.getValue().getStompCommand(), "ACK");
    assertEquals(stompMessageCaptor.getValue().findHeader("subscription"), "subs");
    assertEquals(stompMessageCaptor.getValue().findHeader("message-id"), "mess");
    assertEquals(stompMessageCaptor.getValue().getPayload(), "");
    inOrder.verify(stompClient).send("/mobile/retrieveOverPackage", "\"\"");
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP отправку ACK сразу и пинга после 10 секунд.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToSendPollPing10SecondsLater() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Arrays.asList(
                    new StompHeader("OverPackage", "1"),
                    new StompHeader("subscription", "subs"),
                    new StompHeader("message-id", "mess")
                ),
                "\n"
            )
        ));
    when(mapper.map(anyString())).thenReturn(new Pair<>(10_000L, 30_000L));

    // Действие:
    gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(9, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    inOrder.verify(stompClient).sendAfterConnection(stompMessageCaptor.capture());
    assertEquals(stompMessageCaptor.getValue().getStompCommand(), "ACK");
    assertEquals(stompMessageCaptor.getValue().findHeader("subscription"), "subs");
    assertEquals(stompMessageCaptor.getValue().findHeader("message-id"), "mess");
    assertEquals(stompMessageCaptor.getValue().getPayload(), "");
    verifyNoMoreInteractions(stompClient);
    testScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
    inOrder.verify(stompClient).send("/mobile/retrieveOverPackage", "\"\"");
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP отправку ACK сразу и набора пингов после 200 секунд.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToSendPollPings() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Arrays.asList(
                    new StompHeader("OverPackage", "1"),
                    new StompHeader("subscription", "subs"),
                    new StompHeader("message-id", "mess")
                ),
                "\n"
            )
        ));
    when(mapper.map(anyString())).thenReturn(new Pair<>(10_000L, 30_000L));

    // Действие:
    gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(220, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    inOrder.verify(stompClient).sendAfterConnection(stompMessageCaptor.capture());
    assertEquals(stompMessageCaptor.getValue().getStompCommand(), "ACK");
    assertEquals(stompMessageCaptor.getValue().findHeader("subscription"), "subs");
    assertEquals(stompMessageCaptor.getValue().findHeader("message-id"), "mess");
    assertEquals(stompMessageCaptor.getValue().getPayload(), "");
    inOrder.verify(stompClient, times(8)).send("/mobile/retrieveOverPackage", "\"\"");
    verifyNoMoreInteractions(stompClient);
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем результаты отправки сообщений серверу */

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется.
   */
  @Test
  public void answerConnectionErrorIfNotConnectedAndNotConnecting() {
    // Действие:
    TestObserver<Void> testSubscriber =
        gateway.startPolling("1234567890").test();

    // Результат:
    testSubscriber.assertError(ConnectionClosedException.class);
  }

  /**
   * Должен игнорировать ошибку, если он соединен и не соединяется.
   */
  @Test
  public void answerWithErrorIfSubscribed() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.error(new IOException()));

    // Действие:
    TestObserver<Void> testSubscriber =
        gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    testSubscriber.assertError(IOException.class);
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен игнорировать сообщение без нужных заголовков.
   */
  @Test
  public void ignoreWrongHeaderAndWaitForCompletionOrInterruption() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.just(
            new StompMessage("MESSAGE", null, "SHIFT"),
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("Type", "State")),
                "SHIFT"
            )
        ).concatWith(Flowable.never())
    );

    // Действие:
    TestObserver<Void> testSubscriber =
        gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой для сообщения с заголовком OverPackage = 1, если маппинг не удался.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForOverPackageHeader1() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(any());
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("OverPackage", "1")),
                null
            )
        ).concatWith(Flowable.never())
    );

    // Действие:
    TestObserver<Void> testSubscriber =
        gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен завершить для сообщения с заголовком OverPackage = 0.
   */
  @Test
  public void answerCompleteForOverPackageHeader0() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("OverPackage", "0")),
                null
            )
        ).concatWith(Flowable.never())
    );

    // Действие:
    TestObserver<Void> testSubscriber =
        gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
  }

  /**
   * Должен ждать завершения для сообщения с заголовком OverPackage = 1.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithTimersForOverPackageHeaderIfConnected() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(new Pair<>(10_000L, 30_000L));
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("OverPackage", "1")),
                "\n"
            )
        ).concatWith(Flowable.never())
    );

    // Действие:
    TestObserver<Void> testSubscriber =
        gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен ждать завершения для сообщения с заголовком OverPackage = 1 и спустя 10 минут.
   *
   * @throws Exception error
   */
  @Test
  public void waitForCompletionOrInterruption() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(new Pair<>(10_000L, 30_000L));
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("OverPackage", "1")),
                "\n"
            )
        ).concatWith(Flowable.never())
    );
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(10, TimeUnit.MINUTES);

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой сети сразу, если отправка пинг поллинга не удалась.
   *
   * @throws Exception error
   */
  @Test
  public void answerPollPingNetworkError() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(new Pair<>(0L, 30_000L));
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("OverPackage", "1")),
                "\n"
            )
        ).concatWith(Flowable.never())
    );
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(IllegalArgumentException::new));

    // Действие:
    TestObserver<Void> testObserver = gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    testObserver.assertError(IllegalArgumentException.class);
    testObserver.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой сети через 10 секунд, если отправка пинг поллинга не удалась.
   *
   * @throws Exception error
   */
  @Test
  public void answerPollPingNetworkError10SecondsLater() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(new Pair<>(10_000L, 30_000L));
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL)).thenReturn(
        Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("OverPackage", "1")),
                "\n"
            )
        ).concatWith(Flowable.never())
    );
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(IllegalArgumentException::new));

    // Действие:
    TestObserver<Void> testObserver = gateway.startPolling("1234567890").test();
    testScheduler.advanceTimeBy(9, TimeUnit.SECONDS);

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
    testScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
    testObserver.assertError(IllegalArgumentException.class);
    testObserver.assertNotComplete();
  }
}