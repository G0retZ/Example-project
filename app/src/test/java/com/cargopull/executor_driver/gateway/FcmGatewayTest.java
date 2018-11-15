package com.cargopull.executor_driver.gateway;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.interactor.CommonGateway;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Map;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

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
    // Дано:
    gateway = new FcmGateway<>(Observable.never(), filter, mapper);

    // Действие:
    gateway.getData().test().isDisposed();

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
    gateway = new FcmGateway<>(Observable.just(dataMap), filter, mapper);

    // Действие:
    gateway.getData().test().isDisposed();

    // Результат:
    verify(filter, only()).test(dataMap);
  }

  /* Проверяем работу с маппером */

  /**
   * Не должен трогать маппер, если фильтр не дал добро.
   */
  @Test
  public void doNotTouchMapperIfFiltered() {
    // Дано:
    gateway = new FcmGateway<>(Observable.just(dataMap), filter, mapper);

    // Действие:
    gateway.getData().test().isDisposed();

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
    when(filter.test(dataMap)).thenReturn(true);
    gateway = new FcmGateway<>(Observable.just(dataMap), filter, mapper);

    // Действие:
    gateway.getData().test().isDisposed();

    // Результат:
    verify(mapper, only()).map(dataMap);
  }

  /* Проверяем результаты обработки сообщений от сервера */

  /**
   * Должен игнорировать отфильтрованные сообщение, выдав только значение по-умолчанию, если оно
   * задано.
   */
  @Test
  public void ignoreFilteredMessages() {
    // Дано:
    gateway = new FcmGateway<>(Observable.just(dataMap), filter, mapper);

    // Действие:
    TestSubscriber<String> testSubscriber = gateway.getData().test();

    // Результат:
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
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(dataMap);
    when(filter.test(dataMap)).thenReturn(true);
    gateway = new FcmGateway<>(Observable.just(dataMap), filter, mapper);

    // Действие:
    TestSubscriber<String> testSubscriber = gateway.getData().test();

    // Результат:
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
    // Дано:
    when(mapper.map(dataMap)).thenReturn("Data");
    when(filter.test(dataMap)).thenReturn(true);
    gateway = new FcmGateway<>(Observable.just(dataMap), filter, mapper);

    // Действие:
    TestSubscriber<String> testSubscriber = gateway.getData().test();

    // Результат:
    testSubscriber.assertValue("Data");
    testSubscriber.assertNoErrors();
  }
}