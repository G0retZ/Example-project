package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.gateway.DataMappingException;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DriverOrderConfirmationUseCaseTest {

  private DriverOrderConfirmationUseCase driverOrderConfirmationUseCase;

  @Mock
  private OrderGateway orderGateway;
  @Mock
  private OrderConfirmationGateway orderConfirmationGateway;
  @Mock
  private Order order;
  @Mock
  private Order order2;

  @Before
  public void setUp() {
    when(orderGateway.getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION))
        .thenReturn(Flowable.never());
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Completable.never());
    driverOrderConfirmationUseCase = new DriverOrderConfirmationUseCaseImpl(orderGateway,
        orderConfirmationGateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение заказов.
   */
  @Test
  public void askGatewayForOrders() {
    // Действие:
    driverOrderConfirmationUseCase.getOrders().test();

    // Результат:
    verify(orderGateway, only()).getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION);
  }

  /**
   * Не должен запрашивать у гейтвея передачу решения, если не было заказа.
   */
  @Test
  public void doNotAskGatewayToSendDecision() {
    // Действие:
    driverOrderConfirmationUseCase.sendDecision(true).test();
    driverOrderConfirmationUseCase.sendDecision(false).test();

    // Результат:
    verifyZeroInteractions(orderGateway);
  }

  /**
   * Должен запросить у гейтвея передачу решений.
   */
  @Test
  public void askGatewayToSendDecisionsForOrders() {
    // Дано:
    when(orderGateway.getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION))
        .thenReturn(Flowable.just(order));

    // Действие:
    driverOrderConfirmationUseCase.getOrders().test();
    driverOrderConfirmationUseCase.sendDecision(true).test();
    when(orderGateway.getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION))
        .thenReturn(Flowable.just(order));
    driverOrderConfirmationUseCase.getOrders().test();
    driverOrderConfirmationUseCase.sendDecision(false).test();

    // Результат:
    verify(orderConfirmationGateway).sendDecision(order, true);
    verify(orderConfirmationGateway).sendDecision(order, false);
    verifyNoMoreInteractions(orderConfirmationGateway);
  }

  /**
   * Должен запросить у гейтвея передачу решений только для последнего заказа.
   */
  @Test
  public void askGatewayToSendDecisionsForLastOrderOnly() {
    // Дано:
    when(orderGateway.getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION))
        .thenReturn(Flowable.just(order, order2));

    // Действие:
    driverOrderConfirmationUseCase.getOrders().test();
    driverOrderConfirmationUseCase.getOrders().test();
    driverOrderConfirmationUseCase.sendDecision(true).test();
    driverOrderConfirmationUseCase.sendDecision(false).test();

    // Результат:
    verify(orderConfirmationGateway, times(2)).sendDecision(eq(order2), anyBoolean());
    verifyNoMoreInteractions(orderConfirmationGateway);
  }

  /* Проверяем ответы на запрос заказов */

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Дано:
    when(orderGateway.getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION))
        .thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Order> test = driverOrderConfirmationUseCase.getOrders().test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить заказами.
   */
  @Test
  public void answerWithOrders() {
    // Дано:
    when(orderGateway.getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION))
        .thenReturn(Flowable.just(order, order2));

    // Действие:
    TestSubscriber<Order> test = driverOrderConfirmationUseCase.getOrders().test();

    // Результат:
    test.assertValues(order, order2);
    test.assertComplete();
    test.assertNoErrors();
  }

  /* Проверяем ответы на запрос отправки решения */

  /**
   * Должен ответить ошибкой отсуствия актуальных заказов на подтверждение.
   */
  @Test
  public void answerNoOrdersErrorForAccept() {
    // Действие:
    TestObserver<Void> test = driverOrderConfirmationUseCase.sendDecision(true).test();

    // Результат:
    test.assertError(NoOrdersAvailableException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой отсуствия актуальных заказов на отказ.
   */
  @Test
  public void answerNoOrdersErrorForDecline() {
    // Действие:
    TestObserver<Void> test = driverOrderConfirmationUseCase.sendDecision(false).test();

    // Результат:
    test.assertError(NoOrdersAvailableException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой сети на подтверждение.
   */
  @Test
  public void answerNoNetworkErrorForAccept() {
    // Дано:
    when(orderGateway.getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION))
        .thenReturn(Flowable.just(order));
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    driverOrderConfirmationUseCase.getOrders().test();
    TestObserver<Void> test = driverOrderConfirmationUseCase.sendDecision(true).test();

    // Результат:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой сети на отказ.
   */
  @Test
  public void answerNoNetworkErrorForDecline() {
    // Дано:
    when(orderGateway.getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION))
        .thenReturn(Flowable.just(order));
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    driverOrderConfirmationUseCase.getOrders().test();
    TestObserver<Void> test = driverOrderConfirmationUseCase.sendDecision(false).test();

    // Результат:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить успехом передачи подтверждения.
   */
  @Test
  public void answerSendAcceptSuccessful() {
    // Дано:
    when(orderGateway.getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION))
        .thenReturn(Flowable.just(order));
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Completable.complete());

    // Действие:
    driverOrderConfirmationUseCase.getOrders().test();
    TestObserver<Void> test = driverOrderConfirmationUseCase.sendDecision(true).test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }

  /**
   * Должен ответить успехом передачи отказа.
   */
  @Test
  public void answerSendDeclineSuccessful() {
    // Дано:
    when(orderGateway.getOrders(ExecutorState.DRIVER_ORDER_CONFIRMATION))
        .thenReturn(Flowable.just(order));
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Completable.complete());

    // Действие:
    driverOrderConfirmationUseCase.getOrders().test();
    TestObserver<Void> test = driverOrderConfirmationUseCase.sendDecision(false).test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }
}