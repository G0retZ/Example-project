package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.NoOffersAvailableException;
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
public class OrderUseCaseTest {

  private DriverOrderConfirmationUseCase driverOrderConfirmationUseCase;

  @Mock
  private OrderGateway gateway;
  @Mock
  private Order order;
  @Mock
  private Order order2;

  @Before
  public void setUp() {
    when(gateway.getOffers()).thenReturn(Flowable.never());
    when(gateway.sendDecision(any(), anyBoolean())).thenReturn(Completable.never());
    driverOrderConfirmationUseCase = new DriverOrderConfirmationUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение заказов.
   */
  @Test
  public void askGatewayForOffers() {
    // Действие:
    driverOrderConfirmationUseCase.getOffers().test();

    // Результат:
    verify(gateway, only()).getOffers();
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
    verifyZeroInteractions(gateway);
  }

  /**
   * Должен запросить у гейтвея передачу решений.
   */
  @Test
  public void askGatewayToSendDecisionsForOffers() {
    // Дано:
    when(gateway.getOffers()).thenReturn(Flowable.just(order));

    // Действие:
    driverOrderConfirmationUseCase.getOffers().test();
    driverOrderConfirmationUseCase.sendDecision(true).test();
    when(gateway.getOffers()).thenReturn(Flowable.just(order));
    driverOrderConfirmationUseCase.getOffers().test();
    driverOrderConfirmationUseCase.sendDecision(false).test();

    // Результат:
    verify(gateway).sendDecision(order, true);
    verify(gateway).sendDecision(order, false);
  }

  /**
   * Должен запросить у гейтвея передачу решений только для последнего заказа.
   */
  @Test
  public void askGatewayToSendDecisionsForLastOfferOnly() {
    // Дано:
    when(gateway.getOffers()).thenReturn(Flowable.just(order, order2));

    // Действие:
    driverOrderConfirmationUseCase.getOffers().test();
    driverOrderConfirmationUseCase.getOffers().test();
    driverOrderConfirmationUseCase.sendDecision(true).test();
    driverOrderConfirmationUseCase.sendDecision(false).test();

    // Результат:
    verify(gateway, times(2)).sendDecision(eq(order2), anyBoolean());
  }

  /* Проверяем ответы на запрос заказов */

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Дано:
    when(gateway.getOffers()).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Order> test = driverOrderConfirmationUseCase.getOffers().test();

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
    when(gateway.getOffers()).thenReturn(Flowable.just(order, order2));

    // Действие:
    TestSubscriber<Order> test = driverOrderConfirmationUseCase.getOffers().test();

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
    TestObserver<Void> test = driverOrderConfirmationUseCase.sendDecision(true).test();

    // Результат:
    test.assertError(NoOffersAvailableException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой отсуствия актуальных заказов на отказ.
   */
  @Test
  public void answerNoOffersErrorForDecline() {
    // Действие:
    TestObserver<Void> test = driverOrderConfirmationUseCase.sendDecision(false).test();

    // Результат:
    test.assertError(NoOffersAvailableException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой сети на подтверждение.
   */
  @Test
  public void answerNoNetworkErrorForAccept() {
    // Дано:
    when(gateway.getOffers()).thenReturn(Flowable.just(order));
    when(gateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    driverOrderConfirmationUseCase.getOffers().test();
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
    when(gateway.getOffers()).thenReturn(Flowable.just(order));
    when(gateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    driverOrderConfirmationUseCase.getOffers().test();
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
    when(gateway.getOffers()).thenReturn(Flowable.just(order));
    when(gateway.sendDecision(any(), anyBoolean())).thenReturn(Completable.complete());

    // Действие:
    driverOrderConfirmationUseCase.getOffers().test();
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
    when(gateway.getOffers()).thenReturn(Flowable.just(order));
    when(gateway.sendDecision(any(), anyBoolean())).thenReturn(Completable.complete());

    // Действие:
    driverOrderConfirmationUseCase.getOffers().test();
    TestObserver<Void> test = driverOrderConfirmationUseCase.sendDecision(false).test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }
}