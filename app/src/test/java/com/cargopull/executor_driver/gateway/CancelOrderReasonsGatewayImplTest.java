package com.cargopull.executor_driver.gateway;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.interactor.CancelOrderReasonsGateway;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class CancelOrderReasonsGatewayImplTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private CancelOrderReasonsGateway gateway;

  @Mock
  private TopicListener topicListener;
  @Mock
  private Mapper<StompMessage, List<CancelOrderReason>> mapper;
  @Mock
  private StompMessage stompMessage;
  @Mock
  private CancelOrderReason cancelOrderReason;
  @Mock
  private CancelOrderReason cancelOrderReason1;
  @Mock
  private CancelOrderReason cancelOrderReason2;

  @Before
  public void setUp() {
    gateway = new CancelOrderReasonsGatewayImpl(topicListener, mapper);
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.never());
  }

  /* Проверяем работу с слушателем сокета */

  /**
   * Должен запросить у слушателя топика причины для отказа.
   */
  @Test
  public void askWebTopicListenerForCancelReason() {
    // Действие:
    gateway.loadCancelOrderReasons().test();

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
    gateway.loadCancelOrderReasons().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить маппинг если сообщение с заголовком CancelReason.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForCancelReasonHeader() throws Exception {
    // Дано:
    when(stompMessage.findHeader("CancelReason")).thenReturn("");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    gateway.loadCancelOrderReasons().test();

    // Результат:
    verify(mapper, only()).map(any());
  }

  /* Проверяем результаты обработки сообщений от сервера по причинам для отказа */

  /**
   * Должен игнорировать сообщение без нужных заголовков.
   */
  @Test
  public void ignoreWrongHeader() {
    // Дано:
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        gateway.loadCancelOrderReasons().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой для сообщение с заголовком CancelReason.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForCancelReasonHeaderIfConnected() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(any());
    when(stompMessage.findHeader("CancelReason")).thenReturn("");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        gateway.loadCancelOrderReasons().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть причины для отказа для сообщения с заголовком CancelReason.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithCancelReasonsForCancelReasonHeader() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
    );
    when(stompMessage.findHeader("CancelReason")).thenReturn("payload");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        gateway.loadCancelOrderReasons().test();

    // Результат:
    testSubscriber.assertValue(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
    );
  }
}