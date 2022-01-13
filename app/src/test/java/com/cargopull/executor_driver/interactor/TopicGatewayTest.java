package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.backend.web.TopicListener;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.gateway.Mapper;
import com.cargopull.executor_driver.gateway.TopicGateway;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;

import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(Parameterized.class)
public class TopicGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();
  private final boolean withDefault;
  @Rule
  public MockitoRule rule = MockitoJUnit.rule();
  private CommonGateway<String> gateway;
  @Mock
  private TopicListener topicListener;
  @Mock
  private Mapper<StompFrame, String> mapper;
  @Mock
  private Predicate<StompFrame> filter;
  @Mock
  private StompFrame stompFrame;

  // Each parameter should be placed as an argument here
  // Every time runner triggers, it will pass the arguments
  // from parameters we defined in primeNumbers() method

  public TopicGatewayTest(boolean conditions) {
    withDefault = conditions;
  }

  @Parameterized.Parameters
  public static Iterable<Boolean> primeConditions() {
    return Arrays.asList(false, true);
  }

  @Before
  public void setUp() {
    if (withDefault) {
      gateway = new TopicGateway<>(topicListener, filter, mapper, "defaultValue");
    } else {
      gateway = new TopicGateway<>(topicListener, filter, mapper);
    }
    when(topicListener.getMessages()).thenReturn(Flowable.never());
  }

  /* Проверяем работу с слушателем сокета */

  /**
   * Должен запросить у слушателя топика текущий заказ.
   */
  @Test
  public void askExecutorStateUseCaseForStatusUpdates() {
    // Action:
    gateway.getData().test().isDisposed();

    // Effect:
    verify(topicListener, only()).getMessages();
  }

  /* Проверяем работу с фильтром */

  /**
   * Не должен трогать фильтр, если данных еще нет.
   */
  @Test
  public void doNotTouchFilterIfNoDataYet() {
    // Action:
    gateway.getData().test().isDisposed();

    // Effect:
    verifyNoInteractions(filter);
  }

  /**
   * Должен запросить проверку.
   *
   * @throws Exception error
   */
  @Test
  public void askFilterForCheck() throws Exception {
    // Given:
    when(topicListener.getMessages()).thenReturn(Flowable.just(stompFrame));

    // Action:
    gateway.getData().test().isDisposed();

    // Effect:
    verify(filter, only()).test(stompFrame);
  }

  /* Проверяем работу с маппером */

  /**
   * Не должен трогать маппер, если фильтр не дал добро.
   */
  @Test
  public void doNotTouchMapperIfFiltered() {
    // Given:
    when(topicListener.getMessages()).thenReturn(Flowable.just(stompFrame));

    // Action:
    gateway.getData().test().isDisposed();

    // Effect:
    verifyNoInteractions(mapper);
  }

  /**
   * Должен запросить маппинг если, если фильтр дал добро.
   *
   * @throws Exception error
   */
  @Test
  public void askMapperForForDataMapping() throws Exception {
    // Given:
    when(filter.test(stompFrame)).thenReturn(true);
    when(topicListener.getMessages()).thenReturn(Flowable.just(stompFrame));

    // Action:
    gateway.getData().test().isDisposed();

    // Effect:
    verify(mapper, only()).map(stompFrame);
  }

  /* Проверяем результаты обработки сообщений от сервера */

  /**
   * Должен игнорировать отфильтрованные сообщение, выдав только значение по-умолчанию, если оно
   * задано.
   */
  @Test
  public void ignoreFilteredMessages() {
    // Given:
    when(topicListener.getMessages()).thenReturn(Flowable.just(stompFrame));

    // Action:
    TestSubscriber<String> testSubscriber = gateway.getData().test();

    // Effect:
    if (withDefault) {
      testSubscriber.assertValue("defaultValue");
    } else {
      testSubscriber.assertNoValues();
    }
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой маппинга, выдав только значение по-умолчанию, если оно задано.
   *
   * @throws Exception error
   */
  @Test
  public void answerNoStringAvailableForNoData() throws Exception {
    // Given:
    doThrow(new DataMappingException()).when(mapper).map(stompFrame);
    when(filter.test(stompFrame)).thenReturn(true);
    when(topicListener.getMessages()).thenReturn(Flowable.just(stompFrame));

    // Action:
    TestSubscriber<String> testSubscriber = gateway.getData().test();

    // Effect:
    testSubscriber.assertError(DataMappingException.class);
    if (withDefault) {
      testSubscriber.assertValue("defaultValue");
    } else {
      testSubscriber.assertNoValues();
    }
  }

  /**
   * Должен вернуть данные, после значения по-умолчанию, если оно задано.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithData() throws Exception {
    // Given:
    when(mapper.map(stompFrame)).thenReturn("Data");
    when(filter.test(stompFrame)).thenReturn(true);
    when(topicListener.getMessages()).thenReturn(Flowable.just(stompFrame));

    // Action:
    TestSubscriber<String> testSubscriber = gateway.getData().test();

    // Effect:
    if (withDefault) {
      testSubscriber.assertValues("defaultValue", "Data");
    } else {
      testSubscriber.assertValue("Data");
    }
    testSubscriber.assertNoErrors();
  }
}