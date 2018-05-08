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
import com.fasten.executor_driver.entity.NoOffersAvailableException;
import com.fasten.executor_driver.entity.Offer;
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
public class OrderConfirmationUseCaseTest {

  private OrderConfirmationUseCase orderConfirmationUseCase;

  @Mock
  private OfferGateway gateway;
  @Mock
  private Offer offer;
  @Mock
  private Offer offer2;

  @Before
  public void setUp() {
    when(gateway.getOffers()).thenReturn(Flowable.never());
    when(gateway.sendDecision(any(), anyBoolean())).thenReturn(Completable.never());
    orderConfirmationUseCase = new OrderConfirmationUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение заказов.
   */
  @Test
  public void askGatewayForOffers() {
    // Действие:
    orderConfirmationUseCase.getOffers().test();

    // Результат:
    verify(gateway, only()).getOffers();
  }

  /**
   * Не должен запрашивать у гейтвея передачу отказа, если не было заказа.
   */
  @Test
  public void doNotAskGatewayToSendCancel() {
    // Действие:
    orderConfirmationUseCase.cancelOrder().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Должен запросить у гейтвея передачу отказов.
   */
  @Test
  public void askGatewayToSendCancelForOffers() {
    // Дано:
    when(gateway.getOffers()).thenReturn(Flowable.just(offer));

    // Действие:
    orderConfirmationUseCase.getOffers().test();
    orderConfirmationUseCase.cancelOrder().test();

    // Результат:
    verify(gateway).getOffers();
    verify(gateway).sendDecision(offer, false);
    verifyNoMoreInteractions(gateway);
  }

  /**
   * Должен запросить у гейтвея передачу отказа только для последнего заказа.
   */
  @Test
  public void askGatewayToSendDecisionsForLastOfferOnly() {
    // Дано:
    when(gateway.getOffers()).thenReturn(Flowable.just(offer, offer2));

    // Действие:
    orderConfirmationUseCase.getOffers().test();
    orderConfirmationUseCase.getOffers().test();
    orderConfirmationUseCase.cancelOrder().test();
    orderConfirmationUseCase.cancelOrder().test();

    // Результат:
    verify(gateway, times(2)).sendDecision(eq(offer2), eq(false));
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
    TestSubscriber<Offer> test = orderConfirmationUseCase.getOffers().test();

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
    when(gateway.getOffers()).thenReturn(Flowable.just(offer, offer2));

    // Действие:
    TestSubscriber<Offer> test = orderConfirmationUseCase.getOffers().test();

    // Результат:
    test.assertValues(offer, offer2);
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
    TestObserver<Void> test = orderConfirmationUseCase.cancelOrder().test();

    // Результат:
    test.assertError(NoOffersAvailableException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой отсуствия актуальных заказов.
   */
  @Test
  public void answerNoOffersError() {
    // Действие:
    TestObserver<Void> test = orderConfirmationUseCase.cancelOrder().test();

    // Результат:
    test.assertError(NoOffersAvailableException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkErrorForAccept() {
    // Дано:
    when(gateway.getOffers()).thenReturn(Flowable.just(offer));
    when(gateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    orderConfirmationUseCase.getOffers().test();
    TestObserver<Void> test = orderConfirmationUseCase.cancelOrder().test();

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
    when(gateway.getOffers()).thenReturn(Flowable.just(offer));
    when(gateway.sendDecision(any(), anyBoolean())).thenReturn(Completable.complete());

    // Действие:
    orderConfirmationUseCase.getOffers().test();
    TestObserver<Void> test = orderConfirmationUseCase.cancelOrder().test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }
}