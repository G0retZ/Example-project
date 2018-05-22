package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.gateway.Mapper;
import com.fasten.executor_driver.gateway.OrderGatewayImpl;
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

  private OrderGateway orderGateway;
  @Mock
  private ExecutorStateUseCase executorStateUseCase;
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
    when(executorStateUseCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.never());
    orderGateway = new OrderGatewayImpl(executorStateUseCase, mapper);
  }

  /* Проверяем работу с с юзкейсом статусов */

  /**
   * Должен попросить у юзкейса статусы исполнителя.
   */
  @Test
  public void askExecutorStateUseCaseForStatusUpdates() {
    // Действие:
    orderGateway.getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION).test();

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates(false);
  }

  /* Проверяем работу с маппером */

  /**
   * Не должен трогать маппер, если не статус не соответствует фильтруемому.
   */
  @Test
  public void doNotTouchMapperIfExecutorStateIncorrect() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED,
            ExecutorState.ONLINE, ExecutorState.CLIENT_ORDER_CONFIRMATION,
            ExecutorState.MOVING_TO_CLIENT, ExecutorState.WAITING_FOR_CLIENT,
            ExecutorState.ORDER_FULFILLMENT));

    // Действие:
    orderGateway.getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION).test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Не должен трогать маппер, если статус без сообщения.
   */
  @Test
  public void doNotTouchMapperIfNoData() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.CLIENT_ORDER_CONFIRMATION));

    // Действие:
    orderGateway.getOrders(ExecutorState.CLIENT_ORDER_CONFIRMATION).test();

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
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

    // Действие:
    orderGateway.getOrders(ExecutorState.MOVING_TO_CLIENT).test();

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
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED,
            ExecutorState.ONLINE, ExecutorState.CLIENT_ORDER_CONFIRMATION,
            ExecutorState.MOVING_TO_CLIENT, ExecutorState.WAITING_FOR_CLIENT,
            ExecutorState.ORDER_FULFILLMENT));

    // Действие:
    TestSubscriber<Order> testSubscriber =
        orderGateway.getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION).test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой отсутствия заказов для статуса "принятие заказа" без данных.
   */
  @Test
  public void answerNoOrdersAvailableForNoData() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.CLIENT_ORDER_CONFIRMATION));

    // Действие:
    TestSubscriber<Order> testSubscriber = orderGateway
        .getOrders(ExecutorState.CLIENT_ORDER_CONFIRMATION).test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
    testSubscriber.assertError(NoOrdersAvailableException.class);
  }

  /**
   * Должен ответить ошибкой маппинга.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingError() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(anyString());
    ExecutorState.MOVING_TO_CLIENT.setData("");
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

    // Действие:
    TestSubscriber<Order> testSubscriber =
        orderGateway.getOrders(ExecutorState.MOVING_TO_CLIENT).test();

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
    when(mapper.map(anyString())).thenReturn(order);
    ExecutorState.WAITING_FOR_CLIENT.setData("");
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.WAITING_FOR_CLIENT));

    // Действие:
    TestSubscriber<Order> testSubscriber =
        orderGateway.getOrders(ExecutorState.WAITING_FOR_CLIENT).test();

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
    testSubscriber.assertValue(order);
  }
}