package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.Pair;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
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
  private OrderDecisionUseCase orderDecisionUseCase;
  @Mock
  private OrderConfirmationGateway orderConfirmationGateway;
  @Mock
  private Order order;
  @Mock
  private Order order2;
  @Mock
  private Action action;
  @Mock
  private OrdersUseCase ordersUseCase;

  @Before
  public void setUp() {
    when(orderUseCase.getOrders()).thenReturn(Flowable.never());
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean())).thenReturn(Single.never());
    useCase = new OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway,
        orderDecisionUseCase, ordersUseCase);
  }

  /* Проверяем работу с юзкейсом заказа */

  /**
   * Должен запросить у юзкейса заказа получение заказов при отправке решения.
   */
  @Test
  public void askOrderUseCaseForOrdersOnSendDecision() {
    // Действие:
    useCase.sendDecision(true).test();
    useCase.sendDecision(false).test();

    // Результат:
    verify(orderUseCase, times(2)).getOrders();
    verifyNoMoreInteractions(orderUseCase);
  }

  /**
   * Должен запросить у юзкейса заказа получение заказов при запросе таймаутов.
   */
  @Test
  public void askOrderUseCaseForOrdersOnGetTimeout() {
    // Действие:
    useCase.getOrderDecisionTimeout().test();
    useCase.getOrderDecisionTimeout().test();

    // Результат:
    verify(orderUseCase, times(2)).getOrders();
    verifyNoMoreInteractions(orderUseCase);
  }

  /* Проверяем работу с юзкейсом принятия решения по заказу */

  /**
   * Не должно ничего сломаться, если юзкейса принятия решения нет.
   */
  @Test
  public void shouldNotCrashIfNoDecisionUseCaseSet() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order).concatWith(Flowable.never()));
    when(orderConfirmationGateway.sendDecision(order, false)).thenReturn(Single.just("success"));
    when(orderConfirmationGateway.sendDecision(order, true)).thenReturn(Single.just("success"));
    useCase = new OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway, null,
        ordersUseCase);

    // Действие:
    useCase.sendDecision(false).test();
    useCase.sendDecision(true).test();

    // Результат:
    verifyZeroInteractions(orderDecisionUseCase);
  }

  /**
   * Должен запросить у юзкейса заказа деактуализацию заказов.
   */
  @Test
  public void askOrderDecisionUseCaseToSetCurrentOrderExpired() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order).concatWith(Flowable.never()));
    when(orderConfirmationGateway.sendDecision(any(), anyBoolean())).thenReturn(Single.just(""));

    // Действие:
    useCase.sendDecision(true).test();
    useCase.sendDecision(false).test();

    // Результат:
    verify(orderDecisionUseCase, times(2)).setOrderOfferDecisionMade();
    verifyNoMoreInteractions(orderDecisionUseCase);
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
  public void askGatewayToSendDecisionsForFirstOrderOnly() {
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
  public void cancelGatewayToSendDecisionsForNextOrder() throws Exception {
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

  /**
   * Должен отменить запрос у гейтвея на передачу решений если заказ истек.
   */
  @Test
  public void cancelGatewayToSendDecisionsForOrderExpired() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(orderConfirmationGateway, action);
    when(orderUseCase.getOrders()).thenReturn(
        Flowable.just(order).concatWith(Flowable.error(new OrderOfferExpiredException(""))));
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

  /* Проверяем работу с юзкейсом списка заказов */

  /**
   * Не должно ничего сломаться, если юзкейса заказов нет.
   */
  @Test
  public void shouldNotCrashIfNoUseCaseSet() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order).concatWith(Flowable.never()));
    when(orderConfirmationGateway.sendDecision(order, false)).thenReturn(Single.just("success"));
    when(orderConfirmationGateway.sendDecision(order, true)).thenReturn(Single.just("success"));
    useCase = new OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway,
        orderDecisionUseCase, null);

    // Действие:
    useCase.sendDecision(false).test();
    useCase.sendDecision(true).test();

    // Результат:
    verifyZeroInteractions(ordersUseCase);
  }

  /**
   * Должен передать юзкейсу успешно отвергнутый заказа.
   */
  @Test
  public void passRefusedOrderToOrdersUseCase() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order).concatWith(Flowable.never()));
    when(orderConfirmationGateway.sendDecision(order, false)).thenReturn(Single.just("success"));

    // Действие:
    useCase.sendDecision(false).test();

    // Результат:
    verify(ordersUseCase, only()).removeOrder(order);
  }

  /**
   * Должен передать юзкейсу успешно принятый заказа.
   */
  @Test
  public void passConfirmedOrderToOrdersUseCase() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order).concatWith(Flowable.never()));
    when(orderConfirmationGateway.sendDecision(order, true)).thenReturn(Single.just("success"));

    // Действие:
    useCase.sendDecision(true).test();

    // Результат:
    verify(ordersUseCase, only()).addOrder(order);
  }

  /**
   * Не должен трогать юзкейс если пришел новый заказ.
   */
  @Test
  public void doNotTouchOrdersUseCaseIfNextOrder() {
    // Дано:
    when(orderUseCase.getOrders())
        .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()));

    // Действие:
    useCase.sendDecision(true).test();

    // Результат:
    verifyZeroInteractions(ordersUseCase);
  }

  /**
   * Не должен трогать юзкейс если заказ истек.
   */
  @Test
  public void doNotTouchOrdersUseCaseIfOrderExpired() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(
        Flowable.just(order).concatWith(Flowable.error(new OrderOfferExpiredException(""))));

    // Действие:
    useCase.sendDecision(true).test();

    // Результат:
    verifyZeroInteractions(ordersUseCase);
  }

  /* Проверяем ответы на запрос отправки решения */

  /**
   * Должен ответить ошибкой маппинга на запрос таймаутов.
   */
  @Test
  public void answerDataMappingErrorForGetTimeouts() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Pair<Long, Long>> test = useCase.getOrderDecisionTimeout().test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой не актуальности заказа на запрос таймаутов.
   */
  @Test
  public void answerOrderExpiredErrorForGetTimeoutsIfErrorAfterValue() {
    // Дано:
    when(order.getId()).thenReturn(101L);
    when(order.getTimeout()).thenReturn(12345L);
    when(orderUseCase.getOrders()).thenReturn(
        Flowable.just(order).concatWith(Flowable.error(new OrderOfferExpiredException("")))
    );

    // Действие:
    TestSubscriber<Pair<Long, Long>> test = useCase.getOrderDecisionTimeout().test();

    // Результат:
    test.assertError(OrderOfferExpiredException.class);
    test.assertValue(new Pair<>(101L, 12345L));
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой не актуальности заказа на подтверждение.
   */
  @Test
  public void answerWithTimeoutsForGetTimeouts() {
    // Дано:
    when(order.getId()).thenReturn(101L, 202L);
    when(order.getTimeout()).thenReturn(12345L, 54321L);
    when(order2.getId()).thenReturn(303L);
    when(order2.getTimeout()).thenReturn(34543L);
    when(orderUseCase.getOrders())
        .thenReturn(Flowable.just(order, order2, order).concatWith(Flowable.never()));

    // Действие:
    TestSubscriber<Pair<Long, Long>> test = useCase.getOrderDecisionTimeout().test();

    // Результат:
    test.assertNoErrors();
    test.assertValueCount(3);
    test.assertValueAt(0, new Pair<>(101L, 12345L));
    test.assertValueAt(1, new Pair<>(303L, 34543L));
    test.assertValueAt(2, new Pair<>(202L, 54321L));
    test.assertNotComplete();
  }

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
  public void answerOrderExpiredErrorForAcceptIfSecondValue() {
    // Дано:
    when(orderUseCase.getOrders())
        .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()));

    // Действие:
    TestObserver<String> test = useCase.sendDecision(true).test();

    // Результат:
    test.assertError(OrderOfferDecisionException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой не актуальности заказа на подтверждение.
   */
  @Test
  public void answerOrderExpiredErrorForAcceptIfErrorAfterValue() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(
        Flowable.just(order).concatWith(Flowable.error(new OrderOfferExpiredException("")))
    );

    // Действие:
    TestObserver<String> test = useCase.sendDecision(true).test();

    // Результат:
    test.assertError(OrderOfferExpiredException.class);
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