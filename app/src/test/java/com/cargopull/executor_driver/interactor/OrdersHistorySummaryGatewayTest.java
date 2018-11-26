package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrdersHistorySummary;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.gateway.Mapper;
import com.cargopull.executor_driver.gateway.OrdersHistorySummaryGatewayImpl;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrdersHistorySummaryGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private OrdersHistorySummaryGateway gateway;

  @Mock
  private ApiService api;

  @Mock
  private Mapper<ApiOrdersHistorySummary, OrdersHistorySummary> mapper;

  @Mock
  private ApiOrdersHistorySummary apiOrdersHistorySummary;

  @Mock
  private OrdersHistorySummary ordersHistorySummary;

  @Before
  public void setUp() throws Exception {
    when(api.getOrdersHistory(anyLong(), anyLong())).thenReturn(Single.never());
    when(mapper.map(any(ApiOrdersHistorySummary.class))).thenReturn(ordersHistorySummary);
    gateway = new OrdersHistorySummaryGatewayImpl(api, mapper);
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ историю заказов.
   */
  @Test
  public void askApiGetOrdersHistory() {
    // Действие:
    gateway.getOrdersHistorySummary(11, 100);

    // Результат:
    verify(api, only()).getOrdersHistory(11, 100);
  }

  /* Проверяем работу с преобразователем данных истории заказов */

  /**
   * Не должен трогать маппер при ошибках получения данных.
   *
   */
  @Test
  public void doNotTouchMapperOnApiErrors() {
    // Дано:
    when(api.getOrdersHistory(anyLong(), anyLong())).thenReturn(Single.error(new Exception()));

    // Действие:
    gateway.getOrdersHistorySummary(11, 100).test().isDisposed();
    gateway.getOrdersHistorySummary(11, 100).test().isDisposed();
    gateway.getOrdersHistorySummary(11, 100).test().isDisposed();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить все преобразования.
   *
   * @throws Exception error
   */
  @Test
  public void askMapperForMapping() throws Exception {
    // Дано:
    when(api.getOrdersHistory(11, 100)).thenReturn(Single.just(apiOrdersHistorySummary));

    // Действие:
    gateway.getOrdersHistorySummary(11, 100).test().isDisposed();
    gateway.getOrdersHistorySummary(11, 100).test().isDisposed();
    gateway.getOrdersHistorySummary(11, 100).test().isDisposed();

    // Результат:
    verify(mapper, times(3)).map(apiOrdersHistorySummary);
    verifyNoMoreInteractions(mapper);
  }

  /**
   * Должен запросить все преобразования.
   *
   * @throws Exception error
   */
  @Test
  public void askMapperMappingAfterMappingErrors() throws Exception {
    // Дано:
    when(mapper.map(any(ApiOrdersHistorySummary.class))).thenThrow(new DataMappingException());
    when(api.getOrdersHistory(11, 100)).thenReturn(Single.just(apiOrdersHistorySummary));

    // Действие:
    gateway.getOrdersHistorySummary(11, 100).test().isDisposed();
    gateway.getOrdersHistorySummary(11, 100).test().isDisposed();
    gateway.getOrdersHistorySummary(11, 100).test().isDisposed();

    // Результат:
    verify(mapper, times(3)).map(apiOrdersHistorySummary);
    verifyNoMoreInteractions(mapper);
  }

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerWithError() {
    // Дано:
    when(api.getOrdersHistory(anyLong(), anyLong())).thenReturn(Single.error(new Exception()));

    // Действие и Результат:
    gateway.getOrdersHistorySummary(11, 100).test().assertError(Exception.class);
    gateway.getOrdersHistorySummary(11, 100).test().assertError(Exception.class);
  }

  /**
   * Должен ответить ошибкой преобразования маппером.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingError() throws Exception {
    // Дано:
    when(api.getOrdersHistory(11, 100)).thenReturn(Single.just(apiOrdersHistorySummary));
    when(mapper.map(any(ApiOrdersHistorySummary.class))).thenThrow(new DataMappingException());

    // Действие и Результат:
    gateway.getOrdersHistorySummary(11, 100).test().assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть историю заказов.
   *
   */
  @Test
  public void answerWithOrdersHistorySummary() {
    // Дано:
    when(api.getOrdersHistory(11, 100)).thenReturn(Single.just(apiOrdersHistorySummary));

    // Действие:
    TestObserver<OrdersHistorySummary> testObserver = gateway.getOrdersHistorySummary(11, 100)
        .test();

    // Результат:
    testObserver.assertComplete();
    testObserver.assertValue(ordersHistorySummary);
  }
}