package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderCurrentCostUseCaseTest {

  private OrderCurrentCostUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private OrderGateway orderGateway;
  @Mock
  private DataReceiver<String> loginReceiver;
  @Mock
  private OrderCurrentCostGateway orderCurrentCostGateway;
  @Mock
  private Order order;
  @Mock
  private Order order2;

  @Before
  public void setUp() {
    when(loginReceiver.get()).thenReturn(Observable.never());
    when(orderGateway.getOrders(anyString())).thenReturn(Flowable.never());
    when(orderCurrentCostGateway.getOrderCurrentCost(anyString())).thenReturn(Flowable.never());
    useCase = new OrderCurrentCostUseCaseImpl(errorReporter, orderGateway, loginReceiver,
        orderCurrentCostGateway);
  }

  /* Проверяем работу с публикатором логина */

  /**
   * Не должен запрашивать у публикатора логин исполнителя, если не было сброса.
   */
  @Test
  public void doNotTouchLoginPublisherWithoutReset() {
    // Действие:
    useCase.getOrderCurrentCost().test();
    useCase.getOrderCurrentCost().test();
    useCase.getOrderCurrentCost().test();

    // Результат:
    verify(loginReceiver, times(3)).get();
    verifyNoMoreInteractions(loginReceiver);
  }

  /* Проверяем работу с гейтвеем заказа */

  /**
   * Должен запросить у гейтвея получение выполняемого заказа.
   */
  @Test
  public void askOrderGatewayForOrders() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    useCase.getOrderCurrentCost().test();

    // Результат:
    verify(orderGateway).getOrders("1234567890");
    verify(orderGateway).getOrders("0987654321");
    verify(orderGateway).getOrders("123454321");
    verify(orderGateway).getOrders("09876567890");
    verifyNoMoreInteractions(orderGateway);
  }

  /* Проверяем работу с гейтвеем текущей цены заказа */

  /**
   * Не должен трогать гейтвея, пока не было заказа.
   */
  @Test
  public void doNotTouchCurrentCostGateway() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));

    // Действие:
    useCase.getOrderCurrentCost().test();

    // Результат:
    verifyZeroInteractions(orderCurrentCostGateway);
  }

  /**
   * Должен сообщить гейтвею о начале погрузки.
   */
  @Test
  public void askCurrentCostGatewayForCostUpdates() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(orderGateway.getOrders("1234567890")).thenReturn(Flowable.just(order));

    // Действие:
    useCase.getOrderCurrentCost().test();

    // Результат:
    verify(orderCurrentCostGateway, only()).getOrderCurrentCost("1234567890");
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку маппинга.
   */
  @Test
  public void reportDataMappingError() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(orderGateway.getOrders("1234567890"))
        .thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    useCase.getOrderCurrentCost().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить ошибку маппинга.
   */
  @Test
  public void reportDataMappingErrorInCurrentCost() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(orderGateway.getOrders("1234567890"))
        .thenReturn(Flowable.just(order, order2));
    when(order.getTotalCost()).thenReturn(101L);
    when(orderCurrentCostGateway.getOrderCurrentCost("1234567890"))
        .thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    useCase.getOrderCurrentCost().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем ответы на запрос цены заказа */

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(orderGateway.getOrders("1234567890"))
        .thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Long> test = useCase.getOrderCurrentCost().test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingErrorInCurrentCost() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(orderGateway.getOrders("1234567890"))
        .thenReturn(Flowable.just(order, order2));
    when(order.getTotalCost()).thenReturn(101L);
    when(orderCurrentCostGateway.getOrderCurrentCost("1234567890"))
        .thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Long> test = useCase.getOrderCurrentCost().test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertValue(101L);
    test.assertNotComplete();
  }

  /**
   * Должен ответить ценами только из заказов.
   */
  @Test
  public void answerWithOrdersCostsOnly() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(orderGateway.getOrders("1234567890"))
        .thenReturn(Flowable.just(order, order2));
    when(order.getTotalCost()).thenReturn(110L);
    when(order2.getTotalCost()).thenReturn(12173L);

    // Действие:
    TestSubscriber<Long> test = useCase.getOrderCurrentCost().test();

    // Результат:
    test.assertValues(110L, 12173L);
    test.assertNotComplete();
    test.assertNoErrors();
  }

  /**
   * Должен ответить ценами из заказов и обновлениями цен.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithOrdersAndUpdatedCosts() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(orderGateway.getOrders("1234567890"))
        .thenReturn(Flowable.just(order, order2));
    when(order.getTotalCost()).thenReturn(100L);
    when(order2.getTotalCost()).thenReturn(12173L);
    when(orderCurrentCostGateway.getOrderCurrentCost("1234567890")).thenReturn(
        Flowable.just(123L, 145L, 139L, 198L, 202L),
        Flowable.just(8395L, 8937L, 17156L, 9228L)
    );

    // Действие:
    TestSubscriber<Long> test = useCase.getOrderCurrentCost().test();

    // Результат:
    test.assertValues(100L, 123L, 145L, 139L, 198L, 202L, 12173L, 8395L, 8937L, 17156L, 9228L);
    test.assertComplete();
    test.assertNoErrors();
  }
}