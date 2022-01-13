package com.cargopull.executor_driver.gateway;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.interactor.CommonGateway;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.subscribers.TestSubscriber;

public class FcmGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();
  @Rule
  public MockitoRule rule = MockitoJUnit.rule();
  private CommonGateway<String> gateway;
  @Mock
  private Mapper<Map<String, String>, String> mapper;
  @Mock
  private Predicate<Map<String, String>> filter;
  @Mock
  private Map<String, String> dataMap;

  /* Проверяем работу с фильтром */

  /**
   * Не должен трогать фильтр, если данных еще нет.
   */
  @Test
  public void doNotTouchFilterIfNoDataYet() {
    // Given:
    gateway = new FcmGateway<>(Observable.never(), filter, mapper);

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
    gateway = new FcmGateway<>(Observable.just(dataMap), filter, mapper);

    // Action:
    gateway.getData().test().isDisposed();

    // Effect:
    verify(filter, only()).test(dataMap);
  }

  /* Проверяем работу с маппером */

  /**
   * Не должен трогать маппер, если фильтр не дал добро.
   */
  @Test
  public void doNotTouchMapperIfFiltered() {
    // Given:
    gateway = new FcmGateway<>(Observable.just(dataMap), filter, mapper);

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
    when(filter.test(dataMap)).thenReturn(true);
    gateway = new FcmGateway<>(Observable.just(dataMap), filter, mapper);

    // Action:
    gateway.getData().test().isDisposed();

    // Effect:
    verify(mapper, only()).map(dataMap);
  }

  /* Проверяем результаты обработки сообщений от сервера */

  /**
   * Должен игнорировать отфильтрованные сообщение, выдав только значение по-умолчанию, если оно
   * задано.
   */
  @Test
  public void ignoreFilteredMessages() {
    // Given:
    gateway = new FcmGateway<>(Observable.just(dataMap), filter, mapper);

    // Action:
    TestSubscriber<String> testSubscriber = gateway.getData().test();

    // Effect:
    testSubscriber.assertNoValues();
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
    doThrow(new DataMappingException()).when(mapper).map(dataMap);
    when(filter.test(dataMap)).thenReturn(true);
    gateway = new FcmGateway<>(Observable.just(dataMap), filter, mapper);

    // Action:
    TestSubscriber<String> testSubscriber = gateway.getData().test();

    // Effect:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть данные, после значения по-умолчанию, если оно задано.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithData() throws Exception {
    // Given:
    when(mapper.map(dataMap)).thenReturn("Data");
    when(filter.test(dataMap)).thenReturn(true);
    gateway = new FcmGateway<>(Observable.just(dataMap), filter, mapper);

    // Action:
    TestSubscriber<String> testSubscriber = gateway.getData().test();

    // Effect:
    testSubscriber.assertValue("Data");
    testSubscriber.assertNoErrors();
  }
}