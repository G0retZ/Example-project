package com.cargopull.executor_driver.gateway;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.interactor.ExecutorStateGateway;
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
public class ExecutorStateGatewayImplTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private ExecutorStateGateway gateway;
  @Mock
  private TopicListener topicListener;
  @Mock
  private Mapper<StompMessage, ExecutorState> mapper;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    gateway = new ExecutorStateGatewayImpl(topicListener, mapper);
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.never());
  }

  /* Проверяем работу с слушателем сокета */

  /**
   * Должен запросить у слушателя топика статусы.
   */
  @Test
  public void askWebTopicListenerForExecutorBalance() {
    // Действие:
    gateway.getState().test();

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
    gateway.getState().test();

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
    when(stompMessage.findHeader("Status")).thenReturn("");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    gateway.getState().test();

    // Результат:
    verify(mapper, only()).map(stompMessage);
  }

  /* Проверяем результаты обработки сообщений от сервера по статусам */

  /**
   * Должен игнорировать сообщение без нужных заголовков.
   */
  @Test
  public void ignoreWrongHeader() {
    // Дано:
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber = gateway.getState().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой для сообщения с заголовком Status.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForBalanceHeader() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(stompMessage);
    when(stompMessage.findHeader("Status")).thenReturn("");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber = gateway.getState().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть статус исполнителя для сообщения с заголовком Status.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithExecutorBalanceForBalanceHeader() throws Exception {
    // Дано:
    when(mapper.map(stompMessage)).thenReturn(ExecutorState.SHIFT_OPENED);
    when(stompMessage.findHeader("Status")).thenReturn("payload");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber = gateway.getState().test();

    // Результат:
    testSubscriber.assertValue(ExecutorState.SHIFT_OPENED);
    testSubscriber.assertNoErrors();
  }
}