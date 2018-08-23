package com.cargopull.executor_driver.gateway;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import com.cargopull.executor_driver.interactor.ExecutorBalanceGateway;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorBalanceGatewayImplTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private ExecutorBalanceGateway gateway;

  @Mock
  private TopicListener topicListener;
  @Mock
  private Mapper<StompMessage, ExecutorBalance> mapper;
  @Mock
  private StompMessage stompMessage;
  @Mock
  private ExecutorBalance executorBalance;

  @Before
  public void setUp() {
    gateway = new ExecutorBalanceGatewayImpl(topicListener, mapper);
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.never());
  }

  /* Проверяем работу с слушателем сокета */

  /**
   * Должен запросить у слушателя топика баланс исполнителя.
   */
  @Test
  public void askWebTopicListenerForExecutorBalance() {
    // Действие:
    gateway.loadExecutorBalance().test();

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
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    gateway.loadExecutorBalance().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить маппинг если сообщение с заголовком Balance.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForBalanceHeader() throws Exception {
    // Дано:
    when(stompMessage.findHeader("Balance")).thenReturn("");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    gateway.loadExecutorBalance().test();

    // Результат:
    verify(mapper, only()).map(any());
  }

  /* Проверяем результаты обработки сообщений от сервера по балансу */

  /**
   * Должен игнорировать сообщение без нужных заголовков.
   */
  @Test
  public void ignoreWrongHeader() {
    // Дано:
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber = gateway.loadExecutorBalance().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой для сообщения с заголовком Balance.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForBalanceHeader() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(any());
    when(stompMessage.findHeader("Balance")).thenReturn("");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber = gateway.loadExecutorBalance().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть баланс исполнителя для сообщения с заголовком Balance.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithExecutorBalanceForBalanceHeader() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(executorBalance);
    when(stompMessage.findHeader("Balance")).thenReturn("payload");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber = gateway.loadExecutorBalance().test();

    // Результат:
    testSubscriber.assertValue(executorBalance);
    testSubscriber.assertNoErrors();
  }
}