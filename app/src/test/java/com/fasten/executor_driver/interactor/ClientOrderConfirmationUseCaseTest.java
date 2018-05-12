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
public class ClientOrderConfirmationUseCaseTest {

  private ClientOrderConfirmationUseCase clientOrderConfirmationUseCase;

  @Mock
  private OrderGateway gateway;
  @Mock
  private Order order;
  @Mock
  private Order order2;

  @Before
  public void setUp() {
    when(gateway.getOrders()).thenReturn(Flowable.never());
    when(gateway.sendDecision(any(), anyBoolean())).thenReturn(Completable.never());
    clientOrderConfirmationUseCase = new ClientOrderConfirmationUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение заказов.
   */
  @Test
  public void askGatewayForOffers() {
    // Действие:
    clientOrderConfirmationUseCase.getOrders().test();

    // Результат:
    verify(gateway, only()).getOrders();
  }

  /**
   * Не должен запрашивать у гейтвея передачу отказа, если не было заказа.
   */
  @Test
  public void doNotAskGatewayToSendCancel() {
    // Действие:
    clientOrderConfirmationUseCase.cancelOrder().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Должен запросить у гейтвея передачу отказов.
   */
  @Test
  public void askGatewayToSendCancelForOffers() {
    // Дано:
    when(gateway.getOrders()).thenReturn(Flowable.just(order));

    // Действие:
    clientOrderConfirmationUseCase.getOrders().test();
    clientOrderConfirmationUseCase.cancelOrder().test();

    // Результат:
    verify(gateway).getOrders();
    verify(gateway).sendDecision(order, false);
    verifyNoMoreInteractions(gateway);
  }

  /**
   * Должен запросить у гейтвея передачу отказа только для последнего заказа.
   */
  @Test
  public void askGatewayToSendDecisionsForLastOfferOnly() {
    // Дано:
    when(gateway.getOrders()).thenReturn(Flowable.just(order, order2));

    // Действие:
    clientOrderConfirmationUseCase.getOrders().test();
    clientOrderConfirmationUseCase.getOrders().test();
    clientOrderConfirmationUseCase.cancelOrder().test();
    clientOrderConfirmationUseCase.cancelOrder().test();

    // Результат:
    verify(gateway, times(2)).sendDecision(eq(order2), eq(false));
  }

  /* Проверяем ответы на запрос заказов */

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Дано:
    when(gateway.getOrders()).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Order> test = clientOrderConfirmationUseCase.getOrders().test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить заказами.
   */
  @Test
  public void answerWithOffers() {
    // Дано:
    when(gateway.getOrders()).thenReturn(Flowable.just(order, order2));

    // Действие:
    TestSubscriber<Order> test = clientOrderConfirmationUseCase.getOrders().test();

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
  public void answerNoOffersErrorForAccept() {
    // Действие:
    TestObserver<Void> test = clientOrderConfirmationUseCase.cancelOrder().test();

    // Результат:
    test.assertError(NoOrdersAvailableException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой отсуствия актуальных заказов.
   */
  @Test
  public void answerNoOffersError() {
    // Действие:
    TestObserver<Void> test = clientOrderConfirmationUseCase.cancelOrder().test();

    // Результат:
    test.assertError(NoOrdersAvailableException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkErrorForAccept() {
    // Дано:
    when(gateway.getOrders()).thenReturn(Flowable.just(order));
    when(gateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    clientOrderConfirmationUseCase.getOrders().test();
    TestObserver<Void> test = clientOrderConfirmationUseCase.cancelOrder().test();

    // Результат:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить успехом передачи отказа.
   */
  @Test
  public void answerSendDeclineSuccessful() {
    // Дано:
    when(gateway.getOrders()).thenReturn(Flowable.just(order));
    when(gateway.sendDecision(any(), anyBoolean())).thenReturn(Completable.complete());

    // Действие:
    clientOrderConfirmationUseCase.getOrders().test();
    TestObserver<Void> test = clientOrderConfirmationUseCase.cancelOrder().test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }
}