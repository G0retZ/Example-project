package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.gateway.Mapper;
import com.cargopull.executor_driver.gateway.OrderCostDetailsGatewayImpl;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderCostDetailsGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private OrderCostDetailsGateway gateway;
  @Mock
  private ExecutorStateUseCase useCase;
  @Mock
  private Mapper<String, OrderCostDetails> mapper;
  @Mock
  private OrderCostDetails order;

  @Before
  public void setUp() {
    ExecutorState.PAYMENT_CONFIRMATION.setData(null);
    when(useCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.never());
  }

  /* Проверяем работу с юзкейсом статусов */

  /**
   * Должен попросить у юзкейса статусы исполнителя.
   */
  @Test
  public void askExecutorStateUseCaseForStatusUpdates() {
    // Дано:
    gateway = new OrderCostDetailsGatewayImpl(useCase, mapper);

    // Действие:
    gateway.getOrderCostDetails().test();

    // Результат:
    verify(useCase, only()).getExecutorStates(false);
  }

  /* Проверяем работу с маппером */

  /**
   * Не должен трогать маппер, если статус не соответствует фильтруемому.
   */
  @Test
  public void doNotTouchMapperIfExecutorStateIncorrect() {
    // Дано:
    gateway = new OrderCostDetailsGatewayImpl(useCase, mapper);
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED,
            ExecutorState.ONLINE, ExecutorState.DRIVER_ORDER_CONFIRMATION,
            ExecutorState.CLIENT_ORDER_CONFIRMATION, ExecutorState.MOVING_TO_CLIENT,
            ExecutorState.WAITING_FOR_CLIENT, ExecutorState.ORDER_FULFILLMENT));

    // Действие:
    gateway.getOrderCostDetails().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Не должен трогать маппер, если статус без сообщения.
   */
  @Test
  public void doNotTouchMapperIfNoData() {
    // Дано:
    gateway = new OrderCostDetailsGatewayImpl(useCase, mapper);
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.PAYMENT_CONFIRMATION));

    // Действие:
    gateway.getOrderCostDetails().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить маппинг для данных статуса.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForData() throws Exception {
    // Дано:
    ExecutorState.PAYMENT_CONFIRMATION.setData("");
    gateway = new OrderCostDetailsGatewayImpl(useCase, mapper);
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.PAYMENT_CONFIRMATION));

    // Действие:
    gateway.getOrderCostDetails().test();

    // Результат:
    verify(mapper, only()).map("");
  }

  /* Проверяем результаты обработки сообщений от сервера по статусам */

  /**
   * Должен игнорировать не свои статусы.
   */
  @Test
  public void ignoreForIncorrectExecutorState() {
    // Дано:
    gateway = new OrderCostDetailsGatewayImpl(useCase, mapper);
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED,
            ExecutorState.ONLINE, ExecutorState.DRIVER_ORDER_CONFIRMATION,
            ExecutorState.CLIENT_ORDER_CONFIRMATION, ExecutorState.MOVING_TO_CLIENT,
            ExecutorState.WAITING_FOR_CLIENT, ExecutorState.ORDER_FULFILLMENT));

    // Действие:
    TestSubscriber<OrderCostDetails> testSubscriber = gateway.getOrderCostDetails().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой маппинга детального расчета заказа для статуса "принятие оплаты" без данных.
   */
  @Test
  public void answerNoOrderCostDetailsAvailableForNoData() {
    // Дано:
    gateway = new OrderCostDetailsGatewayImpl(useCase, mapper);
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.PAYMENT_CONFIRMATION));

    // Действие:
    TestSubscriber<OrderCostDetails> testSubscriber = gateway.getOrderCostDetails().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен ответить ошибкой маппинга.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingError() throws Exception {
    // Дано:
    gateway = new OrderCostDetailsGatewayImpl(useCase, mapper);
    doThrow(new DataMappingException()).when(mapper).map(anyString());
    ExecutorState.PAYMENT_CONFIRMATION.setData("");
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.PAYMENT_CONFIRMATION));

    // Действие:
    TestSubscriber<OrderCostDetails> testSubscriber = gateway.getOrderCostDetails().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть детальный расчет заказа.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithOrderCostDetails() throws Exception {
    // Дано:
    gateway = new OrderCostDetailsGatewayImpl(useCase, mapper);
    when(mapper.map(anyString())).thenReturn(order);
    ExecutorState.PAYMENT_CONFIRMATION.setData("");
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.PAYMENT_CONFIRMATION));

    // Действие:
    TestSubscriber<OrderCostDetails> testSubscriber = gateway.getOrderCostDetails().test();

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
    testSubscriber.assertValue(order);
  }
}