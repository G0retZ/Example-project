package com.cargopull.executor_driver.gateway;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.interactor.CommonGateway;
import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class TopicGatewayWithDefaultImplTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private CommonGateway<String> gateway;
  @Mock
  private TopicListener topicListener;
  @Mock
  private Mapper<StompMessage, String> mapper;
  @Mock
  private Predicate<StompMessage> filter;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    gateway = new TopicGatewayWithDefaultImpl<>(topicListener, mapper, filter, "defaultValue");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.never());
  }

  /* Проверяем работу с слушателем сокета */

  /**
   * Должен запросить у слушателя топика текущий заказ.
   */
  @Test
  public void askExecutorStateUseCaseForStatusUpdates() {
    // Действие:
    gateway.getData().test();

    // Результат:
    verify(topicListener, only()).getAcknowledgedMessages();
  }

  /* Проверяем работу с фильтром */

  /**
   * Не должен трогать фильтр, если данных еще нет.
   */
  @Test
  public void doNotTouchFilterIfNoDataYet() {
    // Действие:
    gateway.getData().test();

    // Результат:
    verifyZeroInteractions(filter);
  }

  /**
   * Должен запросить проверку.
   *
   * @throws Exception error
   */
  @Test
  public void askFilterForCheck() throws Exception {
    // Дано:
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    gateway.getData().test();

    // Результат:
    verify(filter, only()).test(stompMessage);
  }

  /* Проверяем работу с маппером */

  /**
   * Не должен трогать маппер, если фильтр не дал добро.
   */
  @Test
  public void doNotTouchMapperIfFiltered() {
    // Дано:
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    gateway.getData().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить маппинг если, если фильтр дал добро.
   *
   * @throws Exception error
   */
  @Test
  public void askMapperForForDataMapping() throws Exception {
    // Дано:
    when(filter.test(stompMessage)).thenReturn(true);
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    gateway.getData().test();

    // Результат:
    verify(mapper, only()).map(stompMessage);
  }

  /* Проверяем результаты обработки сообщений от сервера */

  /**
   * Должен игнорировать отфильтрованные сообщение, выдав только значение по-умолчанию.
   */
  @Test
  public void ignoreFilteredMessages() {
    // Дано:
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<String> testSubscriber = gateway.getData().test();

    // Результат:
    testSubscriber.assertValue("defaultValue");
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой маппинга, выдав только значение по-умолчанию.
   *
   * @throws Exception error
   */
  @Test
  public void answerNoStringAvailableForNoData() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(stompMessage);
    when(filter.test(stompMessage)).thenReturn(true);
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<String> testSubscriber = gateway.getData().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertValue("defaultValue");
  }

  /**
   * Должен вернуть данные после значения по-умолчанию.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithData() throws Exception {
    // Дано:
    when(mapper.map(stompMessage)).thenReturn("Data");
    when(filter.test(stompMessage)).thenReturn(true);
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<String> testSubscriber = gateway.getData().test();

    // Результат:
    testSubscriber.assertValues("defaultValue", "Data");
    testSubscriber.assertNoErrors();
  }
}