package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.gateway.Mapper;
import com.cargopull.executor_driver.gateway.OrderGatewayImpl;
import io.reactivex.Flowable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderGatewayTest {

  private OrderGateway gateway;
  @Mock
  private ExecutorStateUseCase useCase;
  @Mock
  private Mapper<String, Order> mapper;
  @Mock
  private Order order;

  @Before
  public void setUp() {
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    ExecutorState.DRIVER_ORDER_CONFIRMATION.setData(null);
    ExecutorState.CLIENT_ORDER_CONFIRMATION.setData(null);
    when(useCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.never());
  }

  /* Проверяем работу с с юзкейсом статусов */

  /**
   * Должен попросить у юзкейса статусы исполнителя.
   */
  @Test
  public void askExecutorStateUseCaseForStatusUpdates() {
    // Дано:
    gateway = new OrderGatewayImpl(useCase,
        ExecutorState.DRIVER_ORDER_CONFIRMATION, mapper);

    // Действие:
    gateway.getOrders().test();

    // Результат:
    verify(useCase, only()).getExecutorStates(false);
  }

  /* Проверяем работу с маппером */

  /**
   * Не должен трогать маппер, если не статус не соответствует фильтруемому.
   */
  @Test
  public void doNotTouchMapperIfExecutorStateIncorrect() {
    // Дано:
    gateway = new OrderGatewayImpl(useCase,
        ExecutorState.DRIVER_ORDER_CONFIRMATION, mapper);
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED,
            ExecutorState.ONLINE, ExecutorState.CLIENT_ORDER_CONFIRMATION,
            ExecutorState.MOVING_TO_CLIENT, ExecutorState.WAITING_FOR_CLIENT,
            ExecutorState.ORDER_FULFILLMENT));

    // Действие:
    gateway.getOrders().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Не должен трогать маппер, если статус без сообщения.
   */
  @Test
  public void doNotTouchMapperIfNoData() {
    // Дано:
    gateway = new OrderGatewayImpl(useCase,
        ExecutorState.CLIENT_ORDER_CONFIRMATION, mapper);
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.CLIENT_ORDER_CONFIRMATION));

    // Действие:
    gateway.getOrders().test();

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
    ExecutorState.MOVING_TO_CLIENT.setData("");
    gateway = new OrderGatewayImpl(useCase, ExecutorState.MOVING_TO_CLIENT,
        mapper);
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

    // Действие:
    gateway.getOrders().test();

    // Результат:
    verify(mapper, only()).map("");
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем результаты обработки сообщений от сервера по статусам */

  /**
   * Должен игнорировать не свои статусы.
   */
  @Test
  public void ignoreForIncorrectExecutorState() {
    // Дано:
    gateway = new OrderGatewayImpl(useCase,
        ExecutorState.DRIVER_ORDER_CONFIRMATION, mapper);
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED,
            ExecutorState.ONLINE, ExecutorState.CLIENT_ORDER_CONFIRMATION,
            ExecutorState.MOVING_TO_CLIENT, ExecutorState.WAITING_FOR_CLIENT,
            ExecutorState.ORDER_FULFILLMENT));

    // Действие:
    TestSubscriber<Order> testSubscriber = gateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой маппинга заказа для статуса "принятие заказа" без данных.
   */
  @Test
  public void answerNoOrdersAvailableForNoData() {
    // Дано:
    gateway = new OrderGatewayImpl(useCase,
        ExecutorState.CLIENT_ORDER_CONFIRMATION, mapper);
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.CLIENT_ORDER_CONFIRMATION));

    // Действие:
    TestSubscriber<Order> testSubscriber = gateway.getOrders().test();

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
    gateway = new OrderGatewayImpl(useCase, ExecutorState.MOVING_TO_CLIENT,
        mapper);
    doThrow(new DataMappingException()).when(mapper).map(anyString());
    ExecutorState.MOVING_TO_CLIENT.setData("");
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

    // Действие:
    TestSubscriber<Order> testSubscriber = gateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть заказ.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithOrder() throws Exception {
    // Дано:
    gateway = new OrderGatewayImpl(useCase, ExecutorState.WAITING_FOR_CLIENT,
        mapper);
    when(mapper.map(anyString())).thenReturn(order);
    ExecutorState.WAITING_FOR_CLIENT.setData("");
    when(useCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.WAITING_FOR_CLIENT));

    // Действие:
    TestSubscriber<Order> testSubscriber = gateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
    testSubscriber.assertValue(order);
  }
}