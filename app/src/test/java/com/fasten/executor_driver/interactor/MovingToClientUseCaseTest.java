package com.fasten.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
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
public class MovingToClientUseCaseTest {

  private MovingToClientUseCase movingToClientUseCase;

  @Mock
  private MovingToClientGateway movingToClientGateway;
  @Mock
  private Order order;
  @Mock
  private Order order2;

  @Before
  public void setUp() {
    when(movingToClientGateway.getOrders()).thenReturn(Flowable.never());
    when(movingToClientGateway.callToClient()).thenReturn(Completable.never());
    when(movingToClientGateway.reportArrival()).thenReturn(Completable.never());
    movingToClientUseCase = new MovingToClientUseCaseImpl(movingToClientGateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение выполняемого заказа.
   */
  @Test
  public void askGatewayForOrders() {
    // Действие:
    movingToClientUseCase.getOrders().test();

    // Результат:
    verify(movingToClientGateway, only()).getOrders();
  }

  /**
   * Должен запросить у гейтвея звонок клиенту.
   */
  @Test
  public void askGatewayToToCallClientForOrder() {
    // Действие:
    movingToClientUseCase.callToClient().test();

    // Результат:
    verify(movingToClientGateway, only()).callToClient();
  }

  /**
   * Должен сообщить гейтвею о прибытии к клиенту.
   */
  @Test
  public void askGatewayToReportArrivalForOrder() {
    // Действие:
    movingToClientUseCase.reportArrival().test();

    // Результат:
    verify(movingToClientGateway, only()).reportArrival();
  }

  /* Проверяем ответы на запрос заказов */

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Дано:
    when(movingToClientGateway.getOrders()).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Order> test = movingToClientUseCase.getOrders().test();

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
    when(movingToClientGateway.getOrders()).thenReturn(Flowable.just(order, order2));

    // Действие:
    TestSubscriber<Order> test = movingToClientUseCase.getOrders().test();

    // Результат:
    test.assertValues(order, order2);
    test.assertComplete();
    test.assertNoErrors();
  }

  /* Проверяем ответы на запрос звонка клиенту */

  /**
   * Должен ответить ошибкой сети на запрос звонка клиенту.
   */
  @Test
  public void answerNoNetworkErrorForCallClient() {
    // Дано:
    when(movingToClientGateway.getOrders()).thenReturn(Flowable.just(order));
    when(movingToClientGateway.callToClient())
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    movingToClientUseCase.getOrders().test();
    TestObserver<Void> test = movingToClientUseCase.callToClient().test();

    // Результат:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить успехом запроса звонка клиенту.
   */
  @Test
  public void answerSendCallClientSuccessful() {
    // Дано:
    when(movingToClientGateway.getOrders()).thenReturn(Flowable.just(order));
    when(movingToClientGateway.callToClient()).thenReturn(Completable.complete());

    // Действие:
    movingToClientUseCase.getOrders().test();
    TestObserver<Void> test = movingToClientUseCase.callToClient().test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }

  /* Проверяем ответы на сообщение о прибытии к клиенту */

  /**
   * Должен ответить ошибкой сети на сообщение о прибытии к клиенту.
   */
  @Test
  public void answerNoNetworkErrorForReportArrival() {
    // Дано:
    when(movingToClientGateway.getOrders()).thenReturn(Flowable.just(order));
    when(movingToClientGateway.reportArrival())
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    movingToClientUseCase.getOrders().test();
    TestObserver<Void> test = movingToClientUseCase.reportArrival().test();

    // Результат:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить успехом отправки сообщения о прибытии к клиенту.
   */
  @Test
  public void answerSendReportArrivalSuccessful() {
    // Дано:
    when(movingToClientGateway.getOrders()).thenReturn(Flowable.just(order));
    when(movingToClientGateway.reportArrival()).thenReturn(Completable.complete());

    // Действие:
    movingToClientUseCase.getOrders().test();
    TestObserver<Void> test = movingToClientUseCase.reportArrival().test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }
}