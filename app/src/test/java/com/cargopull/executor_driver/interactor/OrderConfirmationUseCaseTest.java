package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationUseCaseTest {

  private OrderConfirmationUseCase useCase;

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
    when(orderGateway.getOrders()).thenReturn(Flowable.never());
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean())).thenReturn(Single.never());
    useCase = new OrderConfirmationUseCaseImpl(orderGateway,
        orderConfirmationGateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение заказов.
   */
  @Test
  public void askGatewayForOrders() {
    // Действие:
    useCase.sendDecision(true).test();
    useCase.sendDecision(false).test();

    // Результат:
    verify(orderGateway, times(2)).getOrders();
    verifyNoMoreInteractions(orderGateway);
  }

  /**
   * Должен запросить у гейтвея передачу решений.
   */
  @Test
  public void askGatewayToSendDecisionsForOrders() {
    // Дано:
    when(orderGateway.getOrders()).thenReturn(Flowable.just(order));

    // Действие:
    useCase.sendDecision(true).test();
    useCase.sendDecision(false).test();

    // Результат:
    verify(orderConfirmationGateway).sendDecision(order, true);
    verify(orderConfirmationGateway).sendDecision(order, false);
    verifyNoMoreInteractions(orderConfirmationGateway);
  }

  /**
   * Должен запросить у гейтвея передачу решений только для первого свежего заказа.
   */
  @Test
  public void askGatewayToSendDecisionsForLastOrderOnly() {
    // Дано:
    when(orderGateway.getOrders()).thenReturn(Flowable.just(order, order2));

    // Действие:
    useCase.sendDecision(true).test();
    useCase.sendDecision(false).test();

    // Результат:
    verify(orderConfirmationGateway, times(2)).sendDecision(eq(order), anyBoolean());
    verifyNoMoreInteractions(orderConfirmationGateway);
  }

  /* Проверяем ответы на запрос отправки решения */

  /**
   * Должен ответить ошибкой маппинга на подтверждение.
   */
  @Test
  public void answerNoOrdersErrorForAccept() {
    // Дано:
    when(orderGateway.getOrders()).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestObserver<String> test = useCase.sendDecision(true).test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой сети на подтверждение.
   */
  @Test
  public void answerNoNetworkErrorForAccept() {
    // Дано:
    when(orderGateway.getOrders()).thenReturn(Flowable.just(order));
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Single.error(new NoNetworkException()));

    // Действие:
    TestObserver<String> test = useCase.sendDecision(true).test();

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
    when(orderGateway.getOrders())
        .thenReturn(Flowable.just(order));
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Single.error(new NoNetworkException()));

    // Действие:
    TestObserver<String> test = useCase.sendDecision(false).test();

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
    when(orderGateway.getOrders())
        .thenReturn(Flowable.just(order));
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Single.just("success"));

    // Действие:
    TestObserver<String> test = useCase.sendDecision(true).test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
    test.assertValue("success");
  }

  /**
   * Должен ответить успехом передачи отказа.
   */
  @Test
  public void answerSendDeclineSuccessful() {
    // Дано:
    when(orderGateway.getOrders())
        .thenReturn(Flowable.just(order));
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Single.just("success"));

    // Действие:
    TestObserver<String> test = useCase.sendDecision(false).test();

    // Результат:
    test.assertComplete();
    test.assertComplete();
    test.assertValue("success");
  }
}