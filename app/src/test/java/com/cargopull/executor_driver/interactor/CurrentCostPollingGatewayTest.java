package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.gateway.CurrentCostPollingGatewayImpl;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.gateway.Mapper;
import com.cargopull.executor_driver.utils.Pair;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class CurrentCostPollingGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private CurrentCostPollingGateway gateway;

  @Mock
  private TopicListener topicListener;
  @Mock
  private StompClient stompClient;
  @Mock
  private StompMessage stompMessage;
  @Mock
  private StompMessage stompMessage1;
  @Mock
  private Mapper<StompMessage, Pair<Long, Long>> mapper;

  private TestScheduler testScheduler;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.never());
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
    gateway = new CurrentCostPollingGatewayImpl(topicListener, stompClient, mapper);
  }

  /* Проверяем работу с слушателем топика */

  /**
   * Должен запросить у слушателя топика таймеры заказа.
   */
  @Test
  public void askWebTopicListenerForOverPackage() {
    // Действие:
    gateway.startPolling().test().isDisposed();

    // Результат:
    verify(topicListener, only()).getAcknowledgedMessages();
  }

  /* Проверяем работу с маппером */

  /**
   * Не должен трогать маппер, если сообщение без нужных заголовков.
   */
  @Test
  public void doNotTouchMapperIfWrongHeader() {
    // Дано:
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage).concatWith(Flowable.never()));

    // Действие:
    gateway.startPolling().test().isDisposed();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Не должен трогать маппер, если сообщение без нужных заголовков.
   */
  @Test
  public void doNotTouchMapperForOverPackageHeader0() {
    // Дано:
    when(stompMessage.findHeader("OverPackage")).thenReturn("0");
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage).concatWith(Flowable.never()));

    // Действие:
    gateway.startPolling().test().isDisposed();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить маппинг если сообщение с заголовком OverPackage.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForOverPackageHeader1() throws Exception {
    // Дано:
    when(stompMessage.findHeader("OverPackage")).thenReturn("1");
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage).concatWith(Flowable.never()));

    // Действие:
    gateway.startPolling().test().isDisposed();

    // Результат:
    verify(mapper, only()).map(stompMessage);
  }

  /* Проверяем работу с клиентом STOMP на отправку пинга */

  /**
   * Не должен просить у клиента STOMP отправку пинга, если заголовок не тот.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void doNotAskStompClientToSendPollPing() {
    // Дано:
    when(stompMessage.findHeader("OverPackage")).thenReturn("");
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage), Flowable.never());

    // Действие:
    gateway.startPolling().test().isDisposed();

    // Результат:
    verifyZeroInteractions(stompClient);
  }

  /**
   * Должен просить у клиента STOMP отправку ACK и пинга сразу, если пакет истек.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToSendPollPingImmediately() throws Exception {
    // Дано:
    when(stompMessage.findHeader("OverPackage")).thenReturn("1");
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage).concatWith(Flowable.never()));
    when(mapper.map(stompMessage)).thenReturn(new Pair<>(0L, 30_000L));

    // Действие:
    gateway.startPolling().test().isDisposed();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    verify(stompClient, only()).send("/mobile/retrieveOverPackage", "\"\"");
  }

  /**
   * Должен запросить у клиента STOMP отправку пинга после 10 минут.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToSendPollPing10SecondsLater() throws Exception {
    // Дано:
    when(stompMessage.findHeader("OverPackage")).thenReturn("1");
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage).concatWith(Flowable.never()));
    when(mapper.map(stompMessage)).thenReturn(new Pair<>(600_000L, 30_000L));

    // Действие:
    gateway.startPolling().test().isDisposed();
    testScheduler.advanceTimeBy(9, TimeUnit.MINUTES);

    // Результат:
    verifyZeroInteractions(stompClient);
    testScheduler.advanceTimeBy(1, TimeUnit.MINUTES);
    verify(stompClient, only()).send("/mobile/retrieveOverPackage", "\"\"");
  }

  /**
   * Должен запросить у клиента STOMP отправку набора пингов после 15 минут.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToSendPollPings() throws Exception {
    // Дано:
    when(stompMessage.findHeader("OverPackage")).thenReturn("1");
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage).concatWith(Flowable.never()));
    when(mapper.map(stompMessage)).thenReturn(new Pair<>(600_000L, 30_000L));

    // Действие:
    gateway.startPolling().test().isDisposed();
    testScheduler.advanceTimeBy(15, TimeUnit.MINUTES);

    // Результат:
    verify(stompClient, times(11)).send("/mobile/retrieveOverPackage", "\"\"");
    verifyNoMoreInteractions(stompClient);
  }

  /* Проверяем завершение и возобновление поллинга */

  /**
   * Не должен просить у клиента STOMP отправку набора пингов после остановки поллинга.
   *
   * @throws Exception error
   */
  @Test
  public void doNotAskStompClientToSendPollPingsIfPollingShouldStop() throws Exception {
    // Дано:
    PublishSubject<StompMessage> publishSubject = PublishSubject.create();
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(stompMessage.findHeader("OverPackage")).thenReturn("1");
    when(stompMessage1.findHeader("OverPackage")).thenReturn("0");
    when(mapper.map(stompMessage)).thenReturn(new Pair<>(600_000L, 30_000L));

    // Действие:
    gateway.startPolling().test().isDisposed();

    // Результат:
    publishSubject.onNext(stompMessage);
    testScheduler.advanceTimeBy(15, TimeUnit.MINUTES);
    verify(stompClient, times(11)).send("/mobile/retrieveOverPackage", "\"\"");
    verifyNoMoreInteractions(stompClient);
    publishSubject.onNext(stompMessage1);
    testScheduler.advanceTimeBy(15, TimeUnit.MINUTES);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен просить у клиента STOMP отправку набора пингов снова после возобновления поллинга.
   *
   * @throws Exception error
   */
  @Test
  public void askStompClientToSendPollPingsIfPollingShouldResume() throws Exception {
    // Дано:
    PublishSubject<StompMessage> publishSubject = PublishSubject.create();
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(stompMessage.findHeader("OverPackage")).thenReturn("1");
    when(stompMessage1.findHeader("OverPackage")).thenReturn("0");
    when(mapper.map(stompMessage)).thenReturn(new Pair<>(600_000L, 30_000L));

    // Действие:
    gateway.startPolling().test().isDisposed();

    // Результат:
    publishSubject.onNext(stompMessage);
    testScheduler.advanceTimeBy(15, TimeUnit.MINUTES);
    verify(stompClient, times(11)).send("/mobile/retrieveOverPackage", "\"\"");
    verifyNoMoreInteractions(stompClient);
    publishSubject.onNext(stompMessage1);
    testScheduler.advanceTimeBy(15, TimeUnit.MINUTES);
    verifyNoMoreInteractions(stompClient);
    publishSubject.onNext(stompMessage);
    testScheduler.advanceTimeBy(15, TimeUnit.MINUTES);
    verify(stompClient, times(22)).send("/mobile/retrieveOverPackage", "\"\"");
    verifyNoMoreInteractions(stompClient);
  }

  /* Проверяем результаты отправки сообщений серверу */

  /**
   * Должен игнорировать сообщение без нужных заголовков.
   */
  @Test
  public void ignoreWrongHeaderAndWaitForCompletionOrInterruption() {
    // Дано:
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage).concatWith(Flowable.never()));

    // Действие:
    TestObserver<Void> testObserver = gateway.startPolling().test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой для сообщения с заголовком OverPackage = 1, если маппинг не удался.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForOverPackageHeader1() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(stompMessage);
    when(stompMessage.findHeader("OverPackage")).thenReturn("1");
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage).concatWith(Flowable.never()));

    // Действие:
    TestObserver<Void> testObserver = gateway.startPolling().test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    testObserver.assertError(DataMappingException.class);
    testObserver.assertNotComplete();
  }

  /**
   * Не должен завершать для сообщения с заголовком OverPackage = 0.
   */
  @Test
  public void answerCompleteForOverPackageHeader0() {
    // Дано:
    when(stompMessage.findHeader("OverPackage")).thenReturn("0");
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage).concatWith(Flowable.never()));

    // Действие:
    TestObserver<Void> testObserver = gateway.startPolling().test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
  }

  /**
   * Должен ждать завершения для сообщения с заголовком OverPackage = 1.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithTimersForOverPackageHeaderIfConnected() throws Exception {
    // Дано:
    when(mapper.map(stompMessage)).thenReturn(new Pair<>(10_000L, 30_000L));
    when(stompMessage.findHeader("OverPackage")).thenReturn("1");
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage).concatWith(Flowable.never()));

    // Действие:
    TestObserver<Void> testObserver = gateway.startPolling().test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
  }

  /**
   * Должен ждать завершения для сообщения с заголовком OverPackage = 1 и спустя 10 минут.
   *
   * @throws Exception error
   */
  @Test
  public void waitForCompletionOrInterruption() throws Exception {
    // Дано:
    when(mapper.map(stompMessage)).thenReturn(new Pair<>(600_000L, 30_000L));
    when(stompMessage.findHeader("OverPackage")).thenReturn("1");
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage).concatWith(Flowable.never()));
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = gateway.startPolling().test();
    testScheduler.advanceTimeBy(10, TimeUnit.MINUTES);

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
  }

  /**
   * Должен игнорировать ошибку отправки пинга поллинга.
   *
   * @throws Exception error
   */
  @Test
  public void ignorePollPingNetworkError() throws Exception {
    // Дано:
    when(mapper.map(stompMessage)).thenReturn(new Pair<>(0L, 30_000L));
    when(stompMessage.findHeader("OverPackage")).thenReturn("1");
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage).concatWith(Flowable.never()));
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(IllegalArgumentException::new));

    // Действие:
    TestObserver<Void> testObserver = gateway.startPolling().test();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
  }
}