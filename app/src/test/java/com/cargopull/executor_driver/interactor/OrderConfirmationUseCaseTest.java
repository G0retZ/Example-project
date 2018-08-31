package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.PreOrderExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private OrderConfirmationUseCase useCase;

  @Mock
  private OrderUseCase orderUseCase;
  @Mock
  private OrderConfirmationGateway orderConfirmationGateway;
  @Mock
  private Order order;
  @Mock
  private Order order2;
  @Mock
  private Action action;

  @Before
  public void setUp() {
    when(orderUseCase.getOrders()).thenReturn(Flowable.never());
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean())).thenReturn(Single.never());
    useCase = new OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway);
  }

  /* Проверяем работу с юзкейсом заказа */

  /**
   * Должен запросить у юзкейса заказа получение заказов.
   */
  @Test
  public void askOrderUseCaseForOrders() {
    // Действие:
    useCase.sendDecision(true).test();
    useCase.sendDecision(false).test();

    // Результат:
    verify(orderUseCase, times(2)).getOrders();
    verifyNoMoreInteractions(orderUseCase);
  }

  /**
   * Должен запросить у юзкейса заказа деактуализацию заказов.
   */
  @Test
  public void askOrderUseCaseToSetCurrentOrderExpired() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(orderUseCase);
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order).concatWith(Flowable.never()));
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean())).thenReturn(Single.just(""));

    // Действие:
    useCase.sendDecision(true).test();
    useCase.sendDecision(false).test();

    // Результат:
    inOrder.verify(orderUseCase).getOrders();
    inOrder.verify(orderUseCase).setOrderExpired();
    inOrder.verify(orderUseCase).getOrders();
    inOrder.verify(orderUseCase).setOrderExpired();
    verifyNoMoreInteractions(orderUseCase);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея передачу решений.
   */
  @Test
  public void askGatewayToSendDecisionsForOrders() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order).concatWith(Flowable.never()));

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
    when(orderUseCase.getOrders())
        .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()));

    // Действие:
    useCase.sendDecision(true).test();
    useCase.sendDecision(false).test();

    // Результат:
    verify(orderConfirmationGateway, times(2)).sendDecision(eq(order), anyBoolean());
    verifyNoMoreInteractions(orderConfirmationGateway);
  }

  /**
   * Должен отменить запрос у гейтвея на передачу решений если пришел новый заказ.
   */
  @Test
  public void cancelGatewayToSendDecisionsForLastOrderOnly() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(orderConfirmationGateway, action);
    when(orderUseCase.getOrders())
        .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()));
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Single.<String>never().doOnDispose(action));

    // Действие:
    useCase.sendDecision(true).test();
    useCase.sendDecision(false).test();

    // Результат:
    inOrder.verify(orderConfirmationGateway).sendDecision(order, true);
    inOrder.verify(action).run();
    inOrder.verify(orderConfirmationGateway).sendDecision(order, false);
    inOrder.verify(action).run();
    verifyNoMoreInteractions(orderConfirmationGateway, action);
  }

  /* Проверяем ответы на запрос отправки решения */

  /**
   * Должен ответить ошибкой маппинга на подтверждение.
   */
  @Test
  public void answerDataMappingErrorForAccept() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestObserver<String> test = useCase.sendDecision(true).test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой не актуальности заказа на подтверждение.
   */
  @Test
  public void answerOrderExpiredErrorForAccept() {
    // Дано:
    when(orderUseCase.getOrders())
        .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()));

    // Действие:
    TestObserver<String> test = useCase.sendDecision(true).test();

    // Результат:
    test.assertError(PreOrderExpiredException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой сети на подтверждение.
   */
  @Test
  public void answerNoNetworkErrorForAccept() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order).concatWith(Flowable.never()));
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
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order).concatWith(Flowable.never()));
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
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order).concatWith(Flowable.never()));
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
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order).concatWith(Flowable.never()));
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
        .thenReturn(Single.just("success"));

    // Действие:
    TestObserver<String> test = useCase.sendDecision(false).test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
    test.assertValue("success");
  }
}